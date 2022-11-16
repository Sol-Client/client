package io.github.solclient.client.platform.mc.texture;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.resource.Identifier;

public interface Texture {

	public static final Identifier ICONS_ID = get("ICONS_ID"),
			INVENTORY_ID = get("INVENTORY_ID"),
			MOB_EFFECTS_ATLAS_ID = get("MOB_EFFECTS_ATLAS_ID");

	public static @NotNull Identifier get(@NotNull String name) {
		throw new UnsupportedOperationException();
	}

}
