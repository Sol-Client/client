package io.github.solclient.client.mixin.lwjgl;

import org.lwjgl.opengl.GLContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GLContext.class)
public interface MixinGLContext {

	@Invoker("getFunctionAddress")
	static long getFunctionAddress(String name) {
		throw new UnsupportedOperationException();
	}

	@Invoker("ngetFunctionAddress")
	static long ngetFunctionAddress(long name) {
		throw new UnsupportedOperationException();
	}

}
