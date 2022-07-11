package io.github.solclient.client.platform.mc.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	public static void copy(@NotNull String text) {
		throw new UnsupportedOperationException();
	}

	public static String getClipboardContent() {
		throw new UnsupportedOperationException();
	}

}
