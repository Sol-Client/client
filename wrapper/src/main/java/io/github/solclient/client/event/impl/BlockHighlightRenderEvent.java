package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.util.hit.BlockHitResult;

@RequiredArgsConstructor
public class BlockHighlightRenderEvent {

	public final BlockHitResult hit;
	public final float partialTicks;
	public boolean cancelled;

}
