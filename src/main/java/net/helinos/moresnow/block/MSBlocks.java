package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.client.sound.block.BlockSounds;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import turniplabs.halplibe.helper.BlockBuilder;

public class MSBlocks {
	private static int minimumID = 0;
	private static int currentID = 0;

	public static BlockLayerSnowCover layerSnowCover;

	public static void init(int minimumID) {
		MSBlocks.minimumID = minimumID;

		layerSnowCover = (BlockLayerSnowCover) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND)
			.setBlockModel(new BlockModelRenderBlocks(72))
			.build(new BlockLayerSnowCover("layer.snow.cover", nextID(), Material.topSnow));
	}

	private static int nextID() {
		int currentID = MSBlocks.currentID;
		MSBlocks.currentID++;
		return minimumID + currentID;
	}
}
