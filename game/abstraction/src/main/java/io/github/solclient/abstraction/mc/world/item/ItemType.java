package io.github.solclient.abstraction.mc.world.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.Identifier;

public interface ItemType {

	ItemType BLAZE_POWDER = null;
	ItemType BOW = null;
	ItemType ARROW = null;
	ItemType DIAMOND = null;
	ItemType DIAMOND_SWORD = null;
	ItemType EMERALD = null;
	ItemType ENDER_EYE = null;
	ItemType FISHING_ROD = null;
	ItemType GOLDEN_APPLE = null;
	ItemType IRON_BOOTS = null;
	ItemType IRON_CHESTPLATE = null;
	ItemType IRON_HELMET = null;
	ItemType IRON_LEGGINGS = null;
	ItemType LAVA_BUCKET = null;
	ItemType RED_BED = null;
	ItemType SLIMEBALL = null;
	ItemType STONE_AXE = null;
	ItemType COMPASS = null;

	@NotNull Identifier getId();

}
