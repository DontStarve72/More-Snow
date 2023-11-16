package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;

public class BlockSnowCover extends Block {
	public BlockSnowCover(String key, int id, Material material) {
		super(key, id, material);
		this.setTickOnLoad(true);
	}

	// TODO
}
