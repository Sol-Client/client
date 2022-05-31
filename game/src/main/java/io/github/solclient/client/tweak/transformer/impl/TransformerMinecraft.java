package io.github.solclient.client.tweak.transformer.impl;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import io.github.solclient.client.tweak.transformer.ClassNodeTransformer;

import java.lang.reflect.Modifier;

public class TransformerMinecraft implements ClassNodeTransformer {

	@Override
	public boolean test(String name) {
		return name.equals("net/minecraft/client/Minecraft") || name.equals("ave");
	}

	@Override
	public void apply(ClassNode clazz) {
		for(MethodNode method : clazz.methods) {
			if(method.name.equals("resize") ||
					(method.name.equals("func_71370_a") && method.desc.equals("(II)V"))) {
				method.access = (method.access & ~Modifier.PRIVATE) | Modifier.PUBLIC;
			}
		}
	}

}
