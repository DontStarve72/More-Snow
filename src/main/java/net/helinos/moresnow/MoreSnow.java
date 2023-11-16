package net.helinos.moresnow;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.loader.api.FabricLoader;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.helper.BlockHelper;
import turniplabs.halplibe.helper.RegistryHelper;
import turniplabs.halplibe.util.ConfigHandler;

import java.io.File;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;


public class MoreSnow implements ModInitializer {
	public static Map<Integer, Integer> COVERED_ID_MAP;

	public static void initBlockIDMap() {
		Hashtable<Integer, Integer> tmp =
			new Hashtable<>();
		tmp.put(1, Block.tallgrass.id); // Tall Grass
		tmp.put(2, Block.tallgrassFern.id); // Fern
		tmp.put(3, Block.deadbush.id); // Dead Bush
		tmp.put(4, Block.spinifex.id); // Spinifex
		tmp.put(5, Block.flowerYellow.id); // Dandelion
		tmp.put(6, Block.flowerRed.id); // Rose
		tmp.put(7, Block.mushroomBrown.id); // Brown Mushroom
		tmp.put(8, Block.mushroomRed.id); // Red Mushroom
		COVERED_ID_MAP = Collections.unmodifiableMap(tmp);
	}

    public static final String MOD_ID = "moresnow";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
		Properties properties = new Properties();
		ConfigHandler[] handler = new ConfigHandler[1];
		File config = new File(FabricLoader.getInstance().getConfigDir() + "/config/moresnow.properties");
		RegistryHelper.scheduleRegistry(config.exists(), () -> {
			int minimumBlockID = BlockHelper.findOpenIds(MSBlocks.class.getFields().length); // This is deprecated?

			MSBlocks.init(minimumBlockID);

			properties.put("block_ids_start", String.format("%s", minimumBlockID));

			handler[0].writeDefaultConfig();
		});
		handler[0] = new ConfigHandler(MOD_ID, properties);

		LOGGER.info("More Snow initialized.");
	}
}
