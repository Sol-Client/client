package io.github.solclient.client.tweak.transformer.impl;

import java.lang.reflect.Modifier;

import org.objectweb.asm.tree.*;

import io.github.solclient.client.tweak.transformer.ClassNodeTransformer;

public class TransformerMinecraft implements ClassNodeTransformer {

	@Override
	public boolean test(String name) {
		return name.equals("net/minecraft/client/Minecraft") || name.equals("ave");
	}

	@Override
	public void apply(ClassNode clazz) {
		for (MethodNode method : clazz.methods) {
			if (method.name.equals("resize") || (method.name.equals("func_71370_a") && method.desc.equals("(II)V"))) {
				method.access = (method.access & ~Modifier.PRIVATE) | Modifier.PUBLIC;
			}
		}
	}

}
