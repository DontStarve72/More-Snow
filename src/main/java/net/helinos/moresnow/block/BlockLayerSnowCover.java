package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

public class BlockLayerSnowCover extends BlockSnowCovered {
	private static int metadataID = 0;

	public BlockLayerSnowCover(String key, int id, Material material) {
		super(key, id, material);
	}

	@Override
	public Map<Integer, Integer> initMetadataToBlockId() {
		Hashtable<Integer, Integer> tmp = new Hashtable<>();
		for (int id = Block.tallgrass.id; id <= Block.mushroomRed.id; ++id) {
			if (blocksList[id] == null || id == Block.algae.id) continue;
			tmp.put(++metadataID, id);
		}
		return Collections.unmodifiableMap(tmp);
	}

	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return this.METADATA_TO_BLOCK_ID.containsValue(id);
	}

	@Override
	public AABB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		float height = ((float) layers + 1.0f) * 2.0f / 16.0f;
		return AABB.getBoundingBoxFromPool((double) x + this.minX, (double) y + this.minY, (double) z + this.minZ, (double) x + this.maxX, (float) y + height - 0.125f, (double) z + this.maxZ);
	}

	@Override
	public void setBlockBoundsBasedOnState(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata) & 7;
		float height = ((float) layers + 1.0f) * 2.0f / 16.0f;
		this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, height, 1.0f);
	}

	@Override
	public void accumulate(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		if (layers > 7) {
			return;
		}

		world.setBlockMetadata(x, y, z, metadata + 1);
		world.markBlockNeedsUpdate(x, y, z);
	}

	@Override
	public int getLayers(int metadata) {
		return metadata & 0b111;
	}

	@Override
	public int getRelativeLayers(int metadata) {
		return getLayers(metadata);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		int blockKey = metadata >>> 3;
		return this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	@Override
	public int getStoredBlockMetadata(int metadata) {
		return 0;
	}

	@Override
	protected int blockToMetadata(int blockId, int metadataID) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << 3;
			}
		}

		return 0;
	}
}
