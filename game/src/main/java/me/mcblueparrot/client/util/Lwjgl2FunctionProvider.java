package me.mcblueparrot.client.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.system.FunctionProvider;

import lombok.SneakyThrows;

public class Lwjgl2FunctionProvider implements FunctionProvider {

	private final Method functionAddressMethod;

	@SneakyThrows
	public Lwjgl2FunctionProvider() {
		functionAddressMethod = GLContext.class.getDeclaredMethod("getFunctionAddress", String.class);
		functionAddressMethod.setAccessible(true);
	}

	@Override
	public long getFunctionAddress(ByteBuffer functionName) {
		try {
			return (long) functionAddressMethod.invoke(null, StandardCharsets.UTF_8.decode(functionName).toString());
		}
		catch (IllegalAccessException | IllegalArgumentException error) {
			throw new Error(error);
		}
		catch(InvocationTargetException error) {
			if(error.getCause() instanceof RuntimeException) {
				throw (RuntimeException) error.getCause();
			}

			throw new Error(error.getCause());
		}
	}

}
