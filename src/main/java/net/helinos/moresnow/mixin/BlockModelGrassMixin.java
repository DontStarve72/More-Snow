package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockSnowy;
import net.helinos.moresnow.block.BlockSnowyFence;
import net.minecraft.client.render.block.model.BlockModelGrass;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.world.WorldSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockModelGrass.class, remap = false)
public class BlockModelGrassMixin {
	@Redirect(method = "getBlockTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/WorldSource;getBlockMaterial(III)Lnet/minecraft/core/block/material/Material;"))
	private Material noSnowUnderSnowCoveredBlock(WorldSource blockAccess, int x, int y, int z) {
		Material material = blockAccess.getBlockMaterial(x, y, z);
		if (material == Material.snow) {
			Block block = blockAccess.getBlock(x, y, z);
			if (block instanceof BlockSnowy && !(block instanceof BlockSnowyFence)) {
				return Material.stone;
			}
		}

		return material;
	}
}
