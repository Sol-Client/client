package io.github.solclient.client.tweak.transformer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.tweak.transformer.impl.*;
import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {

	private List<ClassNodeTransformer> transformers = new ArrayList<>();

	public ClassTransformer() {
		register(new TransformerGuiButton());
		register(new TransformerGuiScreen());
		register(new TransformerWorldClient());
		register(new TransformerMinecraft());
		register(new TransformerLinuxKeycodes());
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		List<ClassNodeTransformer> applicable = transformers.stream()
				.filter((transformers) -> transformers.test(name.replace(".", "/"))).collect(Collectors.toList());

		if (applicable.isEmpty())
			return basicClass;

		ClassReader reader = new ClassReader(basicClass);
		ClassNode clazz = new ClassNode();
		reader.accept(clazz, 0);

		for (ClassNodeTransformer transformer : applicable) {
			try {
				transformer.apply(clazz);
			} catch (IOException error) {
				throw new IllegalStateException("Could not transform class " + name, error);
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		clazz.accept(writer);

		return writer.toByteArray();
	}

	public void register(ClassNodeTransformer transformer) {
		transformers.add(transformer);
	}

}
