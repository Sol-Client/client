package io.github.solclient.client.v1_19_2.mixins.platform.mc.texture;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.texture.*;
import net.minecraft.client.texture.AbstractTexture;

@Mixin(net.minecraft.client.texture.TextureManager.class)
public abstract class TextureManagerImpl implements TextureManager {

	@Override
	public @NotNull CompletableFuture<Texture> download(@NotNull String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bind(@NotNull Texture texture) {
		((AbstractTexture) texture).bindTexture();
	}

	@Override
	public void bind(@NotNull Identifier id) {
		RenderSystem.setShaderTexture(0, (net.minecraft.util.Identifier) id);
	}

	@Override
	public void delete(@NotNull Texture texture) {
		closeTexture(null, (AbstractTexture) texture);
	}

	@Shadow
	protected abstract void closeTexture(net.minecraft.util.Identifier identifier, AbstractTexture abstractTexture);

	@Override
	public @Nullable Texture getTexture(Identifier id) {
		return (Texture) getTexture((net.minecraft.util.Identifier) id);
	}

	@Shadow
	public abstract AbstractTexture getTexture(net.minecraft.util.Identifier identifier);

}
