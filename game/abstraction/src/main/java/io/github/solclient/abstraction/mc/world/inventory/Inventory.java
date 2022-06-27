package io.github.solclient.abstraction.mc.world.inventory;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.item.ItemStack;

public interface Inventory {

	@NotNull ItemStack[] getArmour();

	@NotNull ItemStack getArmour(int slot);

	@NotNull ItemStack getCurrentItem();

	@NotNull ItemStack getItem(int slot);

}
