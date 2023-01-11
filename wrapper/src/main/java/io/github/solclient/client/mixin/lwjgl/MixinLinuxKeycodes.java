package io.github.solclient.client.mixin.lwjgl;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(targets = "org.lwjgl.opengl.LinuxKeycodes")
public interface MixinLinuxKeycodes {

	@Invoker(value = "mapKeySymToLWJGLKeyCode", remap = false)
	static int mapKeySymToLWJGLKeyCode(long keysym) {
		throw new UnsupportedOperationException();
	}

}
