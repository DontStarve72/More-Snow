package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.client.render.block.model.BlockModelRenderBlocks;
import net.minecraft.client.sound.block.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import org.apache.commons.lang3.ArrayUtils;
import turniplabs.halplibe.helper.BlockBuilder;

public class MSBlocks {
	public static BlockSnowyPlant snowyPlant;
	public static BlockSnowySlab snowySlab;
	public static BlockSnowySlabPainted snowySlabPainted;
	public static BlockSnowyStairs snowyStairs;
	public static BlockSnowyStairsPainted snowyStairsPainted;
	public static BlockSnowyPartial snowyPartial;

	public static int[] transparentIds;
	public static int[] solidIds;
	public static int[] blockIds;

	public static void init(int minimumID) {
		snowyPlant = (BlockSnowyPlant) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND)
			.setBlockModel(new BlockModelRenderBlocks(72))
			.build(new BlockSnowyPlant("snowy.plant", minimumID++, Material.topSnow, Block.tallgrass.id, Block.mushroomRed.id, new int[]{Block.algae.id}, false, false));

		snowySlab = (BlockSnowySlab) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(73))
			.build(new BlockSnowySlab("snowy.slab", minimumID++, Material.snow, Block.slabPlanksOak.id, Block.slabBasaltPolished.id, new int[]{Block.slabPlanksOakPainted.id}, true, false));

		snowySlabPainted = (BlockSnowySlabPainted) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(73))
			.build(new BlockSnowySlabPainted("snowy.slab.painted", minimumID++, Material.snow, Block.slabPlanksOakPainted.id, Block.slabPlanksOakPainted.id, new int[0], true, false));

		snowyStairs = (BlockSnowyStairs) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(74))
			.build(new BlockSnowyStairs("snowy.stairs", minimumID++, Material.snow, Block.stairsPlanksOak.id, Block.stairsBrickStone.id, new int[]{Block.stairsPlanksOakPainted.id}, true, true));

		snowyStairsPainted = (BlockSnowyStairsPainted) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.setBlockModel(new BlockModelRenderBlocks(74))
			.build(new BlockSnowyStairsPainted("snowy.stairs", minimumID++, Material.snow, Block.stairsPlanksOakPainted.id, Block.stairsPlanksOakPainted.id, new int[0], true, true));

		snowyPartial = (BlockSnowyPartial) new BlockBuilder(MoreSnow.MOD_ID)
			.setBlockSound(BlockSounds.CLOTH)
			.setTextures(2, 4)
			.setHardness(0.1f)
			.setUseInternalLight()
			.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES)
			.setBlockModel(new BlockModelRenderBlocks(0))
			.build(new BlockSnowyPartial("snowy.partial", minimumID++, Material.topSnow, true, true));

		transparentIds = new int[]{snowyPlant.id, snowyPartial.id};
		solidIds = new int[]{snowySlab.id, snowySlabPainted.id, snowyStairs.id, snowyStairsPainted.id};
		blockIds = ArrayUtils.addAll(transparentIds, solidIds);
	}

	public static BlockSnowy whichCanReplace(int id, int metadata) {
		for (int whichId : transparentIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			if (block.canReplaceBlock(id, metadata)) return snowyPlant;
		}
		return whichCanReplaceSolid(id, metadata);
	}

	public static BlockSnowy whichCanReplaceSolid(int id, int metadata) {
		for (int whichId : solidIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
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
		for (int whichId : transparentIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(World world, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : solidIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}

	public static boolean tryMakeSnowyTransparent(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : transparentIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(chunk, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : solidIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(chunk, id, x, y, z);
			if (placed) break;
		}
		return placed;
	}
}
