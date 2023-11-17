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
	public Map<Integer, Integer> METADATA_TO_BLOCK_ID;

	public BlockSnowCovered(String key, int id, Material material) {
		super(key, id, material);
		this.METADATA_TO_BLOCK_ID = this.initMetadataToBlockID();
	}

	public abstract Map<Integer, Integer> initMetadataToBlockID();

	/**
	 * Check if the block at the given coordinates is capable of being replaced by a snow cover slab.
	 */
	private boolean canReplaceBlock(int id) {
		return this.METADATA_TO_BLOCK_ID.containsValue(id);
	}

	/**
	 * Place a snow covered block at the given coordinates.
	 *
	 * @param id The numerical id of the block that should be "stored" inside the snow cover slab
	 * @return Whether the block was placed successfully
	 * @see BlockSnowCovered#placeSnowCover(Chunk, int, int, int, int)
	 */
	public boolean placeSnowCover(World world, int id, int x, int y, int z) {
		if (!this.canReplaceBlock(id)) return false;
		return world.setBlockAndMetadataWithNotify(x, y, z, this.id, this.blockIDToCoveredID(id));
	}

	/**
	 * Place a snow covered block at the relative chunk given coordinates.
	 *
	 * @param id The numerical id of the block that should be "stored" inside the snow cover slab
	 * @return Whether the block was placed successfully
	 * @see BlockSnowCovered#placeSnowCover(World, int, int, int, int)
	 */
	public boolean placeSnowCover(Chunk chunk, int id, int x, int y, int z) {
		if (!canReplaceBlock(id)) return false;
		return chunk.setBlockIDWithMetadata(x, y, z, this.id, this.blockIDToCoveredID(id));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockSnowCovered#removeSnow(Chunk, int, int, int, int)
	 */
	public void removeSnow(World world, int metadata, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, this.getStoredBlockID(metadata), this.getStoredBlockMetadata(metadata));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockSnowCovered#removeSnow(World, int, int, int, int)
	 */
	public void removeSnow(Chunk chunk, int metadata, int x, int y, int z) {
		chunk.setBlockIDWithMetadata(x, y, z, this.getStoredBlockID(metadata), this.getStoredBlockMetadata(metadata));
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata, EntityPlayer player, Item item) {
		removeSnow(world, metadata, x, y, z);
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
			}
		}
		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.getSavedLightValue(LightLayer.Block, x, y, z) > 11) {
			int metadata = world.getBlockMetadata(x, y, z);
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, metadata, null);
			removeSnow(world, metadata, x, y, z);
		}
		if (world.getBlockBiome(x, y, z) != null && !world.getBlockBiome(x, y, z).hasSurfaceSnow() && world.seasonManager.getCurrentSeason() != null && world.seasonManager.getCurrentSeason().letWeatherCleanUpSnow) {
			int metadata = world.getBlockMetadata(x, y, z);
			this.dropBlockWithCause(world, EnumDropCause.WORLD, x, y, z, world.getBlockMetadata(x, y, z), null);
			removeSnow(world, metadata, x, y, z);
		}
	}

	public int getLayers(int metadata) {
		return metadata % 10;
	}

	public int getRelativeLayers(int metadata) {
		return metadata % 10;
	}

	public int getStoredBlockID(int metadata) {
		return this.METADATA_TO_BLOCK_ID.getOrDefault(this.getBlockKey(metadata), 0);
	}

	private int getStoredBlockMetadata(int metadata) {
		return 0;
	}

	private int getBlockKey(int metadata) {
		return metadata / 10;
	}

	private int blockIDToCoveredID(int blockID) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockID) {
				return entry.getKey() * 10;
			}
		}
		return 0;
	}
}
