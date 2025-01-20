package net.helinos.moresnow.block;

import net.minecraft.core.block.BlockSlab;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

import java.util.Map;

public class BlockSnowySlab<T extends BlockSlab> extends BlockSnowy {
	public BlockSnowySlab(String key, int id, Material material, Class<T> block,
			int[] excludedIds) {
		super(key, id, material, block, excludedIds, true, false, true);
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.625f, 1.0f);
		this.withLightBlock(1);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		if (super.canReplaceBlock(id, metadata)) {
			return (metadata & 0b11) == 0;
		}

		return false;
	}

	@Override
	public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		float height = layers * 2 / 16.0f;
		return AABB.getBoundingBoxFromPool(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX,
				y + 0.5f + height, z + this.maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		float height = (layers + 1) * 2 / 16.0f;
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f + height, 1.0f);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		int blockKey = (metadata >> 2) & 0b00111111;
		return this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << 2;
			}
		}

		return 0;
	}
}
