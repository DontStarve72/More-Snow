package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockSnowySlab;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockSlab;

public class BlockModelSnowySlab extends BlockModelSnowy<BlockSnowySlab<BlockSlab>> {
    public BlockModelSnowySlab(Block block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);

        // Render the slab
        this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
        boolean somethingRendered = this.renderStandardBlock(tessellator, this.block, x, y, z);

        // Render the snow
        renderBlocks.overrideBlockTexture = SNOW_TEXTURE;
        int layers = block.getLayers(metadata);
        float height = (layers + 1) * 2 / 16.0f;

        this.block.setBlockBounds(0.0, 0.5, 0.0, 1.0, 0.5 + height, 1.0);
        somethingRendered |= this.renderStandardBlock(tessellator, this.block, x, y, z);
        renderBlocks.overrideBlockTexture = null;

        this.block.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.5 + height, 1.0);
        return somethingRendered;
    }
}
