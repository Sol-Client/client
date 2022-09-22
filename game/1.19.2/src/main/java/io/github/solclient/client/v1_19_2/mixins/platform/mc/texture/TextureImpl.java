package io.github.solclient.client.v1_19_2.mixins.platform.mc.texture;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.texture.Texture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.entity.effect.StatusEffects;

@Mixin(AbstractTexture.class)
public class TextureImpl implements Texture {

}

@Mixin(Texture.class)
interface TextureImpl$Static {

	@Overwrite(remap = false)
	public static @NotNull Identifier get(@NotNull String name) {
		switch(name) {
			case "ICONS_ID":
				return (Identifier) DrawableHelper.GUI_ICONS_TEXTURE;
			case "INVENTORY_ID":
				return (Identifier) AbstractInventoryScreen.BACKGROUND_TEXTURE;
			case "MOB_EFFECTS_ATLAS_ID":
				return (Identifier) MinecraftClient.getInstance().getStatusEffectSpriteManager()
						.getSprite(StatusEffects.SPEED).getAtlas().getId();
			}

		throw new IllegalArgumentException(name);
	}

}