package net.helinos.moresnow.mixin;

import net.helinos.moresnow.MoreSnow;
import net.minecraft.core.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
	@Inject(method = "<clinit>", at = @At(value = "TAIL"))
	private static void afterBlocks(CallbackInfo ci) {
		MoreSnow.initBlockIDMap();
	}
}
