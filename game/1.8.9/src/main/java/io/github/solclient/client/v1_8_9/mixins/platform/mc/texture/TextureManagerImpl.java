package io.github.solclient.client.v1_8_9.mixins.platform.mc.texture;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.texture.Texture;
import io.github.solclient.client.platform.mc.texture.TextureManager;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.TextureUtil;

@Mixin(net.minecraft.client.texture.TextureManager.class)
public abstract class TextureManagerImpl implements TextureManager {

	@Override
	public @NotNull CompletableFuture<Texture> download(@NotNull String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void bind(@NotNull Texture texture) {
		GlStateManager.bindTexture(((net.minecraft.client.texture.Texture) texture).getGlId());
	}

	@Override
	public void bind(@NotNull Identifier id) {
		bindTexture((net.minecraft.util.Identifier) id);
	}

	@Shadow
	public abstract void bindTexture(net.minecraft.util.Identifier id);

	@Override
	public void delete(@NotNull Texture texture) {
		GlStateManager.deleteTexture(((net.minecraft.client.texture.Texture) texture).getGlId());
	}

	@Override
	public @Nullable Texture getTexture(Identifier id) {
		return (Texture) getTexture((net.minecraft.util.Identifier) id);
	}

	@Shadow
	public abstract net.minecraft.client.texture.Texture getTexture(net.minecraft.util.Identifier identifier);

}
