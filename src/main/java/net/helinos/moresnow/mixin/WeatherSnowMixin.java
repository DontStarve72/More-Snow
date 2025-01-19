package net.helinos.moresnow.mixin;

import net.helinos.moresnow.MoreSnow;
import net.helinos.moresnow.block.BlockSnowyPlant;
import net.helinos.moresnow.block.BlockSnowy;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerSnow;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.weather.WeatherSnow;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WeatherSnow.class, remap = false)
public abstract class WeatherSnowMixin {
	@Unique
	private static boolean snowFell = false;
	@Unique
	private static boolean snowCoverHack = false;
	@Unique
	private static int idToStore = 0;
	@Unique
	private static int metadataToStore = 0;
	@Unique
	private static BlockSnowy snowCoverType = null;

	@ModifyVariable(method = "doEnvironmentUpdate", at = @At("STORE"), ordinal = 0)
	private boolean snow(boolean snow) {
		snowFell = snow;
		return snow;
	}

	@Redirect(method = "doChunkLoadEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/chunk/Chunk;getHeightValue(II)I"))
	private int lowerYIfSolid(Chunk chunk, int x, int z) {
		int y = chunk.getHeightValue(x, z);
		int idAbove = chunk.getBlockID(x, y, z);
		int id = chunk.getBlockID(x, y - 1, z);
		int metadata = chunk.getBlockMetadata(x, y - 1, z);

		// TODO: Find out if this causes issues with snowy fences
		if ((MSBlocks.whichCanReplaceSolid(id, metadata) != null // Lower if the block can be converted to a snowy block
				|| ArrayUtils.contains(MSBlocks.solidIds, id)) // Lower if the block is a snowy block for the accumulate
																// function
				&& idAbove != Block.layerSnow.id) {
			return y - 1;
		}

		return y;
	}

	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 0))
	private int blockIdHack(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (!snowFell)
			return id;
		Block block = Block.getBlock(id);

		if (block instanceof BlockSnowy) {
			snowCoverHack = true;
			return Block.layerSnow.id;
		}

		int metadata = world.getBlockMetadata(x, y, z);
		BlockSnowy blockSnowy = MSBlocks.whichCanReplace(id, metadata);

		if (blockSnowy != null) {
			snowCoverType = blockSnowy;
			idToStore = id;
			metadataToStore = metadata;
			return 0;
		}

		return id;
	}

	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 1))
	private int blockIdBelowHack(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y + 1, z);
		int metadata = world.getBlockMetadata(x, y + 1, z);

		if (MSBlocks.whichCanReplaceSolid(id, metadata) != null) {
			return Block.stone.id;
		}

		return world.getBlockId(x, y, z);
	}

	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/Block;canPlaceBlockAt(Lnet/minecraft/core/world/World;III)Z"))
	private boolean canPlaceBlockAt(Block block, World world, int x, int y, int z) {
		if (!(snowCoverType instanceof BlockSnowyPlant)) {
			return true;
		}

		return block.canPlaceBlockAt(world, x, y, z);
	}

	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;setBlockWithNotify(IIII)Z", ordinal = 0))
	private boolean placeCoverOrSnow(World world, int x, int y, int z, int id) {
		if (snowCoverType != null) {
			boolean placed = snowCoverType.tryMakeSnowy(world, idToStore, metadataToStore, x, y, z);
			snowCoverType = null;
			idToStore = 0;
			metadataToStore = 0;
			return placed;
		}

		return world.setBlockWithNotify(x, y, z, Block.layerSnow.id);
	}

	@Redirect(method = "doEnvironmentUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/BlockLayerSnow;accumulate(Lnet/minecraft/core/world/World;III)V"))
	private void accumulate(BlockLayerSnow blockLayerSnow, World world, int x, int y, int z) {
		int layers;
		int metadata = world.getBlockMetadata(x, y, z);
		Block block = world.getBlock(x, y, z);

		boolean snowCoverHack = WeatherSnowMixin.snowCoverHack;
		WeatherSnowMixin.snowCoverHack = false;

		if (snowCoverHack) {
			try {
				layers = ((BlockSnowy) block).getRelativeLayers(metadata);
			} catch (ClassCastException ignored) {
				MoreSnow.LOGGER.warn("snowCoverHack was true when it shouldn't have been");
				snowCoverHack = false;
				layers = metadata;
			}
		} else {
			layers = metadata;
		}

		for (Direction direction : Direction.horizontalDirections) {
			int neighborX = x + direction.getOffsetX();
			int neighborZ = z + direction.getOffsetZ();

			int neighborId = world.getBlockId(neighborX, y, neighborZ);
			int belowNeighborId = world.getBlockId(neighborX, y - 1, neighborZ);
			Block neighborBlock = Block.getBlock(neighborId);

			// If the neighboring block can support snow
			if (Block.layerSnow.canPlaceBlockAt(world, x, y, z) && belowNeighborId != 0) {
				if (neighborId == 0) {
					world.setBlockWithNotify(x, y, z, Block.layerSnow.id);
					return;
				} else if (MSBlocks.tryMakeSnowyTransparent(world, neighborId, neighborX, y, neighborZ)) {
					return;
				}
			}

			if (MSBlocks.tryMakeSnowySolid(world, neighborId, neighborX, y, neighborZ))
				return;

			// Check if the neighboring block is a snow cover and get how many layers it has
			int neighborMetadata = world.getBlockMetadata(neighborX, y, neighborZ);
			int neighborLayers;
			if (neighborBlock == Block.layerSnow) {
				neighborLayers = neighborMetadata;
			} else if (neighborBlock instanceof BlockSnowy) {
				neighborLayers = ((BlockSnowy) neighborBlock).getRelativeLayers(neighborMetadata);
			} else {
				continue;
			}

			// Accumulate the neighbor if its snow is lower than this one
			if (layers > neighborLayers) {
				if (neighborBlock instanceof BlockLayerSnow) {
					((BlockLayerSnow) neighborBlock).accumulate(world, neighborX, y, neighborZ);
				} else if (neighborBlock instanceof BlockSnowy) {
					((BlockSnowy) neighborBlock).accumulate(world, neighborX, y, neighborZ);
				}

				return;
			}
		}

		if (snowCoverHack) {
			((BlockSnowy) block).accumulate(world, x, y, z);
		} else {
			blockLayerSnow.accumulate(world, x, y, z);
		}
	}

	@Inject(method = "doChunkLoadEffect", at = @At("HEAD"))
	private void doChunkLoadEffect(World world, Chunk chunk, CallbackInfo callbackInfo) {
		int chunkCornerX = chunk.xPosition * 16;
		int chunkCornerZ = chunk.zPosition * 16;
		int chunkCornerY = chunk.getHeightValue(0, 0);
		Biome biome = world.getBlockBiome(chunkCornerX, chunkCornerY, chunkCornerZ);
		if (ArrayUtils.contains(biome.blockedWeathers, this))
			return;

		for (int chunkX = 0; chunkX < 16; ++chunkX) {
			for (int chunkZ = 0; chunkZ < 16; ++chunkZ) {
				int y = chunk.getHeightValue(chunkX, chunkZ);
				if (world.weatherManager.getWeatherPower() <= 0.6f || y < 0 || y >= world.getHeightBlocks()
						|| chunk.getBrightness(LightLayer.Block, chunkX, y, chunkZ) >= 10)
					continue;

				int id = chunk.getBlockID(chunkX, y, chunkZ);
				MSBlocks.tryMakeSnowyTransparent(chunk, id, chunkX, y, chunkZ);

				id = chunk.getBlockID(chunkX, y - 1, chunkZ);
				MSBlocks.tryMakeSnowySolid(chunk, id, chunkX, y - 1, chunkZ);
			}
		}
	}
}
