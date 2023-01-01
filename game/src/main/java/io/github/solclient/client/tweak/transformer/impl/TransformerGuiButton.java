package io.github.solclient.client.tweak.transformer.impl;

import java.lang.reflect.Modifier;

import org.objectweb.asm.tree.*;

import io.github.solclient.client.tweak.transformer.ClassNodeTransformer;

public class TransformerGuiButton implements ClassNodeTransformer {

	@Override
	public boolean test(String name) {
		return name.equals("net/minecraft/client/gui/GuiButton");
	}

	@Override
	public void apply(ClassNode clazz) {
		for (FieldNode field : clazz.fields) {
			if (field.name.equals("width") || field.name.equals("field_146120_f") || field.name.equals("height")
					|| field.name.equals("field_146121_g")) {
				field.access = (field.access & ~Modifier.PROTECTED) | Modifier.PUBLIC;
			}
		}
	}

}
