package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFencePainted;
import net.minecraft.core.block.material.Material;

public class BlockSnowyFencePainted extends BlockSnowyFence<BlockFencePainted> {
    public BlockSnowyFencePainted(String key, int id, Material material, Class<BlockFencePainted> block,
            int[] excludedIds) {
        super(key, id, material, block, excludedIds);
    }

    @Override
    public int getStoredBlockId(int metadata) {
        return Block.fencePlanksOakPainted.id;
    }

    @Override
    public int getStoredBlockMetadata(int metadata) {
        return (metadata >> 4) & 0b00001111;
    }

    @Override
    protected int blockToMetadata(int blockId, int metadata) {
        return (metadata << 4) & 0b11110000;
    }
}
