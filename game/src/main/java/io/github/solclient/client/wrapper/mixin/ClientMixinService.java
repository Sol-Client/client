package io.github.solclient.client.wrapper.mixin;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;

import org.spongepowered.asm.launch.platform.container.ContainerHandleVirtual;
import org.spongepowered.asm.launch.platform.container.IContainerHandle;
import org.spongepowered.asm.logging.ILogger;
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
import org.spongepowered.asm.util.Constants;
import org.spongepowered.asm.util.ReEntranceLock;

import io.github.solclient.client.wrapper.WrapperClassLoader;
import lombok.Getter;

public class ClientMixinService implements IMixinService {

	@Getter
	private static ClientMixinService instance;

	private final ReEntranceLock lock = new ReEntranceLock(1);
	private final IClassProvider classes = new ClientClassProvider();
	private final IClassBytecodeProvider bytecode = new ClientBytecodeProvider();
	private final IContainerHandle container = new ContainerHandleVirtual(getName());
	@Getter
	private IMixinTransformer transformer;

	public ClientMixinService() {
		instance = this;
	}

	@Override
	public String getName() {
		return "Sol Client";
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void prepare() {
	}

	@Override
	public Phase getInitialPhase() {
		return null;
	}

	@Override
	public void offer(IMixinInternal internal) {
		if(internal instanceof IMixinTransformerFactory) {
			transformer = ((IMixinTransformerFactory) internal).createTransformer();
		}
	}

	@Override
	public void init() {
	}

	@Override
	public void beginPhase() {
	}

	@Override
	public void checkEnv(Object bootSource) {
	}

	@Override
	public ReEntranceLock getReEntranceLock() {
		return lock;
	}

	@Override
	public IClassProvider getClassProvider() {
		return classes;
	}

	@Override
	public IClassBytecodeProvider getBytecodeProvider() {
		return bytecode;
	}

	@Override
	public ITransformerProvider getTransformerProvider() {
		return null;
	}

	@Override
	public IClassTracker getClassTracker() {
		return null;
	}

	@Override
	public IMixinAuditTrail getAuditTrail() {
		return null;
	}

	// use bad practices not to feed GC
	// edit: wow I'm so dumb
	@Override
	public Collection<String> getPlatformAgents() {
		return Collections.emptyList();
	}

	@Override
	public IContainerHandle getPrimaryContainer() {
		return container;
	}

	@Override
	public Collection<IContainerHandle> getMixinContainers() {
		return Collections.emptyList();
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		return WrapperClassLoader.INSTANCE.getResourceAsStream(name);
	}

	@Override
	public String getSideName() {
		return Constants.SIDE_CLIENT;
	}

	@Override
	public CompatibilityLevel getMinCompatibilityLevel() {
		return null;
	}

	@Override
	public CompatibilityLevel getMaxCompatibilityLevel() {
		return null;
	}

	@Override
	public ILogger getLogger(String name) {
		return new ClientLogger(name);
	}

}
