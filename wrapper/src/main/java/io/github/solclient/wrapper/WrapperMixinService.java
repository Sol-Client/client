/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.wrapper;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.platform.container.*;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.mixin.MixinEnvironment.*;
import org.spongepowered.asm.mixin.transformer.*;
import org.spongepowered.asm.service.*;
import org.spongepowered.asm.util.*;

/**
 * Implementation of IMixinService for ClassWrapper.
 */
public final class WrapperMixinService implements IMixinService, IClassProvider, IClassBytecodeProvider {

	static IMixinTransformer transformer;

	private final ReEntranceLock lock = new ReEntranceLock(1);
	private final IContainerHandle container = new ContainerHandleVirtual(getName());

	@Override
	public String getName() {
		return "wrapper";
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
		if (internal instanceof IMixinTransformerFactory)
			transformer = ((IMixinTransformerFactory) internal).createTransformer();
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
		return this;
	}

	@Override
	public IClassBytecodeProvider getBytecodeProvider() {
		return this;
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
		return ClassWrapper.instance.getResourceAsStream(name);
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
		return new Log4jLogger(name);
	}

	// class provider

	@Override
	public URL[] getClassPath() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> findClass(String name) throws ClassNotFoundException {
		return ClassWrapper.instance.loadClass(name);
	}

	@Override
	public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, ClassWrapper.instance);
	}

	@Override
	public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
		return Class.forName(name, initialize, ClassWrapper.class.getClassLoader());
	}

	// bytecode provider

	@Override
	public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
		return getClassNode(name, true);
	}

	@Override
	public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
		byte[] bytes = ClassWrapper.instance.getTransformedBytes(name.replace('/', '.'), false);
		if (bytes == null)
			throw new ClassNotFoundException(name);

		ClassReader reader = new ClassReader(bytes);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);
		return node;
	}

}
