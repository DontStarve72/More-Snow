package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.client.sound.block.BlockSounds;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import turniplabs.halplibe.helper.BlockBuilder;

public class MSBlocks {
	public static BlockLayerSnowCover layerSnowCover;
	public static BlockSlabSnowCover slabSnowCover;

	public static void init(int minimumID) {
		layerSnowCover = (BlockLayerSnowCover) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND)
			.setBlockModel(new BlockModelRenderBlocks(72))
			.build(new BlockLayerSnowCover("layer.snow.cover", minimumID++, Material.topSnow));

		slabSnowCover = (BlockSlabSnowCover) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.OVERRIDE_STEPSOUND)
			.setBlockModel(new BlockModelRenderBlocks(73))
			.build(new BlockSlabSnowCover("slab.snow.cover", minimumID++, Material.topSnow));
	}
}
