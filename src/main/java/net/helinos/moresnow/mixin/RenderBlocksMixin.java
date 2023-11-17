package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderBlocks;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderBlocks.class)
public abstract class RenderBlocksMixin {
	@Shadow(remap = false)
	private WorldSource blockAccess;
	@Shadow(remap = false)
	private Minecraft mc;
	@Shadow(remap = false)
	private World world;
	@Shadow(remap = false)
	private int overrideBlockTexture;

	@Shadow(remap = false)
	public abstract float getBlockBrightness(WorldSource blockAccess, int x, int y, int z);

	@Shadow(remap = false)
	public abstract void renderCrossedSquares(Block block, int metadata, double renderX, double renderY, double d2);

	@Shadow(remap = false)
	public abstract boolean renderStandardBlockWithAmbientOcclusion(Block block, int x, int y, int z, float r, float g, float b);

	@Shadow(remap = false)
	public abstract boolean renderStandardBlockWithColorMultiplier(Block block, int x, int y, int z, float r, float g, float b);

	@Shadow
	public abstract boolean renderStandardBlock(Block block, int x, int y, int z);

	@Inject(method = "renderBlockByRenderType", at = @At(value = "RETURN", ordinal = 31), cancellable = true, remap = false)
	private void renderBlockByRenderType(Block block, int renderType, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		switch (renderType) {
			case 72:
				cir.setReturnValue(this.renderLayerSnowCover(block, x, y, z));
				break;
			case 73:
				cir.setReturnValue(this.renderSlabSnowCover(block, x, y, z));
				break;
		}
	}

	@Unique
	private boolean renderLayerSnowCover(Block block, int x, int y, int z) {
		Tessellator tessellator = Tessellator.instance;
		float blockBrightness = this.getBlockBrightness(this.blockAccess, x, y, z);
		int blockColor = BlockColorDispatcher.getInstance().getDispatch(block).getWorldColor(this.world, x, y, z);
		float red = (float) (blockColor >> 16 & 0xFF) / 255.0f;
		float green = (float) (blockColor >> 8 & 0xFF) / 255.0f;
		float blue = (float) (blockColor & 0xFF) / 255.0f;
		tessellator.setColorOpaque_F(blockBrightness * red, blockBrightness * green, blockBrightness * blue);
		double renderX = x;
		double renderY = y;
		double renderZ = z;
		int metadata = this.blockAccess.getBlockMetadata(x, y, z);
		int storedBlockID = MSBlocks.layerSnowCover.getStoredBlockID(metadata);
		Block storedBlock = Block.getBlock(storedBlockID);

		// Random offset based on coordinates
		if (storedBlock == Block.tallgrass || storedBlock == Block.tallgrassFern || storedBlock == Block.spinifex) {
			long hashValue = (x * 3129871L) ^ (long) z * 116129781L ^ (long) y;
			hashValue = hashValue * hashValue * 42317861L + hashValue * 11L;

			renderX += ((double) ((float) (hashValue >> 16 & 0xFL) / 15.0f) - 0.5) * 0.5;
			renderY += ((double) ((float) (hashValue >> 20 & 0xFL) / 15.0f) - 1.0) * 0.2;
			renderZ += ((double) ((float) (hashValue >> 24 & 0xFL) / 15.0f) - 0.5) * 0.5;
		}

		try {
			this.overrideBlockTexture = storedBlock.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
		} catch (NullPointerException ignored) {
			this.overrideBlockTexture = 63;
		}

		this.renderCrossedSquares(block, this.blockAccess.getBlockMetadata(x, y, z), renderX, renderY, renderZ);
		this.overrideBlockTexture = -1;
		if (this.mc.isAmbientOcclusionEnabled()) {
			return this.renderStandardBlockWithAmbientOcclusion(block, x, y, z, red, green, blue);
		}
		return this.renderStandardBlockWithColorMultiplier(block, x, y, z, red, green, blue);
	}

	@Unique
	private boolean renderSlabSnowCover(Block block, int x, int y, int z) {
		int metadata = this.blockAccess.getBlockMetadata(x, y, z);
		int storedBlockId = MSBlocks.slabSnowCover.getStoredBlockID(metadata);
		Block storedBlock = Block.getBlock(storedBlockId);

		try {
			this.overrideBlockTexture = storedBlock.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
		} catch (NullPointerException ignored) {
			this.overrideBlockTexture = 63;
		}

		block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
		this.renderStandardBlock(block, x, y, z);
		this.overrideBlockTexture = -1;

		int layers = MSBlocks.slabSnowCover.getLayers(metadata);
		block.setBlockBounds(0.0f, 0.5f, 0.0f, 1.0f, 0.5f + ((layers + 1) * 0.125f), 1.0f);
		this.renderStandardBlock(block, x, y, z);

		float height = 0.5f + ((float) layers + 1.0f) * 2.0f / 16.0f;
		block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, height, 1.0f);
		return true;
	}
}
