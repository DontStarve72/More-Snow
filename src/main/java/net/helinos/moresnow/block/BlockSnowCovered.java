package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLayerSnow;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

import java.util.Map;
import java.util.Random;

public abstract class BlockSnowCovered extends BlockLayerSnow {
	protected final Map<Integer, Integer> METADATA_TO_BLOCK_ID;

	public BlockSnowCovered(String key, int id, Material material) {
		super(key, id, material);
		this.METADATA_TO_BLOCK_ID = this.initMetadataToBlockId();
	}

	public abstract Map<Integer, Integer> initMetadataToBlockId();

	/**
	 * Check a given block id with given metadata is capable of being replaced by a snow covered block.
	 */
	public abstract boolean canReplaceBlock(int id, int metadata);

	/**
	 * Place a snow covered block at the given coordinates.
	 *
	 * @param id The numerical id of the block that should be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int, int)
	 */
	public boolean placeSnowCover(World world, int id, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return this.placeSnowCover(world, id, meta, x, y, z);
	}

	/**
	 * Place a snow covered block at the given coordinates.
	 *
	 * @param id The numerical id of the block that should be "stored" inside the snow covered block
	 * @param meta The metadata of the block that should be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int, int)
	 */
	public boolean placeSnowCover(World world, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta)) return false;
		return world.setBlockAndMetadataWithNotify(x, y, z, this.id, this.blockToMetadata(id, meta));
	}

	/**
	 * Place a snow covered block at the given relative chunk coordinates.
	 *
	 * @param id The numerical id of the block that should be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int, int)
	 */
	public boolean placeSnowCover(Chunk chunk, int id, int x, int y, int z) {
		int meta = chunk.getBlockMetadata(x, y, z);
		return this.placeSnowCover(chunk, id, meta, x, y, z);
	}

	/**
	 * Place a snow covered block at the given relative chunk coordinates.
	 *
	 * @param id The numerical id of the block that should be "stored" inside the snow covered block
	 * @param meta The metadata of the block that should be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int)
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int, int)
	 */
	public boolean placeSnowCover(Chunk chunk, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta)) return false;
		return chunk.setBlockIDWithMetadata(x, y, z, this.id, this.blockToMetadata(id, meta));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockSnowCovered#removeSnow(Chunk, int, int, int, int)
	 */
	public void removeSnow(World world, int metadata, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, this.getStoredBlockId(metadata), this.getStoredBlockMetadata(metadata));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockSnowCovered#removeSnow(World, int, int, int, int)
	 */
	public void removeSnow(Chunk chunk, int metadata, int x, int y, int z) {
		chunk.setBlockIDWithMetadata(x, y, z, this.getStoredBlockId(metadata), this.getStoredBlockMetadata(metadata));
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata, EntityPlayer player, Item item) {
		this.removeSnow(world, metadata, x, y, z);
	}

	@Override
	public ItemStack[] getBreakResult(World world, EnumDropCause dropCause, int x, int y, int z, int meta, TileEntity tileEntity) {
		switch (dropCause) {
			case SILK_TOUCH: {
				return new ItemStack[]{new ItemStack(Block.layerSnow, this.getLayers(meta) + 1)};
			}
			case PICK_BLOCK: {
				return new ItemStack[]{new ItemStack(Block.layerSnow)};
			}
			case PROPER_TOOL: {
				return new ItemStack[]{new ItemStack(Item.ammoSnowball, this.getLayers(meta) + 1)};
			} case IMPROPER_TOOL: {
				return null;
			}
		}
		Block block = Block.getBlock(this.getStoredBlockId(meta));
		return block.getBreakResult(world, dropCause, x, y, z, this.getStoredBlockMetadata(meta), tileEntity);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11) {
			int metadata = world.getBlockMetadata(x, y, z);
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, metadata, null);
			this.removeSnow(world, metadata, x, y, z);
		}
		if (world.getBlockBiome(x, y, z) != null && !world.getBlockBiome(x, y, z).hasSurfaceSnow() && world.seasonManager.getCurrentSeason() != null && world.seasonManager.getCurrentSeason().letWeatherCleanUpSnow) {
			int metadata = world.getBlockMetadata(x, y, z);
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
			this.removeSnow(world, metadata, x, y, z);
		}
	}

	public abstract int getLayers(int metadata);

	public abstract int getRelativeLayers(int metadata);

	// return this.METADATA_TO_BLOCK_ID.getOrDefault(this.getBlockKey(metadata), 0);
	public abstract int getStoredBlockId(int metadata);

	public abstract int getStoredBlockMetadata(int metadata);

	// abstract int getBlockKey(int metadata);

//	private int blockIDToMetadata(int blockID) {
//		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
//			if (entry.getValue() == blockID) {
//				return entry.getKey() * 10;
//			}
//		}
//		return 0;
//	}

	protected abstract int blockToMetadata(int blockID, int metadata);
}
