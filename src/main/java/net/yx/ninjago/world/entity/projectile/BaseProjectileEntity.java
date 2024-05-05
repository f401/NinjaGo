package net.yx.ninjago.world.entity.projectile;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseProjectileEntity extends Projectile {

    private AbstractArrow.Pickup pickup;
    @Nullable
    private BlockState oldBlockState;
    private boolean inGround;
    protected boolean dealtDamage;
    private float knockback;
    public int shakeTime;
    public int inGroundTicks;

    public BaseProjectileEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Player pShooter,
            Level pLevel, float speed, float inc) {
        super(pEntityType, pLevel);

        this.moveTo(pShooter.getX(), pShooter.getY(), pShooter.getZ(), pShooter.getYRot(), pShooter.getXRot());
        this.setOwner(pShooter);
        this.pickup = pShooter.isCreative() ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        this.inGround = false;
        this.dealtDamage = false;
        this.inGroundTicks = 0;

        float xRotRad = this.getXRot() / 180 * (float) Math.PI;
        float yRotRad = this.getYRot() / 180 * (float) Math.PI;
        double cosXRot = Mth.cos(xRotRad);
        double x = -Mth.sin(yRotRad) * cosXRot;
        double z = Mth.cos(yRotRad) * cosXRot;
        double y = -Mth.sin(xRotRad);

        this.shoot(x, y, z, speed, inc);
    }

    @Override
    protected void defineSynchedData() {
    }

    /**
     * Same as {@linkplain AbstractArrow#shouldRenderAtSqrDistance}
     */
    @Override
    public boolean shouldRenderAtSqrDistance(double pDistance) {
        double d0 = this.getBoundingBox().getSize() * 10.0D;
        if (Double.isNaN(d0)) {
            d0 = 1.0D;
        }

        d0 *= 64.0D * getViewScale();
        return pDistance < d0 * d0;
    }

    protected void tickInGround(BlockState blockState) {
        if (oldBlockState != blockState && this.shouldFall()) {
            this.startFalling();
        }
        ++this.inGroundTicks;
    }

    protected void tickInAir() {
        this.inGroundTicks = 0;
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);

        if (hitresult.getType() == HitResult.Type.ENTITY) {
            Entity targetEntity = ((EntityHitResult) hitresult).getEntity();
            Entity owner = getOwner();
            if (targetEntity instanceof Player targetPlayer && owner instanceof Player ownerPlayer
                    && !ownerPlayer.canHarmPlayer(targetPlayer)) {
                hitresult = null;
            }
        }

        if (hitresult != null && hitresult.getType() != HitResult.Type.MISS
                && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) { // 尊重Forge的事件系统
            this.onHit(hitresult);
            this.hasImpulse = true;
        }
        Vec3 deltaMovement = getDeltaMovement();
        Vec3 position = position();

        ProjectileUtil.rotateTowardsMovement(this, 0.2F);
        // 生成在水中的粒子效果
        if (this.isInWater()) {
            for (int j = 0; j < 4; ++j) {
                this.level().addParticle(ParticleTypes.BUBBLE, position.x - deltaMovement.x * 0.25D,
                        position.y - deltaMovement.y * 0.25D,
                        position.z - deltaMovement.z * 0.25D,
                        deltaMovement.x, deltaMovement.y, deltaMovement.z);
            }
        }

        this.setPos(deltaMovement.add(position));

        // 为下一个tick的move准备
        deltaMovement = deltaMovement.scale(getInteria());
        if (!this.isNoGravity() && !noPhysics) {
            deltaMovement = new Vec3(deltaMovement.x, deltaMovement.y - getGravity(), deltaMovement.z);
        }
        this.setDeltaMovement(deltaMovement);

        this.checkInsideBlocks();
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 deltaMovement = this.getDeltaMovement();
        boolean isNoPhysics = this.noPhysics;// 判断是否要让碰撞框生效

        if (this.shakeTime > 0) {
            --this.shakeTime;
        }

        // If we don't have our rotation set correctly, infer it from our motion
        // direction
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            double f = deltaMovement.horizontalDistance();
            this.yRotO = (float) (Math.atan2(deltaMovement.x, deltaMovement.z) * 180.0D / Math.PI);
            this.xRotO = (float) (Math.atan2(deltaMovement.y, f) * 180.0D / Math.PI);
            this.setXRot(xRotO);
            this.setYRot(yRotO);
        }

        // 检查是否碰撞到了方块
        BlockPos currentBlockPos = this.blockPosition();
        BlockState blockState = this.level().getBlockState(currentBlockPos);
        Vec3 position = this.position();
        if (!blockState.isAir() && !isNoPhysics) {
            VoxelShape vShape = blockState.getCollisionShape(this.level(), currentBlockPos);
            if (!vShape.isEmpty()) {
                for (AABB box : vShape.toAabbs()) {
                    if (box.move(currentBlockPos).contains(position)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        // 在水中或细雪中，去除当前火焰效果(如果有)
        if (this.isInWaterOrRain() || blockState.is(Blocks.POWDER_SNOW)
                || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }

        if (this.inGround && !isNoPhysics) {
            tickInGround(blockState);
        } else {
            tickInAir();
        }
    }

    private boolean shouldFall() {
        return this.inGround && this.level().noCollision(new AABB(position(), position()).inflate(0.6D));
    }

    private void startFalling() {
        this.inGround = false;
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.multiply((double) (this.random.nextFloat() * 0.2F),
                (double) (this.random.nextFloat() * 0.2F), (double) (this.random.nextFloat() * 0.2F)));
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        this.oldBlockState = this.level().getBlockState(pResult.getBlockPos());
        super.onHitBlock(pResult);
        Vec3 vec3 = pResult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
        this.setDeltaMovement(vec3);
        Vec3 vec31 = vec3.normalize().scale((double) 0.05F);
        this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
        this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        this.inGround = true;
        this.shakeTime = 7;

    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        Entity targetEntity = pResult.getEntity();
        Entity owner = this.getOwner();
        DamageSource damagesource = damageSources().magic();
        if (owner instanceof LivingEntity) {
            ((LivingEntity) owner).setLastHurtMob(targetEntity);
        }
        this.dealtDamage = true;

        boolean isEnderman = targetEntity.getType() == EntityType.ENDERMAN;
        if (this.isOnFire() && !isEnderman) {
            targetEntity.setSecondsOnFire(5);
        }

        if (targetEntity.hurt(damagesource, (float) getDamage())) {
            if (isEnderman) {
                return;
            }

            if (targetEntity instanceof LivingEntity) {
                LivingEntity targetLiving = (LivingEntity) targetEntity;
                // 处理击退
                if (this.knockback > 0) {
                    double d0 = Math.max(0.0D, 1.0D -
                            targetLiving.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                    Vec3 vec3 = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize()
                            .scale((double) this.knockback * 0.6D * d0);
                    if (vec3.lengthSqr() > 0.0D) {
                        targetLiving.push(vec3.x, 0.1D, vec3.z);
                    }
                }

                // 使附魔生效
                if (!this.level().isClientSide && owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(targetLiving, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, targetLiving);
                }

                // 通知客户端
                if (targetLiving != owner && targetLiving instanceof Player
                        && owner instanceof ServerPlayer && !this.isSilent()) {
                    ((ServerPlayer) owner).connection
                            .send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }
            }

            this.playSound(getHitEntitySoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
        } else {
            if (!this.level().isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
                if (this.pickup == AbstractArrow.Pickup.ALLOWED) {
                    this.spawnAtLocation(this.getPickupItem(), 0.1F);
                }

                this.discard();
            }
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
        }
    }

    /**
     * Same as {@linkplain AbstractArrow#playerTouch}
     */
    @Override
    public void playerTouch(Player pPlayer) {
        if (!this.level().isClientSide && (this.inGround || this.noPhysics && this.shakeTime <= 0)) {
            if (this.tryPickup(pPlayer)) {
                pPlayer.take(this, 1);
                this.discard();
            }

        }
    }


    /**
     * Same as {@linkplain AbstractArrow#tryPickup}
     */
    protected boolean tryPickup(Player pPlayer) {
        switch (this.pickup) {
            case ALLOWED:
                return pPlayer.getInventory().add(this.getPickupItem());
            case CREATIVE_ONLY:
                return pPlayer.getAbilities().instabuild;
            default:
                return false;
        }
    }

    protected ItemStack getPickupItem() {
        return Items.ARROW.getDefaultInstance();
    }

    public float getKnockback() {
        return knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = knockback;
    }

    protected SoundEvent getHitGroundSoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected SoundEvent getHitEntitySoundEvent() {
        return SoundEvents.ARROW_HIT;
    }

    protected int getDamage() {
        return 0;
    }

    protected float getInteria() {
        return 0.95F;
    }

    protected float getGravity() {
        return 0.01F;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

}
