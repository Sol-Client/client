package io.github.solclient.client.util;

import java.nio.ByteBuffer;

import org.lwjgl.MemoryUtil;
import org.lwjgl.system.FunctionProvider;

import io.github.solclient.client.mixin.lwjgl.MixinGLContext;

public final class Lwjgl2FunctionProvider implements FunctionProvider {

	@Override
	public long getFunctionAddress(CharSequence functionName) {
		return MixinGLContext.getFunctionAddress(functionName.toString());
	}

	@Override
	public long getFunctionAddress(ByteBuffer paramByteBuffer) {
		return MixinGLContext.ngetFunctionAddress(MemoryUtil.getAddress(paramByteBuffer));
	}

}
