/**
 * Modified from danterus' mixin-client-template.
 *
 * MIT License
 *
 * Copyright © 2022 danterusdev
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.solclient.client.wrapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.Proxy;

import io.github.solclient.client.wrapper.mixin.ClientMixinService;
import io.github.solclient.client.wrapper.transformer.Transformer;
import io.github.solclient.client.wrapper.transformer.impl.guava.LegacyFuturesTransformer;
import io.github.solclient.client.wrapper.transformer.impl.guava.LegacyIteratorsTransformer;
import io.github.solclient.client.wrapper.transformer.impl.guava.LegacyObjectsTransformer;
import io.github.solclient.client.wrapper.transformer.impl.mc.PackageAccessFixer;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * A class loader which modifies classes and applies mixins.
 */
public class WrapperClassLoader extends ClassLoader {

	public static final WrapperClassLoader INSTANCE = new WrapperClassLoader();
	private static final Logger LOGGER = LogManager.getLogger();

	private final List<String> exclusions = new ArrayList<>();
	private final List<URL> urls = new ArrayList<>();
	private final ClassLoader parent = WrapperClassLoader.class.getClassLoader();
	private final List<Transformer> transformers = new ArrayList<>();

	private WrapperClassLoader() {
		addDefaultExclusions();
		addDefaultTransformers();

		MixinBootstrap.init();
	}

	private void addDefaultExclusions() {
		exclusions.add("java");
		exclusions.add("jdk");
		exclusions.add("javax");

		exclusions.add("sun");
		exclusions.add("com.sun");
		exclusions.add("org.xml");
		exclusions.add("org.w3c");

		exclusions.add("org.apache");
		exclusions.add("org.slf4j");
		exclusions.add("com.mojang.blocklist");

		exclusions.add("io.github.solclient.client.wrapper");

		exclusions.add("org.spongepowered.asm");
	}

	private void addDefaultTransformers() {
		// Patch guava classes to support old Minecraft versions.
		transformers.add(new LegacyObjectsTransformer());
		transformers.add(new LegacyFuturesTransformer());
		transformers.add(new LegacyIteratorsTransformer());

		transformers.add(new PackageAccessFixer());
	}

	private final Map<String, Class<?>> cache = new HashMap<>();

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		for(String exclusion : exclusions) {
			if(name.startsWith(exclusion.concat("."))) {
				try {
					return parent.loadClass(name);
				}
				catch(ClassNotFoundException ignored) {
				}
			}
		}

		Class<?> clazz = super.findLoadedClass(name);
		if(clazz == null) {
			clazz = cache.get(name);
		}

		if(clazz == null) {
			byte[] data = getModifiedBytes(name);
			clazz = super.defineClass(name, data, 0, data.length);
			cache.put(name, clazz);
		}

		return clazz;
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		Class<?> clazz = this.loadClass(name);

		if(resolve) {
			resolveClass(clazz);
		}

		return clazz;
	}

	public byte[] getModifiedBytes(String name) throws ClassNotFoundException {
		byte[] data = loadClassData(name);
		String internalName = name.replace(".", "/");

		List<Transformer> applicableTransformers = transformers.stream().filter((transformer) -> transformer.willModify(internalName)).collect(Collectors.toList());

		if(!applicableTransformers.isEmpty()) {
			try {
				ClassReader reader = new ClassReader(data);
				ClassNode node = new ClassNode();

				reader.accept(node, 0);

				for(Transformer transformer : applicableTransformers) {
					transformer.accept(node);
				}

				ClassWriter writer = new ClassWriter(0);
				node.accept(writer);
				data = writer.toByteArray();
			}
			catch(ClassNotFoundException | IOException error) {
				throw new ClassNotFoundException(name, error);
			}
		}

		try {
			data = ClientMixinService.getInstance().getTransformer().transformClass(MixinEnvironment.getDefaultEnvironment(), name, data);
		}
		catch(Exception error) {
			throw new ClassNotFoundException(name, error);
		}

		if(data.length == 0) {
			throw new ClassNotFoundException(name);
		}

		return data;
	}

	private byte[] loadClassData(String className) {
		try {
			Enumeration<URL> resources = getResources(className.replace(".", "/") + ".class");

			if(resources.hasMoreElements()) {
				return IOUtils.toByteArray(resources.nextElement());
			}
		}
		catch(IOException error) {
			LOGGER.error(error);
		}

		return new byte[0];
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		List<URL> parentResources = Collections.list(parent.getResources(name));

		List<URL> filteredURLs = new ArrayList<>(parentResources);

		for(URL pathUrl : Collections.list(findResources(name))) {
			for(URL url : urls) {
				if(pathUrl.getFile().contains(url.getFile())) {
					filteredURLs.add(pathUrl);
				}
			}
		}

		return Collections.enumeration(filteredURLs);
	}

	@Override
	public URL getResource(String name) {
		try {
			Enumeration<URL> resources = getResources(name);
			if(resources.hasMoreElements()) {
				return resources.nextElement();
			}
		}
		catch(IOException error) {
			LOGGER.error(error);
		}

		return parent.getResource(name);
	}

}