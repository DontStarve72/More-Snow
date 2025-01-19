package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockSnowy;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;

public abstract class BlockModelSnowy<T extends BlockSnowy> extends BlockModelStandard<T> {
    public static final IconCoordinate SNOW_TEXTURE = TextureRegistry.getTexture("minecraft:block/block_snow");

    public BlockModelSnowy(Block block) {
        super(block);
    }

    @Override
    public IconCoordinate getBlockTexture(WorldSource blockAccess, int x, int y, int z, Side side) {
        int metadata = blockAccess.getBlockMetadata(x, y, z);
        return this.getBlockTextureFromSideAndMetadata(side, metadata);
    }

    @Override
    public IconCoordinate getBlockTextureFromSideAndMetadata(Side side, int metadata) {
        int storedBlockID = this.block.getStoredBlockId(metadata);
        Block storedBlock = Block.getBlock(storedBlockID);
        int storedBlockMetadata = this.block.getStoredBlockMetadata(metadata);

        try {
            return BlockModelDispatcher.getInstance().getDispatch(storedBlock).getBlockTextureFromSideAndMetadata(side,
                    storedBlockMetadata);
        } catch (NullPointerException _exception) {
            return BLOCK_TEXTURE_UNASSIGNED;
        }
    }
}
