package net.yx.ninjago.registries;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.yx.ninjago.NinjaGo;
import net.yx.ninjago.world.item.BoomerangItem;
import net.yx.ninjago.world.item.HeavyHammer;
import net.yx.ninjago.world.item.LongSwordItem;

public class ItemRegistry {
	public static final DeferredRegister<Item> ITEM_REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, NinjaGo.MOD_ID);

	public static final RegistryObject<LongSwordItem> LONG_SWORD = ITEM_REGISTER.register("long_sword", LongSwordItem::new);
	public static final RegistryObject<HeavyHammer> HEAVY_HAMMAR = ITEM_REGISTER.register("heavy_hammer", HeavyHammer::new);
	public static final RegistryObject<BoomerangItem> BOOMERANG  = ITEM_REGISTER.register("boomerang", BoomerangItem::new);
}
