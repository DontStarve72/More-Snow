package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.client.sound.block.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerBase;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import turniplabs.halplibe.helper.BlockBuilder;

public class Blocks {
	private static int minimumID = 0;
	private static int currentID = 0;


	public static Block layerSnowCover;
	public static Block snowCover;

	public static void init(int minimumID) {
		Blocks.minimumID = minimumID;

		layerSnowCover = new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND)
			.build(new BlockLayerSnowCover("layer.snow.cover", nextID(), Material.topSnow));

		snowCover = new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.2f)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.FIREFLIES_CAN_SPAWN)
			.build(new BlockSnowCover("snow.cover", nextID(), Material.snow));

		((BlockLayerBase) layerSnowCover).setFullBlockID(snowCover.id);
	}

	private static int nextID() {
		int currentID = Blocks.currentID;
		Blocks.currentID++;
		return minimumID + currentID;
	}
}
