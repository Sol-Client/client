package io.github.solclient.client.v1_19_2.mixins.platform.mc.util;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.util.Input;
import lombok.experimental.UtilityClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;

@UtilityClass
@Mixin(Input.class)
public class InputImpl {

	private long windowId() {
		return MinecraftClient.getInstance().getWindow().getHandle();
	}

	@Overwrite(remap = false)
	public boolean isKeyDown(int code) {
		return InputUtil.isKeyPressed(windowId(), code);
	}

	@Overwrite(remap = false)
	public boolean isMouseButtonDown(int button) {
		return GLFW.glfwGetMouseButton(windowId(), button) == GLFW.GLFW_PRESS;
	}

}
