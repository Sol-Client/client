package io.github.solclient.client.v1_8_9.mixins.platform.mc.option;

import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.option.Options;
import io.github.solclient.client.platform.mc.option.Perspective;
import io.github.solclient.client.v1_8_9.platform.mc.option.PerspectiveImpl;
import net.minecraft.client.options.GameOptions;

@Mixin(GameOptions.class)
public abstract class OptionsImpl implements Options {

	@Shadow
	public String language;

	@Override
	public double mouseSensitivity() {
		return sensitivity;
	}

	@Override
	public void setMouseSensitivity(double sensitivity) {
		this.sensitivity = (float) sensitivity;
	}

	@Shadow
	public float sensitivity;

	@Override
	public boolean invertMouse() {
		return invertYMouse;
	}

	public boolean invertYMouse;

	@Override
	public boolean debugOverlay() {
		return debugEnabled;
	}

	public boolean debugEnabled;

	@Override
	public @NotNull Perspective perspective() {
		return PerspectiveImpl.values()[ordinalPerspective()];
	}

	@Override
	public int ordinalPerspective() {
		return perspective;
	}

	@Override
	public void setPerspective(@NotNull Perspective perspective) {
		setOrdinalPerspective(perspective.enumOrdinal());
	}

	@Override
	public void setOrdinalPerspective(int perspective) {
		this.perspective = perspective;
	}

	public int perspective;

	@Override
	public @NotNull KeyBinding[] keys() {
		return (KeyBinding[]) keysAll;
	}

	@Override
	public @NotNull KeyBinding forwardsKey() {
		return (KeyBinding) keyForward;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyForward;

	@Override
	public @NotNull KeyBinding backwardsKey() {
		return (KeyBinding) keyBack;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyBack;

	@Override
	public @NotNull KeyBinding strafeLeftKey() {
		return (KeyBinding) keyLeft;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyLeft;

	@Override
	public @NotNull KeyBinding strafeRightKey() {
		return (KeyBinding) keyRight;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyRight;

	@Override
	public @NotNull KeyBinding attackKey() {
		return (KeyBinding) keyAttack;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyAttack;

	@Override
	public @NotNull KeyBinding useKey() {
		return (KeyBinding) keyUse;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyUse;

	@Override
	public @NotNull KeyBinding jumpKey() {
		return (KeyBinding) keyUse;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keyJump;

	@Override
	public @NotNull KeyBinding sprintKey() {
		return (KeyBinding) keySprint;
	}

	@Override
	public void overwriteSprintKey(@NotNull KeyBinding sprint) {
		keySprint = (net.minecraft.client.options.KeyBinding) sprint;
	}

	@Shadow
	public net.minecraft.client.options.KeyBinding keySprint;

	@Override
	public boolean hideGui() {
		return hudHidden;
	}

	public boolean hudHidden;

	@Override
	public void addKey(@NotNull KeyBinding key) {
		keysAll = ArrayUtils.add(keysAll, (net.minecraft.client.options.KeyBinding) key);
	}

	@Override
	public void removeKey(@NotNull KeyBinding key) {
		keysAll = ArrayUtils.removeElement(keysAll, (net.minecraft.client.options.KeyBinding) key);
	}

	@Shadow
	public @Mutable @Final net.minecraft.client.options.KeyBinding[] keysAll;

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
	public void setMouseButton(@NotNull KeyBinding binding, int button) {
		setKeyBindingCode((net.minecraft.client.options.KeyBinding) binding, button - 100);
	}

	@Override
	public void setKey(@NotNull KeyBinding binding, int code, int scancode) {
		setKeyBindingCode((net.minecraft.client.options.KeyBinding) binding, code);
	}

	@Override
	public void unbindKey(@NotNull KeyBinding binding) {
		setKeyBindingCode((net.minecraft.client.options.KeyBinding) binding, Keyboard.KEY_NONE);
	}

	@Shadow
	public abstract void setKeyBindingCode(net.minecraft.client.options.KeyBinding binding, int code);

	@Override
	public void saveFile() {
		save();
	}

	@Shadow
	public abstract void save();

	@Override
	public @NotNull String languageCode() {
		return language.toLowerCase(); // for en_us instead of en_US
	}

}
