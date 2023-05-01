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
import java.net.*;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import io.github.solclient.util.GlobalConstants;
import io.github.solclient.wrapper.transformer.ClassTransformer;
import lombok.Getter;

/**
 * A class loader which applies transformations 🪄. I assure you, any
 * modifications needing to be made to build.gradle to allow this emoji is
 * totally worth it.
 */
public final class ClassWrapper extends URLClassLoader {

	@Getter
	static ClassWrapper instance;

	// exclude me
	// thanks
	// @formatter:off
	private static final String[] EXCLUDED_PACKAGES = {
			// builtin classes
			"java",
			"javax",
			"sun",
			"com.sun",
			"jdk",
			// libraries, for truly great optimisation
			// rule: if we touch it but it doesn't touch MC or anything which does
			"org.apache",
			"io.netty",
			"com.mojang.authlib",
			"com.mojang.util",
			"com.google",
			"com.logisticscraft.occlusionculling",
			"net.hypixel",
			"org.spongepowered",
			// important
			"io.github.solclient.wrapper",
			"io.github.solclient.utils"
	};
	private static final String[] EXCLUDED_CLASSES = {
			"org.lwjgl.Version"
	};
	// @formatter:on

	/**
	 * The default class loader.
	 */
	private final ClassLoader upstream;

	ClassWrapper(URL[] urls) {
		super(urls);
		upstream = getClass().getClassLoader();
		instance = this;
		Thread.currentThread().setContextClassLoader(this);
		MixinBootstrap.init();
	}

	// HACK
	private boolean shouldHideResource(String name) {
		return GlobalConstants.DEV && name.equals("sol-client-wrapper-refmap.json");
	}

	@Override
	public void addURL(URL url) {
		super.addURL(url);
	}

	@Override
	public URL getResource(String name) {
		if (shouldHideResource(name))
			return null;

		URL superResource = super.findResource(name);
		if (superResource != null)
			return superResource;

		return upstream.getResource(name);
	}

	@Override
	public URL findResource(String name) {
		if (shouldHideResource(name))
			return null;

		URL superResource = super.findResource(name);
		if (superResource != null)
			return superResource;

		return upstream.getResource(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
		if (shouldHideResource(name))
			return null;

		InputStream superIn = super.getResourceAsStream(name);
		if (superIn != null)
			return superIn;

		return upstream.getResourceAsStream(name);
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		Enumeration<URL> enumeration = super.getResources(name);
		if (enumeration.hasMoreElements())
			return enumeration;

		return upstream.getResources(name);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		synchronized (getClassLoadingLock(name)) {
			Class<?> preexisting = findLoadedClass(name);
			if (preexisting != null)
				return preexisting;

			// we don't want to load these classes ourselves
			if (!name.startsWith("org.spongepowered.asm.synthetic.") && !name.startsWith("javax.vecmath.")) {
				for (String exclude : EXCLUDED_CLASSES)
					if (name.equals(exclude))
						return upstream.loadClass(name);

				for (String exclude : EXCLUDED_PACKAGES)
					if (name.startsWith(exclude) && name.charAt(exclude.length()) == '.')
						return upstream.loadClass(name);
			}

			Class<?> found = findClass(name);
			if (found == null)
				return upstream.loadClass(name);

			return found;
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = getTransformedBytes(name);
		if (data == null)
			return null;

		return defineClass(name, data, 0, data.length);
	}

	/**
	 * Gets whether a class is directly available.
	 *
	 * @param name the class name.
	 * @return <code>true</code> if the class file is avilable without generation.
	 */
	public boolean isAvailable(String name) {
		return getResource(getClassFileName(name)) != null;
	}

	/**
	 * Load and transform class bytes. This includes mixin.
	 *
	 * @param name the class name.
	 * @return the bytes.
	 * @throws ClassNotFoundException if the class cannot be located.
	 */
	public byte[] getTransformedBytes(String name) throws ClassNotFoundException {
		return getTransformedBytes(name, true);
	}

	byte[] getTransformedBytes(String name, boolean mixin) throws ClassNotFoundException {
		try {
			URL resource = getResource(getClassFileName(name));
			if (resource == null)
				return WrapperMixinService.transformer.generateClass(MixinEnvironment.getDefaultEnvironment(), name);

			byte[] data;
			try (InputStream in = resource.openStream()) {
				data = IOUtils.toByteArray(in);
			}

			data = ClassTransformer.transformClass(name, data);

			if (mixin)
				data = WrapperMixinService.transformer.transformClass(MixinEnvironment.getDefaultEnvironment(), name,
						data);

			return data;
		} catch (Throwable error) {
			throw new ClassNotFoundException(name, error);
		}
	}

	private static String getClassFileName(String name) {
		return name.replace('.', '/') + ".class";
	}

	static {
		registerAsParallelCapable();
	}

}
