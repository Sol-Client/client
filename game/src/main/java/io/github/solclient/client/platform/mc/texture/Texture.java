package io.github.solclient.client.platform.mc.texture;

import org.jetbrains.annotations.NotNull;

import io.github.solclient.client.platform.mc.resource.Identifier;

public interface Texture {

	public static final Identifier ICONS_ID = null,
			INVENTORY_ID = null,
			MOB_EFFECTS_ATLAS_ID = null;

	@NotNull Identifier getId();

}
