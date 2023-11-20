package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

import java.util.ArrayList;
import java.util.Map;

public class BlockSnowyPartial extends BlockSnowy {
	public BlockSnowyPartial(String key, int id, Material material, boolean fourLayers, boolean weirdShape) {
		super(key, id, material, 0, 0, new int[0], fourLayers, weirdShape);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.125f, 1.0f);
	}

	@Override
	protected Map<Integer, Integer> initMetadataToBlockId(int minId, int maxId, int[] excludedIds) {
		return null;
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return false;
	}

	@Override
	public AABB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AABB.getBoundingBoxFromPool(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int rotation = this.getRotation(metadata);
		int layers = this.getLayers(metadata);
		float heightFromSnow = (layers + 1) * 2 / 16.0f;
		if (rotation == 0) {
			this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, heightFromSnow, 1.0f);
		} else if (rotation == 1) {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, heightFromSnow, 1.0f);
		} else if (rotation == 2) {
			this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, heightFromSnow, 1.0f);
		} else {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, heightFromSnow, 0.5f);
		}
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
		int metadata = world.getBlockMetadata(x, y, z);
		int rotation = this.getRotation(metadata);
		int layers = this.getLayers(metadata);
		float heightFromSnow = (layers) * 2 / 16.0f;
		if (rotation == 0) {
			this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, heightFromSnow, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		} else if (rotation == 1) {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, heightFromSnow, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		} else if (rotation == 2) {
			this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, heightFromSnow, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		} else {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, heightFromSnow, 0.5f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		}
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, heightFromSnow + 0.125f, 1.0f);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		Block blockBelow = world.getBlock(x, y - 1, z);
		if (blockBelow instanceof BlockSnowyStairs) {
			BlockSnowyStairs blockSnowyStairs = (BlockSnowyStairs) blockBelow;
			int metadata = world.getBlockMetadata(x, y, z);
			int belowMetadata = world.getBlockMetadata(x, y - 1, z);
			int belowLayers = blockSnowyStairs.getLayers(belowMetadata);

			if (belowLayers != this.getLayers(metadata)) {
				world.setBlockMetadata(x, y, z, (metadata & 0b11111100) | belowLayers);
			}
		} else {
			world.setBlockWithNotify(x, y, z, 0);
		}
	}

	@Override
	public int getRelativeLayers(int metadata) {
		return getLayers(metadata);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return 0;
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return 0;
	}

	public int getRotation(int metadata) {
		return (metadata >> 2) & 0b11;
	}
}
