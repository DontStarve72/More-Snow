package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockSnowyStairs;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.stitcher.TextureRegistry;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;

public class BlockModelSnowyStairs extends BlockModelSnowy<BlockSnowyStairs> {
    public static final IconCoordinate SNOW_TEXTURE = TextureRegistry.getTexture("minecraft:block/block_snow");

    public BlockModelSnowyStairs(Block block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        // Render the stairs
        boolean somethingRendered = false;
        int horizontalRotation = this.block.getRotation(metadata);

        if (horizontalRotation == 0) {
            this.block.setBlockBounds(0.0, 0.0, 0.0, 0.5, 0.5, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
            this.block.setBlockBounds(0.5, 0.0, 0.0, 1.0, 1.0, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        } else if (horizontalRotation == 1) {
            this.block.setBlockBounds(0.0, 0.0, 0.0, 0.5, 1.0, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
            this.block.setBlockBounds(0.5, 0.0, 0.0, 1.0, 0.5, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        } else if (horizontalRotation == 2) {
            this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5, 0.5);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
            this.block.setBlockBounds(0.0, 0.0, 0.5, 1.0, 1.0, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        } else {
            this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0, 0.5);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
            this.block.setBlockBounds(0.0, 0.0, 0.5, 1.0, 0.5, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        }

        // Render the snow
        renderBlocks.overrideBlockTexture = SNOW_TEXTURE;
        int layers = this.block.getLayers(metadata);
        float heightFromSnow = (layers + 1) * 2 / 16.0f;

        // Render the snow
        if (horizontalRotation == 0) {
            this.block.setBlockBounds(0.0, 0.5, 0.0, 0.5, 0.5 + heightFromSnow, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        } else if (horizontalRotation == 1) {
            this.block.setBlockBounds(0.5, 0.5, 0.0, 1.0, 0.5 + heightFromSnow, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        } else if (horizontalRotation == 2) {
            this.block.setBlockBounds(0.0, 0.5, 0.0, 1.0, 0.5 + heightFromSnow, 0.5);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        } else {
            this.block.setBlockBounds(0.0, 0.5, 0.5, 1.0, 0.5 + heightFromSnow, 1.0);
            somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        }
        renderBlocks.overrideBlockTexture = null;

        this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 1.0 + heightFromSnow, 1.0);
        return somethingRendered;
    }
}
