package io.github.solclient.abstraction.mc.world.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.abstraction.mc.world.level.block.BlockType;

public interface ItemStack {

	static ItemStack create(ItemType type) {
		throw new UnsupportedOperationException();
	}

	static ItemStack create(ItemType type, int quantity) {
		throw new UnsupportedOperationException();
	}

	static ItemStack create(BlockType type) {
		throw new UnsupportedOperationException();
	}

	static ItemStack create(BlockType type, int quantity) {
		throw new UnsupportedOperationException();
	}

	int getQuantity();

	void setQuantity(int quantity);

	@NotNull ItemType getType();

	int getDamage();

	int getMaxDamage();

	@NotNull Text getDisplayName();

	@NotNull String getLegacyDisplayName();

}
