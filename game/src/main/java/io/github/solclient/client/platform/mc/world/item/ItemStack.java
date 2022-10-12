package io.github.solclient.client.platform.mc.world.item;

import org.jetbrains.annotations.*;

import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.world.level.Level;
import io.github.solclient.client.platform.mc.world.level.block.*;

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

	int getDamageValue();

	int getMaxDamageValue();

	@NotNull Text getDisplayName();

	@Helper
	default @NotNull String getLegacyDisplayName() {
		return getDisplayName().getLegacy();
	}

	boolean canDestroy(@NotNull Level level, @NotNull BlockPos pos);

	boolean canPlaceOn(@NotNull Level level, @NotNull BlockPos pos);

	int getMaxItemUseTime();

}
