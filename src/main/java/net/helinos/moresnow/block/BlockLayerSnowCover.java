package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerSnow;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

import java.util.Map;
import java.util.Random;

import static net.helinos.moresnow.MoreSnow.COVERED_ID_MAP;

public class BlockLayerSnowCover extends BlockLayerSnow {
	public BlockLayerSnowCover(String key, int id, Material material) {
		super(key, id, material);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
		this.setTickOnLoad(true);
	}

	public static boolean canCoverBlock(World world, int x, int y, int z) {
		int id = world.getBlock(x, y, z).id;
		return COVERED_ID_MAP.containsValue(id) && Blocks.layerSnowCover.canPlaceBlockAt(world, x, y, z);
	}

	public static void placeSnowCover(World world, int id, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, Blocks.layerSnowCover.id, blockIDToCoveredID(id));
	}

	public static void removeCoverOrSnow(World world, int x, int y, int z) {
		if (!world.isClientSide) {
			int metadata = world.getBlockMetadata(x, y, z);
			world.setBlockWithNotify(x, y, z, getStoredBlockID(metadata));
		}
	}

	@Override
	public void onBlockRemoval(World world, int x, int y, int z) {
		removeCoverOrSnow(world, x, y, z);
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta, EntityPlayer player, Item item) {
		removeCoverOrSnow(world, x, y, z);
	}

	@Override
	public AABB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = getLayers(metadata);
		float height = ((float)layers + 1.0f) * 2.0f / 16.0f;
		return AABB.getBoundingBoxFromPool((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (float)y + height - 0.125f, (double)z + this.maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = getLayers(metadata);
		float height = ((float)layers + 1.0f) * 2.0f / 16.0f;
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, height, 1.0f);
	}

	@Override
	public void accumulate(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = getLayers(metadata);
		if (layers % 10 > 7) {
			return;
		}

		boolean posXValid = world.isBlockOpaqueCube(x + 1, y, z) || world.getBlockId(x + 1, y, z) == this.id && getLayers(world.getBlockMetadata(x + 1, y, z)) >= layers;
		if (!posXValid) {
			return;
		}
		boolean posZValid = world.isBlockOpaqueCube(x, y, z + 1) || world.getBlockId(x, y, z + 1) == this.id && getLayers(world.getBlockMetadata(x, y, z + 1)) >= layers;
		if (!posZValid) {
			return;
		}
		boolean negXValid = world.isBlockOpaqueCube(x - 1, y, z) || world.getBlockId(x - 1, y, z) == this.id && getLayers(world.getBlockMetadata(x - 1, y, z)) >= layers;
		if (!negXValid) {
			return;
		}
		boolean negZValid = world.isBlockOpaqueCube(x, y, z - 1) || world.getBlockId(x, y, z - 1) == this.id && getLayers(world.getBlockMetadata(x, y, z - 1)) >= layers;
		if (!negZValid) {
			return;
		}

		world.setBlockMetadata(x, y, z, metadata + 1);
		world.markBlockNeedsUpdate(x, y, z);
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case SILK_TOUCH: {
				return new ItemStack[]{new ItemStack(Block.layerSnow, getLayers(meta) + 1)};
			}
			case PICK_BLOCK: {
				return new ItemStack[]{new ItemStack(Block.layerSnow)};
			}
			case PROPER_TOOL: {
				return new ItemStack[]{new ItemStack(Item.ammoSnowball, getLayers(meta) + 1)};
			}
		}
		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11) {
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
			removeCoverOrSnow(world, x, y, z);
		}
		if (world.getBlockBiome(x, y, z) != null && !world.getBlockBiome(x, y, z).hasSurfaceSnow() && world.seasonManager.getCurrentSeason() != null && world.seasonManager.getCurrentSeason().letWeatherCleanUpSnow) {
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
			removeCoverOrSnow(world, x, y, z);
		}
	}

	public static int getLayers(int metadata) {
		return metadata % 10;
	}

	public static int getCoveredID(int metadata) {
		return (metadata - (metadata % 10)) / 10;
	}

	public static int getStoredBlockID(int metadata) {
		return COVERED_ID_MAP.getOrDefault(getCoveredID(metadata), 0);
	}

	public static int blockIDToCoveredID(int blockID) {
		for (Map.Entry<Integer, Integer> entry : COVERED_ID_MAP.entrySet()) {
			if (entry.getValue() == blockID) {
				return entry.getKey() * 10;
			}
		}
		return 0;
	}
}
