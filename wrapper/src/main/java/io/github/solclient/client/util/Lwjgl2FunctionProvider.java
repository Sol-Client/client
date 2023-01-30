package io.github.solclient.client.util;

import java.nio.ByteBuffer;

import org.lwjgl.MemoryUtil;
import org.lwjgl.system.FunctionProvider;

import io.github.solclient.client.mixin.lwjgl.GLContextAccessor;

public final class Lwjgl2FunctionProvider implements FunctionProvider {

	@Override
	public long getFunctionAddress(CharSequence functionName) {
		return GLContextAccessor.getFunctionAddress(functionName.toString());
	}

	@Override
	public long getFunctionAddress(ByteBuffer paramByteBuffer) {
		return GLContextAccessor.ngetFunctionAddress(MemoryUtil.getAddress(paramByteBuffer));
	}

}
