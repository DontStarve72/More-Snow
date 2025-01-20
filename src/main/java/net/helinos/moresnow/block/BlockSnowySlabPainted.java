package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockSlabPainted;
import net.minecraft.core.block.material.Material;

public class BlockSnowySlabPainted extends BlockSnowySlab<BlockSlabPainted> {
	public BlockSnowySlabPainted(String key, int id, Material material, Class<BlockSlabPainted> block,
			int[] excludedIds) {
		super(key, id, material, block, excludedIds);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		return Block.slabPlanksOakPainted.id;
	}

	@Override
	public int getStoredBlockMetadata(int metadata) {
		return metadata & 0b11110000;
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		return metadata;
	}
}
