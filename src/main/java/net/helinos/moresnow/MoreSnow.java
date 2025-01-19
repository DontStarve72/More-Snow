package net.helinos.moresnow;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.helinos.moresnow.block.MSBlocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import turniplabs.halplibe.helper.BlockBuilder;
import turniplabs.halplibe.helper.RegistryHelper;
import turniplabs.halplibe.util.ConfigHandler;

import java.io.File;
import java.util.Properties;

public class MoreSnow implements ModInitializer {
	public static final String MOD_ID = "moresnow";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		Properties properties = new Properties();
		ConfigHandler[] handler = new ConfigHandler[1];
		File config = new File(FabricLoader.getInstance().getConfigDir() + "/config/moresnow.properties");
		RegistryHelper.scheduleRegistry(config.exists(), () -> {
			// This is deprecated?
			int minimumBlockID = BlockBuilder.Registry.findOpenIds(MSBlocks.class.getDeclaredFields().length - 3);

			MSBlocks.init(minimumBlockID);

			properties.put("block_ids_start", String.format("%s", minimumBlockID));

			handler[0].writeDefaultConfig();
		});
		handler[0] = new ConfigHandler(MOD_ID, properties);

		LOGGER.info("More Snow initialized.");
	}
}
