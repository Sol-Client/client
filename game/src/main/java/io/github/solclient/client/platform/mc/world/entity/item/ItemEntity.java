package io.github.solclient.client.platform.mc.world.entity.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.item.ItemStack;

public interface ItemEntity extends Entity {

	@NotNull ItemStack getItem();

}
