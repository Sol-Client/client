package io.github.solclient.client.v1_19_2.mixins.platform.mc.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.platform.mc.maths.Vec3d;
import io.github.solclient.client.platform.mc.util.MinecraftUtil;
import io.github.solclient.client.platform.mc.util.OperatingSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

@UtilityClass
@Mixin(MinecraftUtil.class)
public class MinecraftUtilImpl {

	private @NotNull OperatingSystem getOperatingSystem() {
		return (OperatingSystem) (Object) Util.getOperatingSystem();
	}

	private @Nullable Vec3d getCameraPos() {
		return (Vec3d) MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
	}

	private void copy(@NotNull String text) {
		MinecraftClient.getInstance().keyboard.setClipboard(text);
	}

	private String getClipboardContent() {
		return MinecraftClient.getInstance().keyboard.getClipboard();
	}

}
