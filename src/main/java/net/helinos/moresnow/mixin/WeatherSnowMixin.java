package net.helinos.moresnow.mixin;

import net.helinos.moresnow.MoreSnow;
import net.helinos.moresnow.block.Blocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerSnow;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherSnow;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(WeatherSnow.class)
public class WeatherSnowMixin extends Weather {
	public WeatherSnowMixin(int id) {
		super(id);
	}

	@Unique
	private static boolean snowFell = false;
	@Unique
	private static boolean snowCoverHack = false;
	@Unique
	private static int blockIdToStore = 0;

	@ModifyVariable(method = "doEnvironmentUpdate", at = @At("STORE"), remap = false, ordinal = 0)
	private boolean snow(boolean snow) {
		snowFell = snow;
        return snow;
    }

	@ModifyVariable(method = "doEnvironmentUpdate", at = @At("STORE"), remap = false, ordinal = 4)
	private int blockId(int originalId) {
		if (!snowFell) return originalId;

		if (originalId == Blocks.layerSnowCover.id) {
			snowCoverHack = true;
			return Block.layerSnow.id;
		} else if (MoreSnow.COVERED_ID_MAP.containsValue(originalId)) {
			blockIdToStore = originalId;
			return 0;
		}

		return originalId;
	}

	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;setBlockWithNotify(IIII)Z", ordinal = 0), remap = false)
	private boolean placeCoverOrSnow(World world, int x, int y, int z, int id) {
		if (blockIdToStore == 0) {
			return world.setBlockWithNotify(x, y, z, Block.layerSnow.id);
		} else {
			int storeId = blockIdToStore;
			blockIdToStore = 0;
			return Blocks.layerSnowCover.placeSnowCover(world, storeId, x, y, z);
		}
	}

	@Debug(export = true)
	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/BlockLayerSnow;accumulate(Lnet/minecraft/core/world/World;III)V"), remap = false)
	private void accumulate(BlockLayerSnow blockLayerSnow, World world, int x, int y, int z) {
		int layers;
		int metadata = world.getBlockMetadata(x, y, z);
		boolean snowCoverHack = WeatherSnowMixin.snowCoverHack;
		WeatherSnowMixin.snowCoverHack = false;
		if (!snowCoverHack) {
			layers = metadata;
		} else {
			layers = Blocks.layerSnowCover.getLayers(metadata);
		}

		for (Direction direction : Direction.horizontalDirections) {
			int neighborX = x + direction.getOffsetX();
			int neighborZ = z + direction.getOffsetZ();

			Block neighborBlock = world.getBlock(neighborX, y, neighborZ);
			// If the is retrieved straight from the block it throws a NPE
			int neighborId = world.getBlockId(neighborX, y, neighborZ);
			int belowNeighborId = world.getBlockId(neighborX, y - 1, neighborZ);

            // If the neighboring block can support snow
			if (Block.layerSnow.canPlaceBlockAt(world, x, y, z) && belowNeighborId != 0) {
				if (neighborId == 0) {
					world.setBlockWithNotify(x, y, z, Block.layerSnow.id);
					return;
				} else if (Blocks.layerSnowCover.canCoverBlock(world, neighborId, neighborX, y, neighborZ)) {
					Blocks.layerSnowCover.placeSnowCover(world, neighborId, neighborX, y, neighborZ);
					return;
				}
            }

			// Check if the neighboring block is a snow layer and get the layers
            int neighborMetadata = world.getBlockMetadata(neighborX, y, neighborZ);
			int neighborLayers;
			if (neighborBlock == Block.layerSnow) {
				neighborLayers = neighborMetadata;
			} else if (neighborBlock == Blocks.layerSnowCover) {
				neighborLayers = Blocks.layerSnowCover.getLayers(neighborMetadata);
			} else {
				continue;
			}

			// Accumulate the neighbor if its snow is lower than this one
			if (layers > neighborLayers) {
				((BlockLayerSnow) neighborBlock).accumulate(world, neighborX, y, neighborZ);
				return;
			}
		}

		if (!snowCoverHack) {
			blockLayerSnow.accumulate(world, x, y, z);
		} else {
			Blocks.layerSnowCover.accumulate(world, x, y, z);
		}
	}

//	@Overwrite
//	public void doEnvironmentUpdate(World world, Random rand, int x, int z) {
//		// Weather isn't powerful enough
//		if (world.weatherPower <= 0.6f) return;
//
//		int y = world.getHeightValue(x, z);
//
//		// Outside the world
//		if (y < 0 || y >= world.getHeightBlocks()) return;
//		// Too bright for snow
//		if (world.getSavedLightValue(LightLayer.Block, x, y, z) >= 10) return;
//
//		Biome biome = world.getBlockBiome(x, y, z);
//		// Snowing is blocked in this biome
//		if (ArrayUtils.contains(biome.blockedWeathers, this)) return;
//
//		int probability = (int)(64.0f * (1.0f / world.weatherPower));
//		if (world.seasonManager.getCurrentSeason() != null && world.seasonManager.getCurrentSeason().hasDeeperSnow) {
//			probability /= 2;
//		}
//		boolean snowWillFall = rand.nextInt(probability) == 0;
//		if (!snowWillFall) return;
//
//		int blockId = world.getBlockId(x, y, z);
//		int blockIdBelow = world.getBlockId(x, y - 1, z);
//
//		// Place snow
//		if (blockIdBelow != 0 && blockIdBelow != Block.ice.id) {
//			if (Block.layerSnow.canPlaceBlockAt(world, x, y, z) && blockId == 0) {
//				world.setBlockWithNotify(x, y, z, Block.layerSnow.id);
//				return;
//			} else if (Blocks.layerSnowCover.canCoverBlock(world, x, y, z)) {
//				Blocks.layerSnowCover.placeSnowCover(world, blockId, x, y, z);
//				return;
//			}
//		}
//
//		// Accumulate snow
//		if (world.seasonManager.getCurrentSeason() != null && (world.seasonManager.getCurrentSeason().hasDeeperSnow || biome == Biomes.OVERWORLD_GLACIER)) {
//			if (blockId == Block.layerSnow.id) {
//				((BlockLayerSnow) Block.layerSnow).accumulate(world, x, y, z);
//				return;
//			} else if (blockId == Blocks.layerSnowCover.id) {
//				Blocks.layerSnowCover.accumulate(world, x, y, z);
//				return;
//			}
//		}
//
//		// Freeze water
//		if (blockIdBelow == Block.fluidWaterStill.id && world.getBlockMetadata(x, y - 1, z) == 0 && rand.nextFloat() < world.weatherPower * world.weatherIntensity) {
//			for (int directionIter = 0; directionIter < 4; directionIter++)	{
//				Direction direction = Direction.horizontalDirections[directionIter];
//				Block neighborBlock = world.getBlock(x + direction.getOffsetX(), y - 1, z + direction.getOffsetZ());
//				if (neighborBlock != Block.ice && (neighborBlock == null || !neighborBlock.isOpaqueCube())) continue;
//				world.setBlockWithNotify(x, y - 1, z, Block.ice.id);
//				break;
//			}
//		}
//	}

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
