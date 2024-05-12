package net.yx.ninjago.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.yx.ninjago.world.entity.projectile.BoomerangEntity;

public class BommerangItem extends Item {

    public BommerangItem() {
        super(new Properties().setNoRepair().defaultDurability(300));
    }

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack offHandItem = pPlayer.getItemInHand(InteractionHand.OFF_HAND);
		if (pUsedHand == InteractionHand.OFF_HAND && offHandItem.is(this)) {
			doShoot(pPlayer, offHandItem, pUsedHand);
			return InteractionResultHolder.consume(offHandItem);
		}
		return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
	}

	private void doShoot(Player player, ItemStack stack, InteractionHand hand) {
		if (!player.level().isClientSide) {
			stack.hurtAndBreak(10, player, p -> p.broadcastBreakEvent(hand));
			BoomerangEntity boomerangEntity = new BoomerangEntity(player.level(), player);
            boomerangEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3F, 1.0F);
			
			player.level().addFreshEntity(boomerangEntity);
			player.getInventory().removeItem(stack);
		}
	}
}
