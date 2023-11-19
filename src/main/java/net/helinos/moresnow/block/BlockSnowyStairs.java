package net.helinos.moresnow.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

import java.util.ArrayList;

public class BlockSnowyStairs extends BlockSnowy {
	public BlockSnowyStairs(String key, int id, Material material, int minId, int maxId, int[] excludedIds, boolean fourLayers) {
		super(key, id, material, minId, maxId, excludedIds, fourLayers);
		this.withLightOpacity(255);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		if (super.canReplaceBlock(id, metadata)) {
			return (metadata & 0b1000) == 0;
		}

		return false;
	}

	@Override
	public AABB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return AABB.getBoundingBoxFromPool(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z) {
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	@SuppressWarnings(value = "unchecked")
	public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
		int metadata = world.getBlockMetadata(x, y, z);
		int rotation = this.getRotation(metadata);
		int layers = this.getLayers(metadata);
		float heightFromSnow = (layers) * 2 / 16.0f;
		if (rotation == 0) {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 0.5f + heightFromSnow, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
			this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		} else if (rotation == 1) {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
			this.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 0.5f + heightFromSnow, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		} else if (rotation == 2) {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f + heightFromSnow, 0.5f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
			this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		} else {
			this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
			this.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 0.5f + heightFromSnow, 1.0f);
			super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
		}
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
	}

	@Override
	public int getStoredBlockMetadata(int metadata) {
		return getRotation(metadata);
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return (metadata << 2) | super.blockToMetadata(blockId, metadata);
	}

	public int getRotation(int metadata) {
		return (metadata >> 2) & 0b11;
	}
}
