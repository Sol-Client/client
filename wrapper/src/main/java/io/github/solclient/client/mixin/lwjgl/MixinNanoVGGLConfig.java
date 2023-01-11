package io.github.solclient.client.mixin.lwjgl;

import org.lwjgl.system.FunctionProvider;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.util.Lwjgl2FunctionProvider;

@Mixin(targets = "org.lwjgl.nanovg.NanoVGGLConfig")
public class MixinNanoVGGLConfig {

	@Overwrite
	private static FunctionProvider getFunctionProvider(String className) {
		return new Lwjgl2FunctionProvider();
	}

}
