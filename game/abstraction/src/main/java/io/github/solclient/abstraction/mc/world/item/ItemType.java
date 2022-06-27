package io.github.solclient.abstraction.mc.world.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.Identifier;

public interface ItemType {

	ItemType IRON_HELMET = null;
	ItemType IRON_CHESTPLATE = null;
	ItemType IRON_LEGGINGS = null;
	ItemType IRON_BOOTS = null;
	ItemType DIAMOND_SWORD = null;
	ItemType DIAMOND = null;
	ItemType EMERALD = null;

	@NotNull Identifier getId();

}
