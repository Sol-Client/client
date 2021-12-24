package me.mcblueparrot.client.mod.impl;

import java.util.Map;
import java.util.WeakHashMap;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.PreRenderChunkEvent;
import me.mcblueparrot.client.event.impl.RenderChunkPositionEvent;
import me.mcblueparrot.client.lib.penner.easing.Back;
import me.mcblueparrot.client.lib.penner.easing.Bounce;
import me.mcblueparrot.client.lib.penner.easing.Circ;
import me.mcblueparrot.client.lib.penner.easing.Cubic;
import me.mcblueparrot.client.lib.penner.easing.Elastic;
import me.mcblueparrot.client.lib.penner.easing.Expo;
import me.mcblueparrot.client.lib.penner.easing.Linear;
import me.mcblueparrot.client.lib.penner.easing.Quad;
import me.mcblueparrot.client.lib.penner.easing.Quart;
import me.mcblueparrot.client.lib.penner.easing.Quint;
import me.mcblueparrot.client.lib.penner.easing.Sine;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;

// Read this class for long enough and the world "chunk" will lose meaning.
public class ChunkAnimatorMod extends Mod implements PrimaryIntegerSettingMod {

	private Map<RenderChunk, Data> chunks = new WeakHashMap<>();
	@Expose
	@ConfigOption("Duration")
	@Slider(min = 0, max = 5, step = 0.5F, suffix = "s")
	private float duration = 1;
	@Expose
	@ConfigOption("Function")
	private EasingFunction animation = EasingFunction.SINE;

	public ChunkAnimatorMod() {
		super("Chunk Animator", "chunk_animator", "Animate world loading.", ModCategory.VISUAL);
	}

	@EventHandler
	public void preRenderChunk(PreRenderChunkEvent event) {
		if(chunks.containsKey(event.chunk)) {
			Data chunk = chunks.get(event.chunk);
			long time = chunk.time;
			if(time == -1L) {
				time = chunk.time = System.currentTimeMillis();
			}
			else if(time == -2L) {
				return;
			}

			long passedTime = System.currentTimeMillis() - time;

			if(passedTime < (duration * 1000)) {
				int chunkY = event.chunk.getPosition().getY();
				GlStateManager.translate(0, -chunkY + ease(passedTime, 0, chunkY, duration * 1000), 0);
			}
		}
	}

	@EventHandler
	public void setPosition(RenderChunkPositionEvent event) {
		if(mc.thePlayer != null) {
			Data data = new Data();
			data.time = -1L;

			chunks.put(event.chunk, data);
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


	private static class Data {

		long time;

	}

	public enum EasingFunction {
		LINEAR("Linear"),
		QUAD("Quad"),
		CUBIC("Cubic"),
		QUART("Quart"),
		QUINT("Quint"),
		EXPO("Expo"),
		SINE("Sine"),
		CIRC("Circ"),
		BACK("Back"),
		BOUNCE("Bounce"),
		ELASTIC("Elastic");

		private String name;

		EasingFunction(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public float ease(float t, float b, float c, float d) {
		switch(animation) {
			case LINEAR:
				return Linear.easeNone(t, b, c, d);
			case QUAD:
				return Quad.easeOut(t, b, c, d);
			case CUBIC:
				return Cubic.easeOut(t, b, c, d);
			case QUART:
				return Quart.easeOut(t, b, c, d);
			case QUINT:
				return Quint.easeOut(t, b, c, d);
			case EXPO:
				return Expo.easeOut(t, b, c, d);
			case SINE:
				return Sine.easeOut(t, b, c, d);
			case CIRC:
				return Circ.easeOut(t, b, c, d);
			case BACK:
				return Back.easeOut(t, b, c, d);
			case BOUNCE:
				return Bounce.easeOut(t, b, c, d);
			case ELASTIC:
				return Elastic.easeOut(t, b, c, d);
			default:
				return 0;
		}
	}

}
