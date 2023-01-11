package io.github.solclient.client.mod.impl.cosmetica;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

@RequiredArgsConstructor
abstract class CosmeticLayer implements LayerRenderer<AbstractClientPlayer> {

	protected final RenderPlayer parent;

	@Override
	public boolean shouldCombineTextures() {
		return false;
	}

}
