package io.github.solclient.abstraction.mc.world.level.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.Identifier;
import io.github.solclient.abstraction.mc.world.item.ItemType;

public interface BlockType {

	BlockType BEACON = null;

	@NotNull Identifier getId();

}
