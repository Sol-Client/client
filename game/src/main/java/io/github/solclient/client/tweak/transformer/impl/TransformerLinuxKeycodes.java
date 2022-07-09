package io.github.solclient.client.tweak.transformer.impl;

import java.lang.reflect.Modifier;

import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.tweak.transformer.ClassNodeTransformer;

public class TransformerLinuxKeycodes implements ClassNodeTransformer {

	@Override
	public boolean test(String name) {
		return name.equals("org/lwjgl/opengl/LinuxKeycodes");
	}

	@Override
	public void apply(ClassNode clazz) {
		clazz.access |= Modifier.PUBLIC;
	}

}
