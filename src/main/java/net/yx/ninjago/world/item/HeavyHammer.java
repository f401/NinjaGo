package net.yx.ninjago.world.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.yx.ninjago.NinjaGo;

public class HeavyHammer extends TieredItem {

	public HeavyHammer() {
		super(new NinjaGoTier(), new Item.Properties().defaultDurability(250).defaultDurability(375));
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		ItemStack mainHand = pPlayer.getMainHandItem();
		if (pUsedHand == InteractionHand.MAIN_HAND) {
			if (!pLevel.isClientSide) {
				AABB areaAABB = new AABB(pPlayer.blockPosition()).deflate(2, 1, 2);
				pLevel.getEntities(pPlayer, areaAABB)
					.stream()
					.filter((t) -> (t instanceof LivingEntity))
					.map((t) -> ((LivingEntity) t)).toList()
					.forEach((t) -> knockbackEntity(pPlayer, t));

			}
			pPlayer.getCooldowns().addCooldown(this, (int) (NinjaGo.TICKS_PER_SECONDS * 2.5));
			return InteractionResultHolder.success(mainHand);
		}
		return InteractionResultHolder.pass(mainHand);
	}

	private static void knockbackEntity(Player sourcePlayer, LivingEntity targetEntity) {
		Vec3 distance = targetEntity.position().vectorTo(sourcePlayer.position());
		targetEntity.knockback(5D, distance.x, distance.z);
	}
}
