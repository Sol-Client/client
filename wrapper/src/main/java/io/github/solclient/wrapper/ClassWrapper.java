package io.github.solclient.wrapper;

import java.io.*;
import java.net.*;
import java.util.Enumeration;

import org.apache.commons.io.IOUtils;

// roughly inspired by Fabric
public final class ClassWrapper extends URLClassLoader {

	public static final boolean OPTIFINE = false;
	public static final ClassWrapper INSTANCE = new ClassWrapper(new URL[0]);

	private final ClassLoader upstream;

	public ClassWrapper(URL[] urls) {
		super(urls);
		upstream = getClass().getClassLoader();
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

		// @formatter:off
		if (name.startsWith("java.")
				|| name.startsWith("javax.")
				|| name.startsWith("jdk.")
				|| name.startsWith("sun.")
				|| name.startsWith("com.sun.")
				|| name.startsWith("io.github.solclient.wrapper.")
				|| name.startsWith("io.github.solclient.client.mixin."))
			return upstream.loadClass(name);
		// @formatter:on

		Class<?> found = findClass(name);
		if (found == null)
			return upstream.loadClass(name);

		return found;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		URL resource = getResource(getClassFileName(name));
		if (resource == null)
			throw new ClassNotFoundException(name);

		byte[] bytes;
		try (InputStream in = resource.openStream()) {
			bytes = IOUtils.toByteArray(in);
		} catch (IOException error) {
			throw new ClassNotFoundException(name, error);
		}

		bytes = ClassTransformer.transformClass(name, bytes);
		return defineClass(name, bytes, 0, bytes.length);
	}

	private static String getClassFileName(String name) {
		return name.replace('.', '/') + ".class";
	}

	static {
		registerAsParallelCapable();
	}

}
