package io.github.solclient.client.platform.mc.world.inventory;

import org.jetbrains.annotations.Nullable;

import io.github.solclient.client.platform.mc.world.item.ItemStack;

public interface Inventory {

	@Nullable ItemStack getArmour(int slot);

	@Nullable ItemStack getMainHand();

	@Nullable ItemStack getItem(int slot);

}
