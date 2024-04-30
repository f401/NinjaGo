package net.yx.ninjago;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yx.ninjago.registries.*;

@Mod(NinjaGo.MOD_ID)
public class NinjaGo { 
	public static final String MOD_ID = "ninjago";
	public static final int TICKS_PER_SECONDS = 20;
	
	public NinjaGo() {
		IEventBus fmlBus = FMLJavaModLoadingContext.get().getModEventBus();
		ItemRegistry.ITEM_REGISTER.register(fmlBus);
		CreativeModeTabRegistry.MODE_TAB_REGISTER.register(fmlBus);
	}
}
