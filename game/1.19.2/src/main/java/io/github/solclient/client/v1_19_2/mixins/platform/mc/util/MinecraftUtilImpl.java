package io.github.solclient.client.v1_19_2.mixins.platform.mc.util;

import io.github.solclient.client.v1_19_2.SharedObjects;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.platform.mc.maths.Vec3d;
import io.github.solclient.client.platform.mc.util.MinecraftUtil;
import io.github.solclient.client.platform.mc.util.OperatingSystem;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Overwrite;

@UtilityClass
@Mixin(MinecraftUtil.class)
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
