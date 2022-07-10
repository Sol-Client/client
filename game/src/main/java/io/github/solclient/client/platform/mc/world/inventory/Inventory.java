package io.github.solclient.client.platform.mc.world.inventory;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.item.ItemStack;

public interface Inventory {

	@NotNull ItemStack[] getArmour();

	@NotNull ItemStack getArmour(int slot);

	@NotNull ItemStack getMainHand();

	@NotNull ItemStack getItem(int slot);

}
