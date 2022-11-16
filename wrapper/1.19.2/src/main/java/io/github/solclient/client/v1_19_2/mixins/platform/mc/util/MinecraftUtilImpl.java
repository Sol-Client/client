package io.github.solclient.client.v1_19_2.mixins.platform.mc.util;

import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.maths.Vec3d;
import io.github.solclient.client.platform.mc.util.*;
import lombok.experimental.UtilityClass;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;

@UtilityClass
@Mixin(MinecraftUtil.class)
@SuppressWarnings("resource")
public class MinecraftUtilImpl {

	@Overwrite(remap = false)
	public @NotNull OperatingSystem getOperatingSystem() {
		return (OperatingSystem) (Object) Util.getOperatingSystem();
	}

	@Overwrite(remap = false)
	public @Nullable Vec3d getCameraPos() {
		return (Vec3d) MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
	}

	@Overwrite(remap = false)
	public void copy(@NotNull String text) {
		MinecraftClient.getInstance().keyboard.setClipboard(text);
	}

	@Overwrite(remap = false)
	public String getClipboardContent() {
		return MinecraftClient.getInstance().keyboard.getClipboard();
	}

	@Overwrite(remap = false)
	public boolean isAllowedInTextBox(char character) {
		return SharedConstants.isValidChar(character);
	}

	@Overwrite(remap = false)
	public static @NotNull String filterTextBoxInput(@NotNull String text) {
		return SharedConstants.stripInvalidChars(text);
	}

}
