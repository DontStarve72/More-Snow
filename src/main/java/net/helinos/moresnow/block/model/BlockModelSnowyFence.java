package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockSnowyFence;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockFence;

public class BlockModelSnowyFence extends BlockModelSnowy<BlockSnowyFence<BlockFence>> {
    public BlockModelSnowyFence(Block block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        this.block.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);

        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        boolean somethingRendered = false;

        if (this.block.getLayers(metadata) != 7) {
            // Center post
            this.block.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);
            this.block.setBlockBounds(0.375, 0.0, 0.375, 0.625, 1.0, 0.625);
            this.renderStandardBlock(tessellator, this.block, x, y, z);

            boolean connectEast = this.block.canConnectTo(renderBlocks.blockAccess, x - 1, y, z);
            boolean connectWest = this.block.canConnectTo(renderBlocks.blockAccess, x + 1, y, z);
            boolean connectNorth = this.block.canConnectTo(renderBlocks.blockAccess, x, y, z - 1);
            boolean connectSouth = this.block.canConnectTo(renderBlocks.blockAccess, x, y, z + 1);
            boolean renderEastWest = connectEast || connectWest;
            boolean renderNorthSouth = connectNorth || connectSouth;

            // if (!renderEastWest && !renderNorthSouth) {
            // renderEastWest = true;
            // }

            float east = connectEast ? 0.0F : 0.4375F;
            float west = connectWest ? 1.0F : 0.5625F;
            float north = connectNorth ? 0.0F : 0.4375F;
            float south = connectSouth ? 1.0F : 0.5625F;

            // Upper connecting posts
            if (renderEastWest) {
                this.block.setBlockBounds(east, 0.75, 0.4375, west, 0.9375, 0.5625);
                this.renderStandardBlock(tessellator, this.block, x, y, z);
            }

            if (renderNorthSouth) {
                this.block.setBlockBounds(0.4375, 0.75, north, 0.5625, 0.9375, south);
                this.renderStandardBlock(tessellator, this.block, x, y, z);
            }

            // Lower connecting posts
            if (renderEastWest) {
                this.block.setBlockBounds(east, 0.375, 0.4375, west, 0.5625, 0.5625);
                this.renderStandardBlock(tessellator, this.block, x, y, z);
            }

            if (renderNorthSouth) {
                this.block.setBlockBounds(0.4375, 0.375, north, 0.5625, 0.5625, south);
                this.renderStandardBlock(tessellator, this.block, x, y, z);
            }
        }

        renderBlocks.overrideBlockTexture = SNOW_TEXTURE;

        int layers = this.block.getLayers(metadata);
        float height = (layers + 1) * 2 / 16.0f;
        this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, height, 1.0);
        somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

        renderBlocks.overrideBlockTexture = null;

        return somethingRendered;
    }
}
