package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockSnowySlab;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;

public class BlockModelSnowySlab extends BlockModelSnowy<BlockSnowySlab> {
    public BlockModelSnowySlab(Block block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        // Render the slab
        this.block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
        boolean somethingRendered = this.renderStandardBlock(tessellator, this.block, x, y, z);

        // Render the snow
        renderBlocks.overrideBlockTexture = SNOW_TEXTURE;
        int layers = block.getLayers(metadata);
        float height = (layers + 1) * 2 / 16.0f;

        this.block.setBlockBounds(0.0f, 0.5f, 0.0f, 1.0f, 0.5f + height, 1.0f);
        somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        renderBlocks.overrideBlockTexture = null;

        this.block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f + height, 1.0f);
        return somethingRendered;
    }
}
