package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.Blocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerBase;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.item.block.ItemBlockLayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemBlockLayer.class)
public class ItemBlockLayerMixin extends ItemBlock {
	public ItemBlockLayerMixin(Block block) {
		super(block);
	}

	@Inject(method = "onItemUse(Lnet/minecraft/core/item/ItemStack;Lnet/minecraft/core/entity/player/EntityPlayer;Lnet/minecraft/core/world/World;IIILnet/minecraft/core/util/helper/Side;DD)Z", at = @At("HEAD"), remap = false, cancellable = true)
	private void onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		int id = world.getBlockId(blockX, blockY, blockZ);
		int metadata = world.getBlockMetadata(blockX, blockY, blockZ);

		if (itemstack.stackSize <= 0) {
			cir.setReturnValue(false);
			return;
		}
		if (blockY == world.getHeightBlocks() - 1 && itemstack.itemID == Block.layerSnow.id) {
			cir.setReturnValue(false);
			return;
		}

		// Incrementing layer count on snow covers with the snow layer item
		if (id == Blocks.layerSnowCover.id && itemstack.itemID == Block.layerSnow.id && side == Side.TOP) {
			BlockLayerBase blockLayer = (BlockLayerBase)Block.blocksList[Blocks.layerSnowCover.id];
			int newMetadata = metadata + 1;
			int layers = Blocks.layerSnowCover.getLayers(newMetadata);

			AABB bbBox = AABB.getBoundingBoxFromPool(blockX, blockY, blockZ, (float)blockX + 1.0f, (float)blockY + (float)(2 * (layers + 1)) / 16.0f, (float)blockZ + 1.0f);
			if (!world.checkIfAABBIsClear(bbBox)) {
				cir.setReturnValue(false);
				return;
			}

			if (newMetadata % 10 < 7) {
				world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, Blocks.layerSnowCover.id, newMetadata);
			} else {
				world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, blockLayer.fullBlockID, Blocks.layerSnowCover.getCoveredID(metadata));
			}

			world.playBlockSoundEffect((float)blockX + 0.5f, (float)blockY + 0.5f, (float)blockZ + 0.5f, blockLayer, EnumBlockSoundEffectType.PLACE);
			itemstack.consumeItem(entityplayer);
			cir.setReturnValue(true);
			return;
		}

		// Cover blocks that can be covered
		if (itemstack.itemID == Block.layerSnow.id && Blocks.layerSnowCover.canCoverBlock(world, blockX, blockY, blockZ)) {
			BlockLayerBase blockLayer = (BlockLayerBase)Block.blocksList[Blocks.layerSnowCover.id];

			AABB bbBox = AABB.getBoundingBoxFromPool(blockX, blockY, blockZ, (float)blockX + 1.0f, (float)blockY + 2.0f / 16.0f, (float)blockZ + 1.0f);
			if (!world.checkIfAABBIsClear(bbBox)) {
				cir.setReturnValue(false);
				return;
			}

			Blocks.layerSnowCover.placeSnowCover(world, id, blockX, blockY, blockZ);

			world.playBlockSoundEffect((float)blockX + 0.5f, (float)blockY + 0.5f, (float)blockZ + 0.5f, blockLayer, EnumBlockSoundEffectType.PLACE);
			itemstack.consumeItem(entityplayer);
			cir.setReturnValue(true);
		}
	}
}
