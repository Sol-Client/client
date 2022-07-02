package io.github.solclient.abstraction.mc.world.level.block;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.Helper;
import io.github.solclient.abstraction.mc.Identifier;
import io.github.solclient.abstraction.mc.world.item.ItemType;

public interface BlockType {

	BlockType AIR = null;
	BlockType ANVIL = null;
	BlockType BEACON = null;
	BlockType CRAFTING_TABLE = null;
	BlockType DARK_OAK_DOOR = null;
	BlockType DIRT = null;
	BlockType GRASS_BLCOK = null;
	BlockType IRON_BARS = null;
	BlockType JUKEBOX = null;
	BlockType OAK_DOOR = null;
	BlockType SOUL_SAND = null;
	BlockType TNT = null;

	@NotNull Identifier getId();

	@NotNull ItemType toItem();

	@Helper
	void fillBox();

	@Helper
	void strokeBox();

}
