package net.yx.ninjago.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.yx.ninjago.NinjaGo;

public class CreativeModeTabRegistry {
	public static final DeferredRegister<CreativeModeTab> MODE_TAB_REGISTER = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NinjaGo.MOD_ID);
	public static final RegistryObject<CreativeModeTab> NINJAGO_TAB = MODE_TAB_REGISTER.register("ninjago_creative_mode_tab", () -> CreativeModeTab.builder()
	.hideTitle()
	.withTabsBefore(CreativeModeTabs.COMBAT)
	.icon(() -> Items.DIAMOND.getDefaultInstance())
	.displayItems((params, output) -> {
		output.accept(ItemRegistry.LONG_SWORD.get());
	}).build());
}
