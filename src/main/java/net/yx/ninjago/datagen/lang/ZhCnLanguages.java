package net.yx.ninjago.datagen.lang;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import net.yx.ninjago.NinjaGo;
import net.yx.ninjago.registries.ItemRegistry;

public class ZhCnLanguages extends LanguageProvider {

	private static final Logger LOGGER = LogManager.getLogger();

	public ZhCnLanguages(PackOutput output) {
		super(output, NinjaGo.MOD_ID, "zh_cn");
	}

	@Override
	protected void addTranslations() {
		LOGGER.info("Adding Chinese Translations");
		add(ItemRegistry.LONG_SWORD.get(), "长剑");
	}
}
