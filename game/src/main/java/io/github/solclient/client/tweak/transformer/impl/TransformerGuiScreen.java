package io.github.solclient.client.tweak.transformer.impl;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import io.github.solclient.client.tweak.transformer.ClassNodeTransformer;

import java.lang.reflect.Modifier;

public class TransformerGuiScreen implements ClassNodeTransformer {

	@Override
	public boolean test(String name) {
		return name.equals("net/minecraft/client/gui/GuiScreen");
	}

	@Override
	public void apply(ClassNode clazz) {
		for(FieldNode field : clazz.fields) {
			if(field.name.equals("mc") || field.name.equals("field_146297_k")) {
				field.access = (field.access & ~Modifier.PROTECTED) | Modifier.PUBLIC;
			}
		}
	}

}
