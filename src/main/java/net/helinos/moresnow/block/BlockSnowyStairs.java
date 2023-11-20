package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

import java.util.ArrayList;

public class BlockSnowyStairs extends BlockSnowy {
	public BlockSnowyStairs(String key, int id, Material material, int minId, int maxId, int[] excludedIds, boolean fourLayers, boolean weirdShape) {
		super(key, id, material, minId, maxId, excludedIds, fourLayers, weirdShape);
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
	public boolean tryMakeSnowy(World world, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta)) return false;
		if (world.getBlockId(x, y + 1, z) == 0) {
			world.setBlockAndMetadataWithNotify(x, y + 1, z, MSBlocks.snowyPartial.id, meta << 2);
		}
		return world.setBlockAndMetadataWithNotify(x, y, z, this.id, this.blockToMetadata(id, meta));
	}

	@Override
	public boolean tryMakeSnowy(Chunk chunk, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta)) return false;
		if (chunk.getBlockID(x, y + 1, z) == 0) {
			chunk.setBlockIDWithMetadata(x, y + 1, z, MSBlocks.snowyPartial.id, meta << 2);
		}
		return chunk.setBlockIDWithMetadata(x, y, z, this.id, this.blockToMetadata(id, meta));
	}

	@Override
	public void accumulate(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int blockIdAbove = world.getBlockId(x, y + 1, z);

		if (blockIdAbove == 0) {
			world.setBlockAndMetadata(x, y, z, MSBlocks.snowyPartial.id, metadata & 0b1111);
		}

		super.accumulate(world, x, y, z);
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
		// this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int blockId) {
		Block blockAbove = world.getBlock(x, y + 1, z);
		int metadata = world.getBlockMetadata(x, y, z);
		if (blockAbove instanceof BlockSnowyPartial) {
			BlockSnowyPartial blockSnowyPartial = (BlockSnowyPartial) blockAbove;
			int aboveMetadata = world.getBlockMetadata(x, y + 1, z);
			int aboveLayers = blockSnowyPartial.getLayers(aboveMetadata);

			if (aboveLayers != this.getLayers(metadata)) {
				world.setBlockMetadata(x, y, z, (metadata & 0b11111100) | aboveLayers);
			}
		}
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
