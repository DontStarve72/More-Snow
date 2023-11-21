package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.entity.player.EntityPlayer;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.chunk.Chunk;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;

public abstract class BlockSnowy extends Block { // extends BlockLayerSnow {
	protected final Map<Integer, Integer> METADATA_TO_BLOCK_ID;
	public final int maxLayers;
	protected final boolean fourLayers;
	private int metadataID = 0;
	private final boolean weirdShape;

	public BlockSnowy(String key, int id, Material material, int minId, int maxId, int[] excludedIds, boolean fourLayers, boolean weirdShape) {
		super(key, id, material);
		this.METADATA_TO_BLOCK_ID = this.initMetadataToBlockId(minId, maxId, excludedIds);
		this.fourLayers = fourLayers;
		if (fourLayers) {
			this.maxLayers = 3;
		} else {
			this.maxLayers = 7;
		}
		this.weirdShape = weirdShape;
	}

	protected Map<Integer, Integer> initMetadataToBlockId(int minId, int maxId, int[] excludedIds) {
		Hashtable<Integer, Integer> tmp = new Hashtable<>();
		for (int id = minId; id <= maxId; ++id) {
			if (blocksList[id] == null || ArrayUtils.contains(excludedIds, id)) continue;
			tmp.put(this.metadataID++, id);
		}
		return Collections.unmodifiableMap(tmp);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(WorldSource blockAccess, int x, int y, int z, int side, int metadata) {
		if (!this.weirdShape) {
			int neighborId = blockAccess.getBlockId(x, y, z);
			Block neighborBlock = Block.getBlock(neighborId);
			int neighborMetadata = blockAccess.getBlockMetadata(x, y, z);

			int neighborLayers = -1;
			if (neighborBlock instanceof BlockSnowy) {
				BlockSnowy blockSnowy = (BlockSnowy) neighborBlock;
				neighborLayers = blockSnowy.getRelativeLayers(neighborMetadata);
			} else if (neighborId == Block.layerSnow.id) {
				neighborLayers = neighborMetadata & 0b111;
			}

			if (neighborLayers >= this.getRelativeLayers(metadata)) {
				return false;
			}
		}

		return super.shouldSideBeRendered(blockAccess, x, y, z, side);
	}

	/**
	 * Check a given block id with given metadata is capable of being replaced by a snow covered block.
	 */
	public boolean canReplaceBlock(int id, int metadata) {
		return this.METADATA_TO_BLOCK_ID.containsValue(id);
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id The block id to be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockSnowy#tryMakeSnowy(Chunk, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(World, int, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(Chunk, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(World world, int id, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return this.tryMakeSnowy(world, id, meta, x, y, z);
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id The block id to be "stored" inside the snow covered block
	 * @param meta The metadata to be "stored"
	 * @return Whether the block was placed successfully
	 * @see BlockSnowy#tryMakeSnowy(World, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(Chunk, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(Chunk, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(World world, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta)) return false;
		return world.setBlockAndMetadataWithNotify(x, y, z, this.id, this.blockToMetadata(id, meta));
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id The block id to be "stored" inside the snow covered block
	 * @return Whether the block was placed successfully
	 * @see BlockSnowy#tryMakeSnowy(World, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(World, int, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(Chunk, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(Chunk chunk, int id, int x, int y, int z) {
		int meta = chunk.getBlockMetadata(x, y, z);
		return this.tryMakeSnowy(chunk, id, meta, x, y, z);
	}

	/**
	 * Place a snow covered variant of a block at the given coordinates.
	 *
	 * @param id The block id to be "stored" inside the snow covered block
	 * @param meta The metadata to be "stored"
	 * @return Whether the block was placed successfully
	 * @see BlockSnowy#tryMakeSnowy(World, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(Chunk, int, int, int, int)
	 * @see BlockSnowy#tryMakeSnowy(World, int, int, int, int, int)
	 */
	public boolean tryMakeSnowy(Chunk chunk, int id, int meta, int x, int y, int z) {
		if (!this.canReplaceBlock(id, meta)) return false;
		return chunk.setBlockIDWithMetadata(x, y, z, this.id, this.blockToMetadata(id, meta));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockSnowy#removeSnow(Chunk, int, int, int, int)
	 */
	public void removeSnow(World world, int metadata, int x, int y, int z) {
		world.setBlockAndMetadataWithNotify(x, y, z, this.getStoredBlockId(metadata), this.getStoredBlockMetadata(metadata));
	}

	/**
	 * Remove the snow covered block and replace it with its actual block
	 *
	 * @param metadata The metadata of the snow covered block
	 * @see BlockSnowy#removeSnow(World, int, int, int, int)
	 */
	public void removeSnow(Chunk chunk, int metadata, int x, int y, int z) {
		chunk.setBlockIDWithMetadata(x, y, z, this.getStoredBlockId(metadata), this.getStoredBlockMetadata(metadata));
	}

	// Vanilla accumulate function but get layers from function rather than directly from metadata
	public void accumulate(World world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		int layers = this.getLayers(metadata);
		if (layers >= this.maxLayers) {
			return;
		}
		int relativeLayers = this.getRelativeLayers(metadata);

		boolean posXValid = world.isBlockOpaqueCube(x + 1, y, z) || isSnow(world, x + 1, y, z) && getOthersLayers(world, x + 1, y, z) >= relativeLayers;
		if (!posXValid) {
			return;
		}
		boolean posZValid = world.isBlockOpaqueCube(x, y, z + 1) || isSnow(world, x, y, z + 1) && getOthersLayers(world, x, y, z + 1) >= relativeLayers;
		if (!posZValid) {
			return;
		}
		boolean negXValid = world.isBlockOpaqueCube(x - 1, y, z) || isSnow(world, x - 1, y, z) && getOthersLayers(world, x - 1, y, z) >= relativeLayers;
		if (!negXValid) {
			return;
		}
		boolean negZValid = world.isBlockOpaqueCube(x, y, z - 1) || isSnow(world, x, y, z - 1) && getOthersLayers(world, x, y, z - 1) >= relativeLayers;
		if (!negZValid) {
			return;
		}

		world.setBlockMetadataWithNotify(x, y, z, metadata + 1);
		world.markBlockNeedsUpdate(x, y, z);
	}

	/**
	 * @return True if the block at the given coordinates is either snow covered or a snow layer
	 */
	private boolean isSnow(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);

		return ArrayUtils.contains(MSBlocks.blockIds, id) || id == Block.layerSnow.id;
	}

	/**
	 * @return How many layers a block at the given coordinates has, presuming the block is snowy or a layer block
	 */
	private int getOthersLayers(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		int metadata = world.getBlockMetadata(x, y, z);

		if (block instanceof BlockSnowy) {
			return ((BlockSnowy) block).getRelativeLayers(metadata);
		}

		return metadata;
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
			default: { // Drop the underlying block if it's destroyed by WORLD or EXPLOSION
				Block block = Block.getBlock(this.getStoredBlockId(meta));
				return block.getBreakResult(world, dropCause, x, y, z, this.getStoredBlockMetadata(meta), tileEntity);
			}
		}
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

	public int getLayers(int metadata) {
		return metadata & this.maxLayers;
	}

	public int getRelativeLayers(int metadata) {
		int layers = getLayers(metadata);
		if (this.fourLayers) {
			return layers + 4;
		}
		return layers;
	}

	public int getStoredBlockId(int metadata) {
		int blockKey = (metadata >> 4 ) & 0b00001111;
		return this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	public int getStoredBlockMetadata(int metadata) {
		return 0;
	}

	protected int blockToMetadata(int blockId, int metadata) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << 4;
			}
		}

		return 0;
	}
}
