package io.github.solclient.client.v1_19_2.mixins.platform.mc.option;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.option.Options;
import io.github.solclient.client.platform.mc.option.Perspective;
import io.github.solclient.client.v1_19_2.mixins.accessor.option.KeyBindingAccessor;
import net.minecraft.client.option.*;
import net.minecraft.client.util.InputUtil;

@Mixin(GameOptions.class)
public abstract class OptionsImpl implements Options {

	@Override
	public double mouseSensitivity() {
		return getMouseSensitivity().getValue();
	}

	@Override
	public void setMouseSensitivity(double sensitivity) {
		getMouseSensitivity().setValue(sensitivity);
	}

	@Shadow
	public abstract SimpleOption<Double> getMouseSensitivity();

	@Override
	public boolean invertMouse() {
		return getInvertYMouse().getValue();
	}

	@Shadow
	public abstract SimpleOption<Boolean> getInvertYMouse();

	@Override
	public boolean debugOverlay() {
		return debugEnabled;
	}

	@Shadow
	public boolean debugEnabled;

	@Override
	public @NotNull Perspective perspective() {
		return (Perspective) (Object) getPerspective();
	}

	@Override
	public int ordinalPerspective() {
		return getPerspective().ordinal();
	}

	@Shadow
	public abstract net.minecraft.client.option.Perspective getPerspective();

	@Override
	public void setPerspective(@NotNull Perspective perspective) {
		setPerspective((net.minecraft.client.option.Perspective) (Object) perspective);
	}

	@Override
	public void setOrdinalPerspective(int perspective) {
		setPerspective(net.minecraft.client.option.Perspective.values()[perspective]);
	}

	@Shadow
	public abstract void setPerspective(net.minecraft.client.option.Perspective perspective);

	@Override
	public @NotNull KeyBinding[] keys() {
		return (KeyBinding[]) allKeys;
	}

	@Override
	public @NotNull KeyBinding forwardsKey() {
		return (KeyBinding) forwardKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding forwardKey;

	@Override
	public @NotNull KeyBinding backwardsKey() {
		return (KeyBinding) backKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding backKey;

	@Override
	public @NotNull KeyBinding strafeLeftKey() {
		return (KeyBinding) leftKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding leftKey;

	@Override
	public @NotNull KeyBinding strafeRightKey() {
		return (KeyBinding) rightKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding rightKey;

	@Override
	public @NotNull KeyBinding attackKey() {
		return (KeyBinding) attackKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding attackKey;

	@Override
	public @NotNull KeyBinding useKey() {
		return (KeyBinding) useKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding useKey;

	@Override
	public @NotNull KeyBinding jumpKey() {
		return (KeyBinding) jumpKey;
	}

	@Shadow
	public @Final net.minecraft.client.option.KeyBinding jumpKey;

	@Override
	public @NotNull KeyBinding sprintKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Shadow
	public @Mutable @Final net.minecraft.client.option.KeyBinding sprintKey;

	@Override
	public void overwriteSprintKey(@NotNull KeyBinding sprint) {
		sprintKey = (net.minecraft.client.option.KeyBinding) sprint;
	}

	@Override
	public boolean hideGui() {
		return hudHidden;
	}

	@Shadow
	public boolean hudHidden;

	@Override
	public void addKey(@NotNull KeyBinding key) {
		Map<String, Integer> sortOrder = KeyBindingAccessor.getCategoryOrderMap();
		if(!sortOrder.containsKey(key.getKeyCategory())) {
			int order = KeyBindingAccessor.getCategoryOrderMap().values().stream().max(Integer::compareTo).orElse(0) + 1;
			sortOrder.put(key.getKeyCategory(), order);
		}

		// I wanted to use a fancy bit-shifting array growth thing based on the JDK
		// impl, then I realised that the array cannot contain nulls :(.
		allKeys = ArrayUtils.add(allKeys, (net.minecraft.client.option.KeyBinding) key);
	}

	@Override
	public void removeKey(@NotNull KeyBinding key) {
		allKeys = ArrayUtils.removeElement(allKeys, (net.minecraft.client.option.KeyBinding) key);
	}

	@Shadow
	public @Mutable @Final net.minecraft.client.option.KeyBinding[] allKeys;

	@Override
	public boolean smoothCamera() {
		return smoothCameraEnabled;
	}

	@Override
	public void setSmoothCamera(boolean camera) {
		smoothCameraEnabled = camera;
	}

	@Shadow
	public boolean smoothCameraEnabled;

	@Override
	public void setKey(@NotNull KeyBinding binding, int code, int scancode) {
		setKeyCode((net.minecraft.client.option.KeyBinding) binding, InputUtil.fromKeyCode(code, scancode));
	}

	@Override
	public void setMouseButton(@NotNull KeyBinding binding, int button) {
		setKeyCode((net.minecraft.client.option.KeyBinding) binding, InputUtil.Type.MOUSE.createFromCode(button));
	}

	@Override
	public void unbindKey(@NotNull KeyBinding binding) {
		setKeyCode((net.minecraft.client.option.KeyBinding) binding, InputUtil.UNKNOWN_KEY);
	}

	@Shadow
	public abstract void setKeyCode(net.minecraft.client.option.KeyBinding keyBinding, InputUtil.Key key);

	@Override
	public void saveFile() {
		write();
	}

	@Shadow
	public abstract void write();

	@Override
	public @NotNull String languageCode() {
		return language;
	}

	@Shadow
	public String language;

}
