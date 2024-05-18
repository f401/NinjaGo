package net.yx.ninjago.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.yx.ninjago.registries.EntityTypeRegistry;
import net.yx.ninjago.registries.ItemRegistry;

public class BoomerangEntity extends BaseProjectileEntity {

	private ItemStack pickupItem = ItemRegistry.BOOMERANG.get().getDefaultInstance();

    // client renderer only
    public int spin;

	public BoomerangEntity(EntityType<? extends BoomerangEntity> entityType, Level pLevel) {
		super(entityType, pLevel);
        this.spin = 0;
	}

	public BoomerangEntity(Level pLevel, Player shooter) {
		super(EntityTypeRegistry.BOOMERANG.get(), pLevel, shooter);
        this.spin = 0;
	}

	public BoomerangEntity(Level pLevel, Player shooter, float speed, float inc) {
		super(EntityTypeRegistry.BOOMERANG.get(), pLevel, shooter, speed, inc);
        this.spin = 0;
	}

	@Override
	public void tick() {
		if (this.inGroundTime > 5 || this.getShootPosition().distanceToSqr(getPosition(1)) > 1024) {
			this.dealtDamage = true;
		}

		Entity owner = this.getOwner();
		if ((this.dealtDamage || this.noPhysics) && owner != null) {
			if (!isAcceptibleReturnOwner()) {
				if (!this.level().isClientSide && this.pickup == AbstractArrow.Pickup.ALLOWED) {
					this.spawnAtLocation(this.getPickupItem(), 0.1F);
				}
				this.discard();
			} else {
				// Do return to owner
				this.noPhysics = true;
				Vec3 distance = owner.getEyePosition().subtract(this.position());
				this.setPosRaw(this.getX(), this.getY() + distance.y * 0.015D, this.getZ());
				if (this.level().isClientSide) {
					this.yOld = this.getY();
				}
				this.setDeltaMovement(this.getDeltaMovement().scale(0.95D).add(distance.normalize().scale(0.05D)));
			}
		}
		super.tick();
	}

	@Override
	public boolean shouldRender(double pX, double pY, double pZ) {
		return true;
	}

	@Override
	protected boolean canHitEntity(Entity pTarget) {
		return (!this.dealtDamage) && super.canHitEntity(pTarget);
	}

	private boolean isAcceptibleReturnOwner() {
		Entity entity = this.getOwner();
		if (entity != null && entity.isAlive()) {
			return !(entity instanceof ServerPlayer) || !entity.isSpectator();
		} else {
			return false;
		}
	}

	@Override
	protected boolean tryPickup(Player pPlayer) {
		return super.tryPickup(pPlayer)
				|| this.noPhysics && this.ownedBy(pPlayer) && pPlayer.getInventory().add(this.getPickupItem());
	}

	@Override
	public void playerTouch(Player pPlayer) {
		if (this.ownedBy(pPlayer) || this.getOwner() == null) {
			super.playerTouch(pPlayer);
		}
	}

	@Override
	protected ItemStack getPickupItem() {
		return pickupItem;
	}

	@Override
	protected int getDamage() {
		return 5;
	}

	@Override
	public void addAdditionalSaveData(CompoundTag pCompound) {
		super.addAdditionalSaveData(pCompound);
		pCompound.put("Boomerang", pickupItem.save(new CompoundTag()));
	}

	@Override
	public void readAdditionalSaveData(CompoundTag pCompound) {
		super.readAdditionalSaveData(pCompound);
		if (pCompound.contains("Bommerang", Tag.TAG_COMPOUND)) {
			this.pickupItem = ItemStack.of(pCompound.getCompound("Bommerang"));
		}
	}

}
