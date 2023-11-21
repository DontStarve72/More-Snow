package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockSnowy;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerSnow;
import net.minecraft.core.block.BlockSlab;
import net.minecraft.core.block.BlockStairs;
import net.minecraft.core.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockLayerSnow.class, remap = false)
public abstract class BlockLayerSnowMixin {
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 0))
	private int blockId1(World world, int x, int y, int z) {
		return accountForSnowy(world, x, y, z);
	}
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 1))
	private int metadata1(World world, int x, int y, int z) {
		return getLayers(world, x, y, z);
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 1))
	private int blockId2(World world, int x, int y, int z) {
		return accountForSnowy(world, x, y, z);
	}
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 2))
	private int metadata2(World world, int x, int y, int z) {
		return getLayers(world, x, y, z);
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 2))
	private int blockId3(World world, int x, int y, int z) {
		return accountForSnowy(world, x, y, z);
	}
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 3))
	private int metadata3(World world, int x, int y, int z) {
		return getLayers(world, x, y, z);
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 3))
	private int blockId4(World world, int x, int y, int z) {
		return accountForSnowy(world, x, y, z);
	}
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 4))
	private int metadata4(World world, int x, int y, int z) {
		return getLayers(world, x, y, z);
	}

	@Unique
	private int accountForSnowy(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);

		if (ArrayUtils.contains(MSBlocks.blockIds, id)) {
			return Block.layerSnow.id;
		}

		return id;
	}

	@Unique
	private int getLayers(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		if (block instanceof BlockSnowy) {
			return ((BlockSnowy) block).getRelativeLayers(metadata);
		}

		return metadata;
	}

	@Redirect(method = "canPlaceBlockAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I"))
	private int pretendIsOpaque(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		Block block = Block.getBlock(id);

		if (block instanceof BlockSlab) {
			int metadata = world.getBlockMetadata(x, y, z);
			int slabState = metadata & 3;

			if (slabState != 0) {
				return Block.stone.id;
			}
		} else if (block instanceof BlockStairs) {
			int metadata = world.getBlockMetadata(x, y, z);
			int stairsState = metadata & 8;

			if (stairsState != 0) {
				return Block.stone.id;
			}
		}

		return world.getBlockId(x, y, z);
	}
}
