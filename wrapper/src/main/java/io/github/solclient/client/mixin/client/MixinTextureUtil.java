package io.github.solclient.client.mixin.client;

import java.nio.IntBuffer;

import net.minecraft.client.texture.TextureUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(TextureUtil.class)
public interface MixinTextureUtil {

	@Accessor("BUFFER")
	static IntBuffer getBuffer() {
		throw new UnsupportedOperationException();
	}

	@Invoker("method_5866")
	static void copyToBuffer(int[] array, int length) {
		throw new UnsupportedOperationException();
	}

}
