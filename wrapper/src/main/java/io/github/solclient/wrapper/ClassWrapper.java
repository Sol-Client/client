package io.github.solclient.wrapper;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.*;

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
			"com.google.gson",
			"com.logisticscraft.occlusionculling",
			"cc.cosmetica",
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

	@Override
	public URL getResource(String name) {
		URL superResource = super.findResource(name);
		if (superResource != null)
			return superResource;

		return upstream.getResource(name);
	}

	@Override
	public URL findResource(String name) {
		URL superResource = super.findResource(name);
		if (superResource != null)
			return superResource;

		return upstream.getResource(name);
	}

	@Override
	public InputStream getResourceAsStream(String name) {
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
		Class<?> preexisting = findLoadedClass(name);
		if (preexisting != null)
			return preexisting;

		// we don't want to load these classes ourselves
		if (!name.startsWith("org.spongepowered.asm.synthetic")) {
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
