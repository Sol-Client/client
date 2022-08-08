package io.github.solclient.client.v1_19_1.mixins.platform.option;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.option.Options;
import io.github.solclient.client.platform.mc.option.Perspective;
import net.minecraft.client.option.GameOptions;

@Mixin(GameOptions.class)
public class OptionsImpl implements Options {

	@Shadow
	public String language;

	@Override
	public float mouseSensitivity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setMouseSensitivity(float sensitivity) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean invertMouse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean debugOverlay() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public @NotNull Perspective perspective() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int ordinalPerspective() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setPerspective(@NotNull Perspective perspective) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setOrdinalPerspective(int perspective) {
		// TODO Auto-generated method stub

	}

	@Override
	public @NotNull KeyBinding[] keys() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding forwardsKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding backwardsKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding strafeLeftKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding strafeRightKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding attackKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding useKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding jumpKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public @NotNull KeyBinding sprintKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void overwriteSprintKey(@NotNull KeyBinding sprint) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hideGui() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addKey(@NotNull KeyBinding key) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeKey(@NotNull KeyBinding key) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean smoothCamera() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSmoothCamera(boolean camera) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setKey(@NotNull KeyBinding binding, int code) {
		// TODO Auto-generated method stub

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public @NotNull String languageCode() {
		return language;
	}

}
