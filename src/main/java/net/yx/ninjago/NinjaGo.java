package net.yx.ninjago;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.yx.ninjago.client.model.BoomerangModel;
import net.yx.ninjago.client.renderer.BoomerangRenderer;
import net.yx.ninjago.network.NinjaGoNetwork;
import net.yx.ninjago.registries.*;

@Mod(NinjaGo.MOD_ID)
public class NinjaGo {
	public static final String MOD_ID = "ninjago";
	public static final int TICKS_PER_SECONDS = 20;
    private static final Logger LOGGER = LogManager.getLogger();

	public NinjaGo() {
        LOGGER.info("NinjaGo Init");
		IEventBus fmlBus = FMLJavaModLoadingContext.get().getModEventBus();
		ItemRegistry.ITEM_REGISTER.register(fmlBus);
		CreativeModeTabRegistry.MODE_TAB_REGISTER.register(fmlBus);
		EntityTypeRegistry.ENTITY_TYPE.register(fmlBus);
        NinjaGoNetwork.init();
        
        fmlBus.addListener(NinjaGo::onClientSetup);
        fmlBus.addListener(NinjaGo::onRegisterEntityLayers);
	}
    
    public static void onClientSetup(FMLClientSetupEvent event) {
        LOGGER.info("NinjaGo Client Setup");
        event.enqueueWork(() -> {
            EntityRenderers.register(EntityTypeRegistry.BOOMERANG.get(), BoomerangRenderer::new);
        });
    }

    public static void onRegisterEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(BoomerangModel.LAYER_LOCATION, BoomerangModel::createBodyLayer);
    }
}
