package net.yx.ninjago.datagen;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.yx.ninjago.NinjaGo;
import net.yx.ninjago.datagen.lang.ZhCnLanguages;

@Mod.EventBusSubscriber(modid = NinjaGo.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenMain {
	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		LOGGER.info("Start Gather Data");
		ExistingFileHelper helper = event.getExistingFileHelper();
		DataGenerator generator = event.getGenerator();
		generator.addProvider(true, new ZhCnLanguages(generator.getPackOutput()));
	}

}
