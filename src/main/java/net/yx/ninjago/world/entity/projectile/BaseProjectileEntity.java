package net.yx.ninjago.world.entity.projectile;

import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
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

public class BaseProjectileEntity extends AbstractArrow {
    private static final EntityDataAccessor<Vector3f> shootPosition = SynchedEntityData
            .defineId(BaseProjectileEntity.class, EntityDataSerializers.VECTOR3);

    protected boolean dealtDamage;
    @Nullable
    private BlockState oldBlockState;

    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> type, Level pLevel, Player pShooter) {
        super(type, pLevel);

        this.moveTo(pShooter.getX(), pShooter.getEyeY(), pShooter.getZ(), pShooter.getYRot(), pShooter.getXRot());
        this.setOwner(pShooter);
        this.pickup = pShooter.isCreative() ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        this.dealtDamage = false;
        entityData.set(shootPosition,
                new Vector3f((float) pShooter.getX(), (float) pShooter.getEyeY(), (float) pShooter.getZ()));
    }

    public BaseProjectileEntity(EntityType<? extends BaseProjectileEntity> type, Level pLevel, Player pShooter,
            float speed, float inc) {
        this(type, pLevel, pShooter);

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
        super.defineSynchedData();
        this.entityData.define(shootPosition, Vec3.ZERO.toVector3f());
    }

    public Vec3 getShootPosition() {
        return new Vec3(entityData.get(shootPosition));
    }

    public Vector3f getShootPositionF() {
        return entityData.get(shootPosition);
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

    @Override
    public void tick() {
        this.baseTick();

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

    @Override
    public boolean isAttackable() {
        return false;
    }

    protected void tickInGround(BlockState blockState) {
        if (oldBlockState != blockState && this.shouldFall()) {
            this.startFalling();
        } else if (!this.level().isClientSide) {
            tickDespawn();
        }
        ++this.inGroundTime;
    }

    protected void tickInAir() {
        this.inGroundTime = 0;
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

    /**
     * Based on {@linkplain AbstractArrow#onHitEntity}
     */
    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        this.dealtDamage = true;
        Entity entity = pResult.getEntity(), owner = getOwner();
        DamageSource source = damageSources().magic();
        if (entity.hurt(source, getDamage())) {
            if (entity.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (entity instanceof LivingEntity) {
                LivingEntity livingentity1 = (LivingEntity) entity;
                if (owner instanceof LivingEntity) {
                    EnchantmentHelper.doPostHurtEffects(livingentity1, owner);
                    EnchantmentHelper.doPostDamageEffects((LivingEntity) owner, livingentity1);
                }

                this.doPostHurtEffects(livingentity1);
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        this.oldBlockState = this.level().getBlockState(pResult.getBlockPos());
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
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        if (this.oldBlockState != null) {
            pCompound.put("inBlockState", NbtUtils.writeBlockState(this.oldBlockState));
        }

        pCompound.putBoolean("dealtDamage", this.dealtDamage);
        CompoundTag shoot = new CompoundTag();
        Vector3f shootPosition = getShootPositionF();
        shoot.putFloat("x", shootPosition.x);
        shoot.putFloat("y", shootPosition.y);
        shoot.putFloat("z", shootPosition.z);
        pCompound.put("shootPosition", shoot);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("inBlockState", Tag.TAG_COMPOUND)) {
            this.oldBlockState = NbtUtils.readBlockState(this.level().holderLookup(Registries.BLOCK),
                    pCompound.getCompound("inBlockState"));
        }

        this.dealtDamage = pCompound.getBoolean("dealtDamage");

        CompoundTag shoot = pCompound.getCompound("shootPosition");
        entityData.set(shootPosition, new Vector3f(shoot.getFloat("x"), shoot.getFloat("y"), shoot.getFloat("z")));
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
    protected ItemStack getPickupItem() {
        return Items.ARROW.getDefaultInstance();
    }

    public final boolean isInGround() {
        return this.inGround;
    }

}
