package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockSnowy;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weather;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(value = Weather.class, remap = false)
public class WeatherMixin {
	@Inject(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", shift = At.Shift.AFTER, ordinal = 1))
	private void doEnvironmentUpdate(World world, Random rand, int x, int z, CallbackInfo ci) {
		int y = world.getHeightValue(x, z);
		for (int i = 0; i < 2; i++) {
			y -= i;
			Block block = world.getBlock(x, y, z);

			if (!world.getBlockBiome(x, y, z).hasSurfaceSnow() && block instanceof BlockSnowy) {
				BlockSnowy blockSnowy = (BlockSnowy) block;
				int metadata = world.getBlockMetadata(x, y, z);
				int layers = blockSnowy.getLayers(metadata);

				if (layers != 0) {
					world.setBlockMetadata(x, y, z, metadata - 1);
					world.markBlockNeedsUpdate(x, y, z);
				} else {
					blockSnowy.removeSnow(world, metadata, x, y, z);
				}
			}
		}
	}

	@Inject(method = "doChunkLoadEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;getBlockID(III)I", shift = At.Shift.AFTER, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
	private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo, int x, int z, int y, int blockId) {
		if (!world.getBlockBiome(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z).hasSurfaceSnow()) {
			Block block = Block.getBlock(blockId);

			if (ArrayUtils.contains(MSBlocks.transparentList, blockId)) {
				BlockSnowy blockSnowy = (BlockSnowy) block;
				int metadata = chunk.getBlockMetadata(x, y, z);
				blockSnowy.removeSnow(chunk, metadata, x, y, z);
			}

			int blockBelowId = chunk.getBlockID(x, y - 1, z);
			block = Block.getBlock(blockBelowId);

			if (ArrayUtils.contains(MSBlocks.solidList, blockBelowId) && !(blockId == Block.layerSnow.id)) {
				BlockSnowy blockSnowy = (BlockSnowy) block;
				int metadata = chunk.getBlockMetadata(x, y - 1, z);
				blockSnowy.removeSnow(chunk, metadata, x, y - 1, z);
			}
		}
	}
}
