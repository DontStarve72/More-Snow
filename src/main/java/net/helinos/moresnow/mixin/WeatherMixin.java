package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Random;

@Mixin(Weather.class)
public class WeatherMixin {
	@Inject(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", shift = At.Shift.AFTER, ordinal = 1), remap = false)
	private void doEnvironmentUpdate(World world, Random rand, int x, int z, CallbackInfo ci) {
		int y = world.getHeightValue(x, z);
		int blockID = world.getBlockId(x, y, z);
		if (!world.getBlockBiome(x, y, z).hasSurfaceSnow() && blockID == Blocks.layerSnowCover.id) {
			int metadata = world.getBlockMetadata(x, y, z);
			int layers = Blocks.layerSnowCover.getLayers(metadata);
			if (layers != 0) {
				world.setBlockMetadata(x, y, z, metadata - 1);
				world.markBlockNeedsUpdate(x, y, z);
			} else {
				Blocks.layerSnowCover.removeCover(world, metadata, x, y, z);
			}
		}
	}

	@Inject(method = "doChunkLoadEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;getBlockID(III)I", shift = At.Shift.AFTER, ordinal = 1), remap = false, locals = LocalCapture.CAPTURE_FAILHARD)
	private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo, int x, int z, int y, int blockId) {
		if (!world.getBlockBiome(chunk.xPosition * 16 + x, y, chunk.zPosition * 16 + z).hasSurfaceSnow() && blockId == Blocks.layerSnowCover.id) {
			int metadata = chunk.getBlockMetadata(x, y, z);
			Blocks.layerSnowCover.removeCover(chunk, metadata, x, y, z);
		}
	}
}
