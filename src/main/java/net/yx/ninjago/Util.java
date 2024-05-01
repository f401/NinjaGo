package net.yx.ninjago;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Util {

	/**
	 * 如果 {@code sourcePlayer} 和 {@code targetEntity}皆为{@linkplain Player}，
	 * 且{@code targetEntity}正在使用的盾牌可以阻挡来自{@code sourcePlayer}的攻击伤害，
	 * 则返回true。
	 * 以上条件不满足其一，则返回false。
	 */
	public static boolean canShieldBlockPlayerAttack(Entity sourcePlayer, Entity targetEntity) {
		return sourcePlayer instanceof Player player &&
			canShieldBlockDamage(targetEntity, targetEntity.damageSources().playerAttack(player));
	}
		
	/**
	 * 如果{@code targetEntity}为{@linkplain Player},
	 * 且可以用正在使用盾牌阻挡来自{@code source}的伤害，返回true
	 */
	public static boolean canShieldBlockDamage(Entity targetEntity, DamageSource source) {
		if (targetEntity instanceof Player player) {
			return player.isDamageSourceBlocked(source);
		}
		return false;
	}
}
