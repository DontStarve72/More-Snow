package net.helinos.moresnow.block.model;

import net.helinos.moresnow.block.BlockSnowyPlant;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.stitcher.IconCoordinate;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;

public class BlockModelSnowyPlant extends BlockModelStandard<BlockSnowyPlant> {
    public BlockModelSnowyPlant(Block block) {
        super(block);
    }

    @Override
    public boolean render(Tessellator tessellator, int x, int y, int z) {
        this.block.setBlockBoundsBasedOnState(renderBlocks.blockAccess, x, y, z);

        float blockBrightness = 1.0F;
        if (!LightmapHelper.isLightmapEnabled()) {
            blockBrightness = this.getBlockBrightness(renderBlocks.blockAccess, x, y, z);
        } else {
            tessellator.setLightmapCoord(this.block.getLightmapCoord(renderBlocks.blockAccess, x, y, z));
        }

        int blockColor = BlockColorDispatcher.getInstance().getDispatch(this.block)
                .getWorldColor(renderBlocks.blockAccess, x, y, z);
        float red = (blockColor >> 16 & 0xFF) / 255.0f;
        float green = (blockColor >> 8 & 0xFF) / 255.0f;
        float blue = (blockColor & 0xFF) / 255.0f;
        tessellator.setColorOpaque_F(blockBrightness * red, blockBrightness * green, blockBrightness * blue);
        double renderX = x;
        double renderY = y;
        double renderZ = z;
        int metadata = renderBlocks.blockAccess.getBlockMetadata(x, y, z);
        int storedBlockID = this.block.getStoredBlockId(metadata);
        Block storedBlock = Block.getBlock(storedBlockID);

        // Random offset based on coordinates
        if (storedBlock == Block.tallgrass || storedBlock == Block.tallgrassFern || storedBlock == Block.spinifex) {
            long hashValue = (x * 3129871) ^ z * 116129781 ^ y;
            hashValue = hashValue * hashValue * 42317861 + hashValue * 11;

            renderX += ((hashValue >> 16 & 0xF) / 15.0 - 0.5) * 0.5;
            renderY += ((hashValue >> 20 & 0xF) / 15.0 - 1.0) * 0.2;
            renderZ += ((hashValue >> 24 & 0xF) / 15.0 - 0.5) * 0.5;
        }

        IconCoordinate textureIndex;

        try {
            textureIndex = BlockModelDispatcher.getInstance().getDispatch(storedBlock)
                    .getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
        } catch (NullPointerException _exception) {
            textureIndex = BLOCK_TEXTURE_UNASSIGNED;
        }

        double minU = textureIndex.getIconUMin();
        double maxU = textureIndex.getIconUMax();
        double minV = textureIndex.getIconVMin();
        double maxV = textureIndex.getIconVMax();
        double minX = renderX + 0.5 - 0.45;
        double maxX = renderX + 0.5 + 0.45;
        double minZ = renderZ + 0.5 - 0.45;
        double maxZ = renderZ + 0.5 + 0.45;
        tessellator.addVertexWithUV(minX, renderY + 1.0 + 0.0, minZ, minU, minV);
        tessellator.addVertexWithUV(minX, renderY + 0.0, minZ, minU, maxV);
        tessellator.addVertexWithUV(maxX, renderY + 0.0, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(maxX, renderY + 1.0 + 0.0, maxZ, maxU, minV);
        tessellator.addVertexWithUV(maxX, renderY + 1.0 + 0.0, maxZ, minU, minV);
        tessellator.addVertexWithUV(maxX, renderY + 0.0, maxZ, minU, maxV);
        tessellator.addVertexWithUV(minX, renderY + 0.0, minZ, maxU, maxV);
        tessellator.addVertexWithUV(minX, renderY + 1.0 + 0.0, minZ, maxU, minV);
        tessellator.addVertexWithUV(minX, renderY + 1.0 + 0.0, maxZ, minU, minV);
        tessellator.addVertexWithUV(minX, renderY + 0.0, maxZ, minU, maxV);
        tessellator.addVertexWithUV(maxX, renderY + 0.0, minZ, maxU, maxV);
        tessellator.addVertexWithUV(maxX, renderY + 1.0 + 0.0, minZ, maxU, minV);
        tessellator.addVertexWithUV(maxX, renderY + 1.0 + 0.0, minZ, minU, minV);
        tessellator.addVertexWithUV(maxX, renderY + 0.0, minZ, minU, maxV);
        tessellator.addVertexWithUV(minX, renderY + 0.0, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(minX, renderY + 1.0 + 0.0, maxZ, maxU, minV);

        return super.render(tessellator, x, y, z);
    }
}
