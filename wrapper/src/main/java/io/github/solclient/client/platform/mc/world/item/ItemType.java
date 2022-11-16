package io.github.solclient.client.platform.mc.world.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.Helper;

public interface ItemType {

	ItemType BLAZE_POWDER = get("BLAZE_POWDER"),
			BOW = get("BOW"),
			ARROW = get("ARROW"),
			DIAMOND = get("DIAMOND"),
			DIAMOND_SWORD = get("DIAMOND_SWORD"),
			EMERALD = get("EMERALD"),
			ENDER_EYE = get("ENDER_EYE"),
			FISHING_ROD = get("FISHING_ROD"),
			GOLDEN_APPLE = get("GOLDEN_APPLE"),
			IRON_BOOTS = get("IRON_BOOTS"),
			IRON_CHESTPLATE = get("IRON_CHESTPLATE"),
			IRON_HELMET = get("IRON_HELMET"),
			IRON_LEGGINGS = get("IRON_LEGGINGS"),
			LAVA_BUCKET = get("LAVA_BUCKET"),
			RED_BED = get("RED_BED"),
			SLIMEBALL = get("SLIMEBALL"),
			STONE_AXE = get("STONE_AXE"),
			COMPASS = get("COMPASS");

	@Helper
	static @NotNull ItemType bukkit(@NotNull String id) {
		throw new UnsupportedOperationException();
	}

	static @NotNull ItemType get(@NotNull String name) {
		throw new UnsupportedOperationException();
	}

}
