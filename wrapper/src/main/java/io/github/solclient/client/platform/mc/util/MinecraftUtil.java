package io.github.solclient.client.platform.mc.util;

import org.jetbrains.annotations.*;

import io.github.solclient.client.platform.Helper;
import io.github.solclient.client.platform.mc.maths.Vec3d;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MinecraftUtil {

	public @NotNull OperatingSystem getOperatingSystem() {
		throw new UnsupportedOperationException();
	}

	public @Nullable Vec3d getCameraPos() {
		throw new UnsupportedOperationException();
	}

	public void copy(@Nullable String text) {
		throw new UnsupportedOperationException();
	}

	public @NotNull String getClipboardContent() {
		throw new UnsupportedOperationException();
	}

	@Helper
	public boolean isMac() {
		return getOperatingSystem() == OperatingSystem.OSX;
	}

	public boolean isAllowedInTextBox(char character) {
		throw new UnsupportedOperationException();
	}

	public static @NotNull String filterTextBoxInput(@NotNull String text) {
		throw new UnsupportedOperationException();
	}
}
