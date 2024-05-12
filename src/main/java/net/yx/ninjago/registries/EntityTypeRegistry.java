package net.yx.ninjago.registries;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yx.ninjago.NinjaGo;
import net.yx.ninjago.world.entity.projectile.BoomerangEntity;

public class EntityTypeRegistry {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPE = DeferredRegister
			.create(ForgeRegistries.ENTITY_TYPES, NinjaGo.MOD_ID);

	public static final RegistryObject<EntityType<BoomerangEntity>> BOOMERANG = ENTITY_TYPE.register("boomerang_entity",
			() -> EntityType.Builder.<BoomerangEntity>of(BoomerangEntity::new, MobCategory.MISC).sized(0.5f, 0.5f)
					.clientTrackingRange(4).updateInterval(20).build("boomerang_entity"));
}
