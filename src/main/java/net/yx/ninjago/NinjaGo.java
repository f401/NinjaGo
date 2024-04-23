package net.yx.ninjago;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yx.ninjago.registries.*;

@Mod(NinjaGo.MOD_ID)
public class NinjaGo {
	public static final String MOD_ID = "ninjago";
	
	public NinjaGo() {
		ItemRegistry.ITEM_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
		CreativeModeTabRegistry.MODE_TAB_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
