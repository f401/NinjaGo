package net.yx.ninjago.world.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;

public class LongSwordItem extends SwordItem {


	public LongSwordItem() {
		super(new NinjaGoTier(), 6, -2.4F, new Item.Properties().setNoRepair().durability(250));
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
		Iterable<ItemStack> iterable = entity.getArmorSlots();
		if (!entity.level().isClientSide && (iterable != null && entity instanceof LivingEntity livingEntity)) {
			DamageSource fakeDamageSource = entity.damageSources().playerAttack(player);
			// 检查盾牌
			if (livingEntity instanceof Player targetPlayer && 
			    !targetPlayer.isDamageSourceBlocked(fakeDamageSource)) {
				// 原版会帮我们处理盾牌耐久等逻辑，我们只需要做到不扣除盔甲耐久
				return false;
			}

			class Helper {
				int i = 0;
			};
			
			Helper helper = new Helper();

			for (ItemStack armor: iterable) {
				if (armor != null && !armor.isEmpty()) {
					int durability = armor.getMaxDamage() - armor.getDamageValue();
					armor.hurtAndBreak((int) (durability * 0.5f), livingEntity, (wearer) -> {
						wearer.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, helper.i));
					});
				}
				helper.i += 1;
			}
		
		}
		// 继续执行
		return false;
	}

} 
