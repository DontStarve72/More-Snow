package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.Blocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerSnow;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockLayerSnow.class)
public abstract class BlockLayerSnowMixin {
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 0), remap = false)
	private int blockId1(World world, int x, int y, int z) {
		return snowLayerHack(world, x, y, z);
	}
	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 1), remap = false)
	private int metadata1(World world, int x, int y, int z) {
		return 8;
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 1), remap = false)
	private int blockId2(World world, int x, int y, int z) {
		return snowLayerHack(world, x, y, z);
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 2), remap = false)
	private int metadata2(World world, int x, int y, int z) {
		return 8;
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 2), remap = false)
	private int blockId3(World world, int x, int y, int z) {
		return snowLayerHack(world, x, y, z);
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 3), remap = false)
	private int metadata3(World world, int x, int y, int z) {
		return 8;
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockId(III)I", ordinal = 3), remap = false)
	private int blockId4(World world, int x, int y, int z) {
		return snowLayerHack(world, x, y, z);
	}

	@Redirect(method = "accumulate", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/World;getBlockMetadata(III)I", ordinal = 4), remap = false)
	private int metadata4(World world, int x, int y, int z) {
		return 8;
	}

	@Unique
	private int snowLayerHack(World world, int x, int y, int z) {
		int blockId = world.getBlockId(x, y, z);
		if (blockId == Blocks.layerSnowCover.id) return Block.layerSnow.id;
		return blockId;
	}
}
