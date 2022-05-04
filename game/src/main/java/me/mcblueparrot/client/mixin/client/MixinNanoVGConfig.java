package me.mcblueparrot.client.mixin.client;

import org.lwjgl.system.FunctionProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import me.mcblueparrot.client.util.Lwjgl2FunctionProvider;

@Mixin(targets = "org.lwjgl.nanovg.NanoVGGLConfig")
public class MixinNanoVGConfig {

	@Overwrite
	private static FunctionProvider getFunctionProvider(String className) {
		return new Lwjgl2FunctionProvider();
	}

}
