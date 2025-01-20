package net.helinos.moresnow.block;

import net.helinos.moresnow.MoreSnow;
import net.helinos.moresnow.block.model.BlockModelSnowyFence;
import net.helinos.moresnow.block.model.BlockModelSnowyPlant;
import net.helinos.moresnow.block.model.BlockModelSnowySlab;
import net.helinos.moresnow.block.model.BlockModelSnowyStairs;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockCropsPumpkin;
import net.minecraft.core.block.BlockCropsWheat;
import net.minecraft.core.block.BlockFence;
import net.minecraft.core.block.BlockFencePainted;
import net.minecraft.core.block.BlockFlower;
import net.minecraft.core.block.BlockSaplingBase;
import net.minecraft.core.block.BlockSlab;
import net.minecraft.core.block.BlockSlabPainted;
import net.minecraft.core.block.BlockStairs;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

import java.util.ArrayList;

import org.apache.commons.lang3.ArrayUtils;
import turniplabs.halplibe.helper.BlockBuilder;

public class MSBlocks {
	public static BlockSnowyPlant snowyPlant;
	public static BlockSnowySlab<BlockSlab> snowySlab;
	public static BlockSnowySlabPainted snowySlabPainted;
	public static BlockSnowyStairs snowyStairs;
	public static BlockSnowyStairs snowyStairs2;
	public static BlockSnowyStairsPainted snowyStairsPainted;
	public static BlockSnowyPartial snowyPartial;
	public static BlockSnowyFence<BlockFence> snowyFence;
	public static BlockSnowyFencePainted snowyFencePainted;

	public static int[] transparentIds;
	public static int[] solidIds;
	public static int[] blockIds;

	public static void init(int minimumID) {
		MoreSnow.LOGGER.info("Initializing Blocks.");

		ArrayList<Integer> excludedPlantIDs = new ArrayList<>();
		for (Block block : Block.blocksList) {
			if (block instanceof BlockSaplingBase || block instanceof BlockCropsPumpkin
					|| block instanceof BlockCropsWheat) {
				excludedPlantIDs.add(block.id);
			}
		}
		snowyPlant = (BlockSnowyPlant) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setTextures("minecraft:block/block_snow")
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND)
				.setBlockModel(block -> new BlockModelSnowyPlant(block))
				.build(new BlockSnowyPlant("snowy.plant", minimumID++, Material.topSnow, BlockFlower.class,
						excludedPlantIDs));

		snowySlab = (BlockSnowySlab<BlockSlab>) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL)
				.setBlockModel(block -> new BlockModelSnowySlab(block))
				.build(new BlockSnowySlab<BlockSlab>("snowy.slab", minimumID++, Material.snow, BlockSlab.class,
						new int[] { Block.slabPlanksOakPainted.id }));

		snowySlabPainted = (BlockSnowySlabPainted) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL)
				.setBlockModel(block -> new BlockModelSnowySlab(block))
				.build(new BlockSnowySlabPainted("snowy.slab.painted", minimumID++, Material.snow,
						BlockSlabPainted.class, new int[0]));

		snowyStairs = (BlockSnowyStairs) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL)
				.setBlockModel(block -> new BlockModelSnowyStairs(block))
				.build(new BlockSnowyStairs("snowy.stairs", minimumID++, Material.snow, BlockStairs.class,
						new int[] { Block.stairsPlanksOakPainted.id }));

		snowyStairs2 = (BlockSnowyStairs) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL)
				.setBlockModel(block -> new BlockModelSnowyStairs(block))
				.build(new BlockSnowyStairs("snowy.stairs2", minimumID++, Material.snow, BlockStairs.class,
						ArrayUtils.add(snowyStairs.USED_IDS, Block.stairsPlanksOakPainted.id)));

		snowyStairsPainted = (BlockSnowyStairsPainted) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL)
				.setBlockModel(block -> new BlockModelSnowyStairs(block))
				.build(new BlockSnowyStairsPainted("snowy.stairs.painted", minimumID++, Material.snow,
						BlockStairs.class, ArrayUtils.addAll(snowyStairs.USED_IDS, snowyStairs2.USED_IDS)));

		snowyPartial = (BlockSnowyPartial) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setTextures("minecraft:block/block_snow")
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.BROKEN_BY_FLUIDS,
						BlockTags.PLACE_OVERWRITES)
				.build(new BlockSnowyPartial("snowy.partial", minimumID++, Material.topSnow));

		snowyFence = (BlockSnowyFence<BlockFence>) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT)
				.setBlockModel(block -> new BlockModelSnowyFence(block))
				.build(new BlockSnowyFence<BlockFence>("snowy.fence", minimumID++, Material.snow, BlockFence.class,
						new int[] { Block.fencePlanksOakPainted.id }));

		snowyFencePainted = (BlockSnowyFencePainted) new BlockBuilder(MoreSnow.MOD_ID)
				.setBlockSound(BlockSounds.CLOTH)
				.setHardness(0.1f)
				.setUseInternalLight()
				.setTags(BlockTags.MINEABLE_BY_SHOVEL, BlockTags.OVERRIDE_STEPSOUND, BlockTags.FENCES_CONNECT)
				.setBlockModel(block -> new BlockModelSnowyFence(block))
				.build(new BlockSnowyFencePainted("snowy.fence.painted", minimumID++, Material.snow,
						BlockFencePainted.class,
						new int[0]));

		transparentIds = new int[] { snowyPlant.id, snowyPartial.id };
		solidIds = new int[] { snowySlab.id, snowySlabPainted.id, snowyStairs.id, snowyStairs2.id,
				snowyStairsPainted.id,
				snowyFence.id, snowyFencePainted.id };
		blockIds = ArrayUtils.addAll(transparentIds, solidIds);

		MoreSnow.LOGGER.info("Initialized Blocks.");
	}

	public static BlockSnowy whichCanReplace(int id, int metadata) {
		for (int whichId : transparentIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			if (block.canReplaceBlock(id, metadata))
				return snowyPlant;
		}
		return whichCanReplaceSolid(id, metadata);
	}

	public static BlockSnowy whichCanReplaceSolid(int id, int metadata) {
		for (int whichId : solidIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			if (block.canReplaceBlock(id, metadata))
				return block;
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
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(World world, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : solidIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(world, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowyTransparent(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : transparentIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(chunk, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}

	public static boolean tryMakeSnowySolid(Chunk chunk, int id, int x, int y, int z) {
		boolean placed = false;
		for (int whichId : solidIds) {
			BlockSnowy block = (BlockSnowy) Block.getBlock(whichId);
			placed = block.tryMakeSnowy(chunk, id, x, y, z);
			if (placed)
				break;
		}
		return placed;
	}
}
