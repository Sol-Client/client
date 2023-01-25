package io.github.solclient.client.mod.impl.chunkanimator;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mixin.client.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.util.data.EasingFunction;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.*;

// Based on lumien231's chunk animator.
public class ChunkAnimatorMod extends Mod implements PrimaryIntegerSettingMod {

	public static ChunkAnimatorMod instance;
	public static boolean enabled;

	@Expose
	@Option
	@Slider(min = 0, max = 5, step = 0.5F, format = "sol_client.slider.seconds")
	private float duration = 1;
	@Expose
	@Option
	private EasingFunction animation = EasingFunction.SINE;

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	@Override
	public String getId() {
		return "chunk_animator";
	}

	@Override
	public String getCredit() {
		return I18n.translate("sol_client.mod.screen.originally_by", "lumien231");
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
		BuiltChunkData chunk = (BuiltChunkData) event.chunk;

		if (chunk.isAnimationComplete())
			return;

		long time = chunk.getAnimationStart();
		long now = System.currentTimeMillis();

		if (time == -1L) {
			chunk.setAnimationStart(now);
			time = now;
		}

		long passedTime = now - time;

		if (passedTime < getDuration()) {
			int chunkY = event.chunk.getPos().getY();
			GlStateManager.translate(0, -chunkY + ease(passedTime, 0, chunkY, getDuration()), 0);
		} else
			chunk.skipAnimation();
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

	public void notifyPlace(BlockPos pos) {
		// not rendered
		MixinBuiltChunkStorage storage = (MixinBuiltChunkStorage) (((MixinWorldRenderer) mc.worldRenderer).getChunks());
		BuiltChunkData data = (BuiltChunkData) storage.getChunk(pos);
		if (data != null && data.getAnimationStart() == -1 && !data.isAnimationComplete())
			data.skipAnimation();
	}

}
