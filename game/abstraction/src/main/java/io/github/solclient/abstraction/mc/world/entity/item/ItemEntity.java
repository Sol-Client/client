package io.github.solclient.abstraction.mc.world.entity.item;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.abstraction.mc.world.entity.Entity;
import io.github.solclient.abstraction.mc.world.item.ItemStack;

public interface ItemEntity extends Entity {

	@NotNull ItemStack getItem();

	boolean isOnGround();

	boolean isInWeb();

}
