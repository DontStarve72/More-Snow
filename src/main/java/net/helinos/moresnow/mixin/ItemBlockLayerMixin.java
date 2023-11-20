package net.helinos.moresnow.mixin;

import net.helinos.moresnow.block.BlockSnowy;
import net.helinos.moresnow.block.BlockSnowyPlant;
import net.helinos.moresnow.block.BlockSnowyStairs;
import net.helinos.moresnow.block.MSBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerBase;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumBlockSoundEffectType;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlockLayer;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.util.phys.AABB;
import net.minecraft.core.world.World;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemBlockLayer.class, remap = false)
public class ItemBlockLayerMixin {
	@Inject(method = "onItemUse", at = @At("HEAD"), cancellable = true)
	private void onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int blockX, int blockY, int blockZ, Side side, double xPlaced, double yPlaced, CallbackInfoReturnable<Boolean> cir) {
		int blockId = world.getBlockId(blockX, blockY, blockZ);
		int metadata = world.getBlockMetadata(blockX, blockY, blockZ);
		Block block = Block.getBlock(blockId);

		if (itemstack.stackSize <= 0) {
			cir.setReturnValue(false);
			return;
		}
		if (blockY == world.getHeightBlocks() - 1 && itemstack.itemID == Block.layerSnow.id) {
			cir.setReturnValue(false);
			return;
		}

		// Incrementing layer count on snow covered blocks with the snow layer item
		if (itemstack.itemID == Block.layerSnow.id && side == Side.TOP && block instanceof BlockSnowy) {
			BlockSnowy blockSnowy = (BlockSnowy) block;
			int newMetadata = metadata + 1;

			AABB bbBox = AABB.getBoundingBoxFromPool(blockX, blockY, blockZ, block.maxX, block.maxY + 0.125f, block.maxZ);
			if (!world.checkIfAABBIsClear(bbBox)) {
				cir.setReturnValue(false);
				return;
			}

			if (block instanceof BlockSnowyPlant) {
				if ((newMetadata & blockSnowy.maxLayers) < 7) {
					world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, MSBlocks.snowyPlant.id, newMetadata);
				} else {
					int storedID = MSBlocks.snowyPlant.getStoredBlockId(metadata);
					block.dropBlockWithCause(world, EnumDropCause.WORLD, blockX, blockY, blockZ, metadata, null);
					world.playSoundEffect(2001, blockX, blockY, blockZ, storedID);
					world.setBlockWithNotify(blockX, blockY, blockZ, Block.blockSnow.id);
				}
			} else if (ArrayUtils.contains(MSBlocks.blockIds, blockId)) {
				if ((newMetadata & blockSnowy.maxLayers) != 0) {
					if (block instanceof BlockSnowyStairs && world.getBlockId(blockX, blockY + 1, blockZ) == 0) {
							world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, MSBlocks.snowyPartial.id, newMetadata & 0b1111);
					}
					world.setBlockAndMetadataWithNotify(blockX, blockY, blockZ, block.id, newMetadata);
				} else {
					world.setBlockAndMetadataWithNotify(blockX, blockY + 1, blockZ, Block.layerSnow.id, 0);
				}
			}

			BlockLayerBase blockLayer = (BlockLayerBase) Block.blocksList[Block.layerSnow.id];
			world.playBlockSoundEffect((float) blockX + 0.5f, (float) blockY + 0.5f, (float) blockZ + 0.5f, blockLayer, EnumBlockSoundEffectType.PLACE);
			itemstack.consumeItem(entityplayer);
			cir.setReturnValue(true);
			return;
		}

		// Cover blocks that can be covered
		if (itemstack.itemID == Block.layerSnow.id) {
			if (!MSBlocks.tryMakeSnowy(world, blockId, blockX, blockY, blockZ)) return;

			BlockLayerBase blockLayer = (BlockLayerBase) Block.blocksList[Block.layerSnow.id];
			world.playBlockSoundEffect((float) blockX + 0.5f, (float) blockY + 0.5f, (float) blockZ + 0.5f, blockLayer, EnumBlockSoundEffectType.PLACE);
			itemstack.consumeItem(entityplayer);
			cir.setReturnValue(true);
		}
	}
}
