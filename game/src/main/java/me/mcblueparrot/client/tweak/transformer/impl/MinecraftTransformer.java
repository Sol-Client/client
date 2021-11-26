package me.mcblueparrot.client.tweak.transformer.impl;

import me.mcblueparrot.client.tweak.transformer.ClassNodeTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Modifier;

public class MinecraftTransformer implements ClassNodeTransformer {

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
