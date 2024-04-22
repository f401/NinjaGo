package net.yx.ninjago.world.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public class LongSwordItem extends SwordItem {

	public LongSwordItem(Tier pTier) {
		super(new Tier() {

			@Override
			public int getUses() {
				return 0;
			}

			@Override
			public float getSpeed() {
				return 0;
			}

			@Override
			public float getAttackDamageBonus() {
				return 0];
			}

			@Override
			public int getLevel() {
				return 3;
			}

			@Override
			public int getEnchantmentValue() {
				return 15;
			}

			@Override
			public Ingredient getRepairIngredient() {
				return Ingredient.EMPTY;
			}
		}, 6, -2.4F, new Item.Properties().setNoRepair().durability(250));
		
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		Iterable<ItemStack> iterable = entity.getArmorSlots();
		if (iterable != null && entity instanceof LivingEntity livingEntity) {
			IntHelper helper = new IntHelper();
			helper.value = 0;
			for (ItemStack armor: iterable) {
				if (player.getRandom().nextFloat() < 0.04F) {
					armor.hurtAndBreak(armor.getDamageValue() / 2, livingEntity, (armorWearer) -> {
						livingEntity.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, helper.value));
					});
				}
				helper.value += 1;
			};
		}
		// 继续执行
		return false;
	}

	private static class IntHelper {
		int value;
	}

} 
