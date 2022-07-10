package io.github.solclient.client.platform.mc.world.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.Identifier;

public interface ItemType {

	ItemType BLAZE_POWDER = null,
			BOW = null,
			ARROW = null,
			DIAMOND = null,
			DIAMOND_SWORD = null,
			EMERALD = null,
			ENDER_EYE = null,
			FISHING_ROD = null,
			GOLDEN_APPLE = null,
			IRON_BOOTS = null,
			IRON_CHESTPLATE = null,
			IRON_HELMET = null,
			IRON_LEGGINGS = null,
			LAVA_BUCKET = null,
			RED_BED = null,
			SLIMEBALL = null,
			STONE_AXE = null,
			COMPASS = null;

	@Helper
	static ItemType bukkit(String id) {
		throw new UnsupportedOperationException();
	}

	@NotNull Identifier getId();

}
