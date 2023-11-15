package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.Blocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherSnow;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(WeatherSnow.class)
public class WeatherSnowMixin extends Weather {
	public WeatherSnowMixin(int id) {
		super(id);
	}

	@Inject(method = "doEnvironmentUpdate", at = @At("HEAD"), remap = false)
	private void doEnvironmentUpdate(World world, Random rand, int x, int z, CallbackInfo callbackInfo) {
		int y = world.getHeightValue(x, z);

		int blockId = world.getBlockId(x, y, z);
		int blockIdBelow = world.getBlockId(x, y - 1, z);

		if (world.weatherPower <= 0.6f || y < 0 || y >= world.getHeightBlocks() || world.getSavedLightValue(LightLayer.Block, x, y, z) >= 10 || blockIdBelow == Block.ice.id || blockIdBelow == 0) return;

		Biome biome = world.getBlockBiome(x, y, z);
		if (ArrayUtils.contains(biome.blockedWeathers, this)) return;

		if (blockId == Blocks.layerSnowCover.id) {
			if (world.weatherPower <= 0.5f || world.seasonManager.getCurrentSeason() == null || !world.seasonManager.getCurrentSeason().hasDeeperSnow && biome != Biomes.OVERWORLD_GLACIER) return;
			Blocks.layerSnowCover.accumulate(world, x, y, z);
			return;
		}

		if (!Blocks.layerSnowCover.canCoverBlock(world, blockId, x, y, z)) return;

		int probability = (int)(64.0f * (1.0f / world.weatherPower));
		if (world.seasonManager.getCurrentSeason() != null && world.seasonManager.getCurrentSeason().hasDeeperSnow) {
			probability /= 2;
		}
		boolean snowWillFall = rand.nextInt(probability) == 0;
		if (!snowWillFall) return;

		int id = world.getBlockId(x, y, z);
		Blocks.layerSnowCover.placeSnowCover(world, id, x, y, z);
	}

	@Inject(method = "doChunkLoadEffect", at = @At("HEAD"), remap = false)
	private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo) {
		int chunkCornerX = chunk.xPosition * 16;
		int chunkCornerZ = chunk.zPosition * 16;
		int chunkCornerY = chunk.getHeightValue(0, 0);
		Biome biome = world.getBlockBiome(chunkCornerX, chunkCornerY, chunkCornerZ);
		if (ArrayUtils.contains(biome.blockedWeathers, this)) return;

		for (int chunkX = 0; chunkX < 16; chunkX++) {
			for (int chunkZ = 0; chunkZ < 16; chunkZ++) {
				int y = chunk.getHeightValue(chunkX, chunkZ);
				int worldX = chunkCornerX + chunkX;
				int worldZ = chunkCornerZ + chunkZ;
				int blockId = chunk.getBlockID(chunkX, y, chunkZ);
				int blockIdBelow = chunk.getBlockID(chunkX, y - 1, chunkZ);
				if (world.weatherPower <= 0.6f || y < 0 || y >= world.getHeightBlocks() || chunk.getSavedLightValue(LightLayer.Block, chunkX, y, chunkZ) >= 10 || blockIdBelow == 0 || !Blocks.layerSnowCover.canCoverBlock(world, blockId, worldX, y, worldZ) || blockIdBelow == Block.ice.id) continue;
				Blocks.layerSnowCover.placeSnowCover(chunk, blockId, chunkX, y, chunkZ);
			}
		}
	}
}
