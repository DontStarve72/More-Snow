package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.client.sound.block.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import turniplabs.halplibe.helper.BlockBuilder;

public class MSBlocks {
	public static BlockSnowyPlant snowyPlant;
	public static BlockSnowySlab snowySlab;
	public static BlockSnowySlabPainted snowySlabPainted;
	public static BlockSnowyStairs snowyStairs;
	public static BlockSnowyStairsPainted snowyStairsPainted;

	private static BlockSnowy[] transparentList;
	private static BlockSnowy[] solidList;
	public static int[] blockIds;

	public static void init(int minimumID) {
		snowyPlant = (BlockSnowyPlant) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND)
			.setBlockModel(new BlockModelRenderBlocks(72))
			.build(new BlockSnowyPlant("snowy.plant", minimumID++, Material.topSnow, Block.tallgrass.id, Block.mushroomRed.id, new int[]{Block.algae.id}, false));

		snowySlab = (BlockSnowySlab) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(73))
			.build(new BlockSnowySlab("snowy.slab", minimumID++, Material.snow, Block.slabPlanksOak.id, Block.slabBasaltPolished.id, new int[]{Block.slabPlanksOakPainted.id}, true));

		snowySlabPainted = (BlockSnowySlabPainted) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(73))
			.build(new BlockSnowySlabPainted("snowy.slab.painted", minimumID++, Material.snow, Block.slabPlanksOakPainted.id, Block.slabPlanksOakPainted.id, new int[0], true));

		snowyStairs = (BlockSnowyStairs) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(74))
			.build(new BlockSnowyStairs("snowy.stairs", minimumID++, Material.snow, Block.stairsPlanksOak.id, Block.stairsBrickStone.id, new int[]{Block.stairsPlanksOakPainted.id}, true));

		snowyStairsPainted = (BlockSnowyStairsPainted) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(74))
			.build(new BlockSnowyStairsPainted("snowy.stairs", minimumID++, Material.snow, Block.stairsPlanksOakPainted.id, Block.stairsPlanksOakPainted.id, new int[0], true));

		transparentList = new BlockSnowy[]{snowyPlant};
		solidList = new BlockSnowy[]{snowySlab, snowySlabPainted, snowyStairs, snowyStairsPainted};

		blockIds = new int[transparentList.length + solidList.length];
		int i = 0;
		for (BlockSnowy block : transparentList) {
			blockIds[i++] = block.id;
		}
		for (BlockSnowy block : solidList) {
			blockIds[i++] = block.id;
		}
	}

	public static BlockSnowy whichCanReplace(int id, int metadata) {
		for (BlockSnowy block : transparentList) {
			if (block.canReplaceBlock(id, metadata)) return snowyPlant;
		}
		return whichCanReplaceSolid(id, metadata);
	}

	public static BlockSnowy whichCanReplaceSolid(int id, int metadata) {
		for (BlockSnowy block : solidList) {
			if (block.canReplaceBlock(id, metadata)) return block;
		}
		return null;
	}

	public static boolean tryMakeSnowy(World world, int id, int x, int y, int z) {
		boolean placed = tryMakeSnowyTransparent(world, id, x, y, z);
		placed |= tryMakeSnowySolid(world, id, x, y, z);
		return placed;
	}

	public static boolean tryMakeSnowyTransparent(World world, int id, int x, int y, int z) {
		boolean placed = false;
		for (BlockSnowy block : transparentList) {
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(World world, int id, int x, int y, int z) {
		boolean placed = false;
		for (BlockSnowy block : solidList) {
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}

	public static boolean tryMakeSnowyTransparent(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (BlockSnowy block : transparentList) {
			placed = block.tryMakeSnowy(chunk, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (BlockSnowy block : solidList) {
			placed = block.tryMakeSnowy(chunk, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}
}
