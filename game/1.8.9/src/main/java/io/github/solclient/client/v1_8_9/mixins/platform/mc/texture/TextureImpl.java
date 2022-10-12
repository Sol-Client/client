package io.github.solclient.client.v1_8_9.mixins.platform.mc.texture;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.texture.Texture;
import net.minecraft.client.gui.DrawableHelper;

@Mixin(Texture.class)
public interface TextureImpl extends net.minecraft.client.texture.Texture {

}

@Mixin(Texture.class)
interface TextureImpl$Static {

	@Overwrite(remap = false)
	public static @NotNull Identifier get(@NotNull String name) {
		switch(name) {
			case "ICONS_ID":
				return (Identifier) DrawableHelper.GUI_ICONS_TEXTURE;
			case "INVENTORY_ID":
			case "MOB_EFFECTS_ATLAS_ID":
				return Identifier.minecraft("textures/gui/container/inventory.png");
			}

		throw new IllegalArgumentException(name);
	}

}