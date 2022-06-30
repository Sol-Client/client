package io.github.solclient.abstraction.mc.world.item;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.text.Text;
import io.github.solclient.abstraction.mc.world.level.block.BlockType;

public interface ItemStack {

	static @NotNull ItemStack create(@Nullable ItemType type) {
		throw new UnsupportedOperationException();
	}

	static @NotNull ItemStack create(@Nullable ItemType type, int quantity) {
		throw new UnsupportedOperationException();
	}

	static @NotNull ItemStack create(@Nullable BlockType type) {
		throw new UnsupportedOperationException();
	}

	static @NotNull ItemStack create(@Nullable BlockType type, int quantity) {
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
