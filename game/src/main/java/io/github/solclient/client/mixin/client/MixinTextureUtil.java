package io.github.solclient.client.mixin.client;

import java.nio.IntBuffer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.*;

import net.minecraft.client.renderer.texture.TextureUtil;

@Mixin(TextureUtil.class)
public interface MixinTextureUtil {

	@Accessor
	static IntBuffer getDataBuffer() {
		throw new UnsupportedOperationException();
	}

	@Invoker("copyToBuffer")
	static void copyToBuffer(int[] array, int length) {
		throw new UnsupportedOperationException();
	}

}
