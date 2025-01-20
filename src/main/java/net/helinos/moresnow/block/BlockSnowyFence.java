package net.helinos.moresnow.block;

import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

import java.util.ArrayList;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFence;

public class BlockSnowyFence<T extends BlockFence> extends BlockSnowy {
    public BlockSnowyFence(String key, int id, Material material, Class<T> block, int[] excludedIds) {
        super(key, id, material, block, excludedIds, false, false, false);
    }

    public boolean canConnectTo(WorldSource worldSource, int x, int y, int z) {
        int blockID = worldSource.getBlockId(x, y, z);
        return Block.hasTag(blockID, BlockTags.FENCES_CONNECT);
    }

    @Override
    public AABB getCollisionBoundingBoxFromPool(WorldSource world, int x, int y, int z) {
        return AABB.getBoundingBoxFromPool(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY,
                z + this.maxZ);
    }

    @Override
    @SuppressWarnings(value = { "unchecked", "rawtypes" })
    public void getCollidingBoundingBoxes(World world, int x, int y, int z, AABB aabb, ArrayList aabbList) {
        int metadata = world.getBlockMetadata(x, y, z);
        int layers = this.getLayers(metadata);
        float height = layers * 2 / 16.0f;

        this.setBlockBounds(0.0, 0.0, 0.0, 1.0, height, 1.0);
        super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);

        boolean connectXPos = this.canConnectTo(world, x + 1, y, z);
        boolean connectXNeg = this.canConnectTo(world, x - 1, y, z);
        boolean connectZPos = this.canConnectTo(world, x, y, z + 1);
        boolean connectZNeg = this.canConnectTo(world, x, y, z - 1);

        this.setBlockBounds(
                0.0 + (connectXNeg ? 0.0 : 0.375),
                0.0,
                0.0 + (connectZNeg ? 0.0 : 0.375),
                1.0 - (connectXPos ? 0.0 : 0.375),
                1.5,
                1.0 - (connectZPos ? 0.0 : 0.375));
        super.getCollidingBoundingBoxes(world, x, y, z, aabb, aabbList);
    }

    @Override
    public void setBlockBoundsBasedOnState(WorldSource world, int x, int y, int z) {
        this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }
}
