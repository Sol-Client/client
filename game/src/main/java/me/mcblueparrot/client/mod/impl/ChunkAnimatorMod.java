package me.mcblueparrot.client.mod.impl;

import java.util.Map;
import java.util.WeakHashMap;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PreRenderChunkEvent;
import me.mcblueparrot.client.event.impl.RenderChunkPositionEvent;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.data.EasingFunction;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;

// Based on lumien231's chunk animator.
public class ChunkAnimatorMod extends Mod implements PrimaryIntegerSettingMod {

	private final Map<RenderChunk, Long> chunks = new WeakHashMap<>();

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
		if(chunks.containsKey(event.chunk)) {
			long time = chunks.get(event.chunk);
			long now = System.currentTimeMillis();

			if(time == -1L) {
				chunks.put(event.chunk, now);
				time = now;
			}

			long passedTime = now - time;

			if(passedTime < getDuration()) {
				int chunkY = event.chunk.getPosition().getY();
				GlStateManager.translate(0, -chunkY + ease(passedTime, 0, chunkY, getDuration()), 0);
			}
		}
	}

	@EventHandler
	public void setPosition(RenderChunkPositionEvent event) {
		if(mc.thePlayer != null) {
			chunks.put(event.chunk, -1L);
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
