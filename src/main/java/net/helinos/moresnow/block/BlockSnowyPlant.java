package net.helinos.moresnow.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.WorldSource;

public class BlockSnowyPlant extends BlockSnowy {
	public BlockSnowyPlant(String key, int id, Material material, int minId, int maxId, int[] excludedIds,
			boolean fourLayers, boolean weirdShape) {
		super(key, id, material, minId, maxId, excludedIds, fourLayers, weirdShape);
	}

	@Override
	public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		float height = layers * 2 / 16.0f;
		return AABB.getBoundingBoxFromPool((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ,
				(double) x + this.maxX, (float) y + height, (double) z + this.maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(WorldSource world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		float height = (layers + 1) * 2 / 16.0f;
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, height, 1.0f);
	}
}
