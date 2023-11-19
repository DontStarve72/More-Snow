package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockSnowySlab;
import net.helinos.moresnow.block.BlockSnowyStairs;
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

@Mixin(value = RenderBlocks.class, remap = false)
public abstract class RenderBlocksMixin {
	@Shadow
	private WorldSource blockAccess;
	@Shadow
	private Minecraft mc;
	@Shadow
	private World world;
	@Shadow
	private int overrideBlockTexture;

	@Shadow
	public abstract float getBlockBrightness(WorldSource blockAccess, int x, int y, int z);

	@Shadow
	public abstract void renderCrossedSquares(Block block, int metadata, double renderX, double renderY, double d2);

	@Shadow
	public abstract boolean renderStandardBlockWithAmbientOcclusion(Block block, int x, int y, int z, float r, float g, float b);

	@Shadow
	public abstract boolean renderStandardBlockWithColorMultiplier(Block block, int x, int y, int z, float r, float g, float b);

	@Shadow
	public abstract boolean renderStandardBlock(Block block, int x, int y, int z);

	@Inject(method = "renderBlockByRenderType", at = @At(value = "RETURN", ordinal = 31), cancellable = true)
	private void renderBlockByRenderType(Block block, int renderType, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
		switch (renderType) {
			case 72:
				cir.setReturnValue(this.renderSnowyPlant(block, x, y, z));
				break;
			case 73:
				cir.setReturnValue(this.renderSnowySlab(block, x, y, z));
				break;
			case 74:
				cir.setReturnValue(this.renderSnowyStairs(block, x, y, z));
				break;
		}
	}

	@Unique
	private boolean renderSnowyPlant(Block block, int x, int y, int z) {
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
		int storedBlockID = MSBlocks.snowyPlant.getStoredBlockId(metadata);
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
	private boolean renderSnowySlab(Block block, int x, int y, int z) {
		// Get stored slab texture
		BlockSnowySlab blockSnowySlab = (BlockSnowySlab) block;
		int metadata = this.blockAccess.getBlockMetadata(x, y, z);
		int storedBlockId = blockSnowySlab.getStoredBlockId(metadata);
		Block storedBlock = Block.getBlock(storedBlockId);

		try {
			this.overrideBlockTexture = storedBlock.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
		} catch (NullPointerException ignored) {
			this.overrideBlockTexture = 63;
		}

		// Get correct color in case slab is painted
		int storedBlockMeta = blockSnowySlab.getStoredBlockMetadata(metadata);
		int color = (BlockColorDispatcher.getInstance().getDispatch(storedBlock)).getFallbackColor(storedBlockMeta);

		// Render the slab
		block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
		boolean somethingRendered = renderStandardBlockWithFallbackColor(block, color, x, y, z);
		this.overrideBlockTexture = -1;

		int layers = blockSnowySlab.getLayers(metadata);
		float height = (layers + 1) * 2 / 16.0f;

		// Render the snow
		block.setBlockBounds(0.0f, 0.5f, 0.0f, 1.0f, 0.5f + height, 1.0f);
		somethingRendered |= this.renderStandardBlock(block, x, y, z);

		block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f + height, 1.0f);
		return somethingRendered;
	}

	@Unique
	private boolean renderSnowyStairs(Block block, int x, int y, int z) {
		// Get stored stair texture
		BlockSnowyStairs blockSnowyStairs = (BlockSnowyStairs) block;
		int metadata = this.blockAccess.getBlockMetadata(x, y, z);
		int storedBlockId = blockSnowyStairs.getStoredBlockId(metadata);
		Block storedBlock = Block.getBlock(storedBlockId);

		try {
			this.overrideBlockTexture = storedBlock.getBlockTextureFromSideAndMetadata(Side.BOTTOM, 0);
		} catch (NullPointerException ignored) {
			this.overrideBlockTexture = 63;
		}

		// Get correct color in case stairs are painted
		int storedBlockMeta = blockSnowyStairs.getStoredBlockMetadata(metadata);
		int color = (BlockColorDispatcher.getInstance().getDispatch(storedBlock)).getFallbackColor(storedBlockMeta);

		boolean somethingRendered = false;
		int hRotation = blockSnowyStairs.getRotation(metadata);

		// Render the stairs
		if (hRotation == 0) {
			block.setBlockBounds(0.0f, 0.0f , 0.0f, 0.5f, 0.5f, 1.0f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
			block.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
		} else if (hRotation == 1) {
			block.setBlockBounds(0.0f, 0.0f, 0.0f, 0.5f, 1.0f, 1.0f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
			block.setBlockBounds(0.5f, 0.0f, 0.0f, 1.0f, 0.5f, 1.0f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
		} else if (hRotation == 2) {
			block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 0.5f, 0.5f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
			block.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 1.0f, 1.0f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
		} else {
			block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.5f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
			block.setBlockBounds(0.0f, 0.0f, 0.5f, 1.0f, 0.5f, 1.0f);
			somethingRendered |= renderStandardBlockWithFallbackColor(block, color, x, y, z);
		}
		this.overrideBlockTexture = - 1;

		int layers = blockSnowyStairs.getLayers(metadata);
		float heightFromSnow = (layers + 1) * 2 / 16.0f;

		// Render the snow
		if (hRotation == 0) {
			block.setBlockBounds(0.0f, 0.5f , 0.0f, 0.5f, 0.5f + heightFromSnow, 1.0f);
			somethingRendered |= renderStandardBlock(block, x, y, z);
		} else if (hRotation == 1) {
			block.setBlockBounds(0.5f, 0.5f, 0.0f, 1.0f, 0.5f + heightFromSnow, 1.0f);
			somethingRendered |= renderStandardBlock(block, x, y, z);
		} else if (hRotation == 2) {
			block.setBlockBounds(0.0f, 0.5f, 0.0f, 1.0f, 0.5f + heightFromSnow, 0.5f);
			somethingRendered |= renderStandardBlock(block, x, y, z);
		} else {
			block.setBlockBounds(0.0f, 0.5f, 0.5f, 1.0f, 0.5f + heightFromSnow, 1.0f);
			somethingRendered |= renderStandardBlock(block, x, y, z);
		}

		block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f + heightFromSnow, 1.0f);
		return somethingRendered;
	}

	@Unique
	private boolean renderStandardBlockWithFallbackColor(Block block, int color, int x, int y, int z) {
		float red = (float)(color >> 16 & 0xFF) / 255.0f;
		float blue = (float)(color >> 8 & 0xFF) / 255.0f;
		float green = (float)(color & 0xFF) / 255.0f;
		if (this.mc.isAmbientOcclusionEnabled()) {
			return this.renderStandardBlockWithAmbientOcclusion(block, x, y, z, red, blue, green);
		}
		return this.renderStandardBlockWithColorMultiplier(block, x, y, z, red, blue, green);
	}
}
