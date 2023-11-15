package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockSnow;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

import static net.helinos.moresnow.MoreSnow.COVERED_ID_MAP;

public class BlockSnowCover extends BlockSnow {
	public BlockSnowCover(String key, int id) {
		super(key, id);
		this.setTickOnLoad(true);
	}

	public static void removeCoverBlock(World world, int metadata, int x, int y, int z) {
		if (!world.isClientSide) {
			world.setBlockWithNotify(x, y, z, getStoredBlockID(metadata));
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata, EntityPlayer player, Item item) {
		if (!world.isClientSide) {
			removeCoverBlock(world, metadata, x, y, z);
		}
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case PICK_BLOCK:
			case SILK_TOUCH: {
				return new ItemStack[]{new ItemStack(Block.blockSnow)};
			}
		}
		return new ItemStack[]{new ItemStack(Item.ammoSnowball, 4)};
	}


	public static int getStoredBlockID(int metadata) {
		return COVERED_ID_MAP.getOrDefault(metadata, 0);
	}
}
