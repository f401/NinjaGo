package net.yx.ninjago.world.entity.projectile;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.AbstractArrow.Pickup;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BaseProjectileEntity extends Projectile {

    private AbstractArrow.Pickup pickup;
    public int shakeTime;
    private boolean inGround;

    public BaseProjectileEntity(EntityType<? extends AbstractHurtingProjectile> pEntityType, Player pShooter,
            Level pLevel, float speed, float inc) {
        super(pEntityType, pLevel);

        this.moveTo(pShooter.getX(), pShooter.getY(), pShooter.getZ(), pShooter.getYRot(), pShooter.getXRot());
        this.setOwner(pShooter);
        this.pickup = pShooter.isCreative() ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        this.inGround = false;

        float xRotRad = this.getXRot() / 180 * (float) Math.PI;
        float yRotRad = this.getYRot() / 180 * (float) Math.PI;
        double cosXRot = Mth.cos(xRotRad);
        double x = -Mth.sin(yRotRad) * cosXRot;
        double z = Mth.cos(yRotRad) * cosXRot;
        double y = -Mth.sin(xRotRad);

        this.shoot(x, y, z, speed, inc);
    }

    @Override
    public boolean isAttackable() {
        return false;
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

        BlockPos currentBlockPos = this.blockPosition();
        BlockState blockState = this.level().getBlockState(currentBlockPos);
        if (!blockState.isAir() && !isNoPhysics) {
            VoxelShape vShape = blockState.getCollisionShape(this.level(), currentBlockPos);
            if (!vShape.isEmpty()) {
                Vec3 currentPosition = this.position();
                for (AABB box : vShape.toAabbs()) {
                    if (box.move(currentBlockPos).contains(currentPosition)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }
        if (this.isInWaterOrRain() || blockState.is(Blocks.POWDER_SNOW)
                || this.isInFluidType((fluidType, height) -> this.canFluidExtinguish(fluidType))) {
            this.clearFire();
        }
    }

}
