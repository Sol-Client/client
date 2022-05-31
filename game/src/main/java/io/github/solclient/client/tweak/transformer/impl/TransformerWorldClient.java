package io.github.solclient.client.tweak.transformer.impl;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import io.github.solclient.client.tweak.transformer.ClassNodeTransformer;

import java.lang.reflect.Modifier;

public class TransformerWorldClient implements ClassNodeTransformer {

	@Override
	public boolean test(String name) {
		return name.equals("net/minecraft/client/multiplayer/WorldClient");
	}

	@Override
	public void apply(ClassNode clazz) {
		for(MethodNode method : clazz.methods) {
			if(method.name.equals("onEntityRemoved") ||
					(method.name.equals("func_72847_b") && method.desc.equals("(Lnet/minecraft/entity/Entity;)V"))) {
				method.access = (method.access & ~Modifier.PROTECTED) | Modifier.PUBLIC;
			}
		}
	}

}
