package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockSnowy;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockGrass;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockGrass.class, remap = false)
public class BlockGrassMixin {
	@Redirect(method = "getBlockTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/WorldSource;getBlockMaterial(III)Lnet/minecraft/core/block/material/Material;"))
	private Material noSnowUnderSnowCoveredBlock(WorldSource blockAccess, int x, int y, int z) {
		// I really don't want to be here right now
		Material material = blockAccess.getBlockMaterial(x, y, z);
		if (material == Material.snow) {
			Block block = blockAccess.getBlock(x, y, z);
			if (block instanceof BlockSnowy) {
				return Material.stone;
			}
		}

		return material;
	}
}
