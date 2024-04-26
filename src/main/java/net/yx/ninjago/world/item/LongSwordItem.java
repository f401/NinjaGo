package net.yx.ninjago.world.item;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.damagesource.DamageSource;
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

	private static final Logger LOGGER = LogManager.getLogger();

	public LongSwordItem() {
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
				return 0;
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
		//LOGGER.info("Triggerd left click {}", iterable);
		if (!entity.level().isClientSide && (iterable != null && entity instanceof LivingEntity livingEntity)) {
			DamageSource fakeDamageSource = entity.damageSources().playerAttack(player);
			// 检查盾牌
			if (livingEntity instanceof Player targetPlayer && 
			    !targetPlayer.isDamageSourceBlocked(fakeDamageSource)) {
				return false;
			}

			class Helper {
				int i = 0;
			};
			
			Helper helper = new Helper();

			for (ItemStack armor: iterable) {
				if (armor != null && !armor.isEmpty()) {
					int durability = armor.getMaxDamage() - armor.getDamageValue();
					//LOGGER.info("Reduce half, current {}", durability);
					armor.hurtAndBreak((int) (durability * 0.5f), livingEntity, (wearer) -> {
						wearer.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, helper.i));
						//LOGGER.info("item break down");
					});
				}
				helper.i += 1;
			}
		
		}
		// 继续执行
		return false;
	}

} 
