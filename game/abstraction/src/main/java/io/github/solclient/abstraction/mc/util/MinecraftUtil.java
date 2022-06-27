package io.github.solclient.abstraction.mc.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.solclient.abstraction.mc.maths.Vec3d;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MinecraftUtil {

	public @NotNull OperatingSystem getOperatingSystem() {
		throw new UnsupportedOperationException();
	}

	public @Nullable Vec3d getCameraPos() {
		throw new UnsupportedOperationException();
	}

	public static @Nullable String getKeyName(int code) {
		throw new UnsupportedOperationException();
	}

}
