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
import net.minecraft.core.world.chunk.Chunk;

import java.util.Map;
import java.util.Random;

import static net.helinos.moresnow.MoreSnow.COVERED_ID_MAP;

public class BlockLayerSnowCover extends BlockLayerSnow {
	public BlockLayerSnowCover(String key, int id, Material material) {
		super(key, id, material);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
		this.setTickOnLoad(true);
	}

	/**
	 * Check if the block at the given coordinates is capable of being covered by snow.
	 */
	public boolean canCoverBlock(World world, int x, int y, int z) {
		int id = world.getBlock(x, y, z).id;
		return canCoverBlock(world, id, x, y, z);
	}

	/**
	 * Check if the block at the given coordinates is capable of being covered by snow.
	 */
	public boolean canCoverBlock(World world, int id, int x, int y, int z) {
		return COVERED_ID_MAP.containsValue(id) && Blocks.layerSnowCover.canPlaceBlockAt(world, x, y, z);
	}

	/**
	 * Place a snow cover block at the given coordinates, this should be run after canCoverBlock(), otherwise the snow cover won't contain any relevant data
	 * @param id The numerical id of the block that should be "stored" inside the snow layer
	 */
	public void placeSnowCover(World world, int id, int x, int y, int z) {
		if (!world.isClientSide) {
			world.setBlockAndMetadataWithNotify(x, y, z, Blocks.layerSnowCover.id, blockIDToCoveredID(id));
		}
	}

	/**
	 * Place a snow cover block at the given coordinates, this should be run after canCoverBlock(), otherwise the snow cover won't contain any relevant data
	 * @param id The numerical id of the block that should be "stored" inside the snow layer
	 */
	public void placeSnowCover(Chunk chunk, int id, int x, int y, int z) {
		chunk.setBlockIDWithMetadata(x, y, z, Blocks.layerSnowCover.id, blockIDToCoveredID(id));
	}

	public void removeCover(World world, int metadata, int x, int y, int z) {
		if (!world.isClientSide) {
			world.setBlockWithNotify(x, y, z, getStoredBlockID(metadata));
		}
	}

	public void removeCover(Chunk chunk, int metadata, int x, int y, int z) {
		chunk.setBlockID(x, y, z, getStoredBlockID(metadata));
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata, EntityPlayer player, Item item) {
		if (!world.isClientSide) {
			removeCover(world, metadata, x, y, z);
		}
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
			int metadata = world.getBlockMetadata(x, y, z);
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, metadata, null);
			removeCover(world, metadata, x, y, z);
		}
		if (world.getBlockBiome(x, y, z) != null && !world.getBlockBiome(x, y, z).hasSurfaceSnow() && world.seasonManager.getCurrentSeason() != null && world.seasonManager.getCurrentSeason().letWeatherCleanUpSnow) {
			int metadata = world.getBlockMetadata(x, y, z);
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
			removeCover(world, metadata, x, y, z);
		}
	}

	public int getLayers(int metadata) {
		return metadata % 10;
	}

	public int getCoveredID(int metadata) {
		return metadata / 10;
	}

	public int getStoredBlockID(int metadata) {
		return COVERED_ID_MAP.getOrDefault(getCoveredID(metadata), 0);
	}

	public int blockIDToCoveredID(int blockID) {
		for (Map.Entry<Integer, Integer> entry : COVERED_ID_MAP.entrySet()) {
			if (entry.getValue() == blockID) {
				return entry.getKey() * 10;
			}
		}
		return 0;
	}
}
