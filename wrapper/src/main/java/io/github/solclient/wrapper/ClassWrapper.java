package io.github.solclient.wrapper;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;
import org.spongepowered.asm.launch.MixinBootstrap;

/**
 * A class loader which applies transformations 🪄. I assure you, any
 * modifications needing to be made to build.gradle to allow this emoji is
 * totally worth it.
 */
public final class ClassWrapper extends URLClassLoader {

	public static final boolean OPTIFINE = false;
	public static final ClassWrapper INSTANCE = new ClassWrapper(new URL[0]);

	// exclude me
	// thanks
	// @formatter:off
	private static final String[] EXCLUDEMES = {
			// builtin classes
			"java",
			"javax",
			"sun",
			"com.sun",
			"jdk",
			// libraries, for truly great optimisation
			// rule: if we touch it but it doesn't touch MC or anything which does
			"org.apache.logging",
			"org.apache.commons",
			"io.netty",
			"com.mojang.authlib",
			"com.mojang.util",
			"com.google.gson",
			"com.logisticscraft.occlusionculling",
			"cc.cosmetica",
			"net.hypixel",
			// important
			"io.github.solclient.client.mixin",
			"io.github.solclient.wrapper"
	};
	// @formatter:on

	/**
	 * The default class loader.
	 */
	private final ClassLoader upstream;

	private ClassWrapper(URL[] urls) {
		super(urls);
		upstream = getClass().getClassLoader();
		MixinBootstrap.init();
		Thread.currentThread().setContextClassLoader(this);
	}

	@Override
	public URL getResource(String name) {
		URL superResource = super.getResource(name);
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
		for (String exclude : EXCLUDEMES)
			if (name.startsWith(exclude) && name.charAt(exclude.length()) == '.')
				return upstream.loadClass(name);

		Class<?> found = findClass(name);
		if (found == null)
			return upstream.loadClass(name);

		return found;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] data = getTransformedBytes(name);
		return defineClass(name, data, 0, data.length);
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
		URL resource = getResource(getClassFileName(name));
		if (resource == null)
			throw new ClassNotFoundException(name);

		byte[] data;
		try (InputStream in = resource.openStream()) {
			data = IOUtils.toByteArray(in);
		} catch (IOException error) {
			throw new ClassNotFoundException(name, error);
		}

		data = ClassTransformer.transformClass(name, data);

		if (mixin)
			data = WrapperMixinService.transformer.transformClassBytes(name, name, data);

		return data;
	}

	private static String getClassFileName(String name) {
		return name.replace('.', '/') + ".class";
	}

	static {
		registerAsParallelCapable();
	}

}
