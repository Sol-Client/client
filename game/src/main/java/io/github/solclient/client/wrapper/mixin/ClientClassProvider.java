package io.github.solclient.client.wrapper.mixin;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.LoggerAdapterConsole;
import org.spongepowered.asm.mixin.MixinEnvironment.CompatibilityLevel;
import org.spongepowered.asm.mixin.MixinEnvironment.Phase;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.asm.mixin.transformer.IMixinTransformerFactory;
import org.spongepowered.asm.service.IClassBytecodeProvider;
import org.spongepowered.asm.service.IClassProvider;
import org.spongepowered.asm.service.IClassTracker;
import org.spongepowered.asm.service.IMixinAuditTrail;
import org.spongepowered.asm.service.IMixinInternal;
import org.spongepowered.asm.service.IMixinService;
import org.spongepowered.asm.service.ITransformerProvider;
import org.spongepowered.asm.service.mojang.LoggerAdapterLog4j2;
import org.spongepowered.asm.util.Constants;
import org.spongepowered.asm.util.ReEntranceLock;

import io.github.solclient.client.wrapper.WrapperClassLoader;
import lombok.Getter;

public class ClientClassProvider implements IClassProvider {

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
