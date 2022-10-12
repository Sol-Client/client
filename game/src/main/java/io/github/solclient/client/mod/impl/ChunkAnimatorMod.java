package io.github.solclient.client.mod.impl;

import java.util.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.world.level.chunk.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.world.level.chunk.CompiledChunk;
import io.github.solclient.client.util.data.EasingFunction;

// Based on lumien231's chunk animator.
public class ChunkAnimatorMod extends Mod implements PrimaryIntegerSettingMod {

	public static final ChunkAnimatorMod INSTANCE = new ChunkAnimatorMod();

	private final Map<CompiledChunk, Long> chunks = new WeakHashMap<>();

	@Expose
	@Option
	@Slider(min = 0, max = 5, step = 0.5F, format = "sol_client.slider.seconds")
	private float duration = 1;
	@Expose
	@Option
	private EasingFunction animation = EasingFunction.SINE;

	@Override
	public String getId() {
		return "chunk_animator";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.VISUAL;
	}

	public int getDuration() {
		return (int) (duration * 1000);
	}

	@EventHandler
	public void preRenderChunk(PreRenderChunkEvent event) {
		if(chunks.containsKey(event.getChunk())) {
			long time = chunks.get(event.getChunk());
			long now = System.currentTimeMillis();

			if(time == -1L) {
				chunks.put(event.getChunk(), now);
				time = now;
			}

			long passedTime = now - time;

			if(passedTime < getDuration()) {
				int chunkY = event.getChunk().getPos().y();
				GlStateManager.translate(0, -chunkY + ease(passedTime, 0, chunkY, getDuration()), 0);
			}
		}
	}

	@EventHandler
	public void setPosition(CompiledChunkPositionEvent event) {
		if(mc.getPlayer() != null) {
			chunks.put(event.getChunk(), -1L);
		}
	}

	@Override
	public void decrement() {
		duration = Math.max(0, duration - 0.5F);
	}

	@Override
	public void increment() {
		duration = Math.min(5, duration + 0.5F);
	}

	public float ease(float t, float b, float c, float d) {
		return animation.ease(t, b, c, d);
	}

}
