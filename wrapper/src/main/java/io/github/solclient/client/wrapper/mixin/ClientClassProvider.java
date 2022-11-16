package io.github.solclient.client.wrapper.mixin;

import java.net.URL;

import org.spongepowered.asm.service.IClassProvider;

import io.github.solclient.client.wrapper.WrapperClassLoader;

public final class ClientClassProvider implements IClassProvider {

	@Override
	public URL[] getClassPath() {
		throw new UnsupportedOperationException("Cannot query class path");
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return WrapperClassLoader.INSTANCE.loadClass(name);
	}

	@Override
	public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, WrapperClassLoader.INSTANCE);
	}

	@Override
	public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, WrapperClassLoader.class.getClassLoader());
	}

}
