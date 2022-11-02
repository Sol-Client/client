package io.github.solclient.client.wrapper.transformer.impl.mc;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.wrapper.transformer.Transformer;

public class PackageAccessFixer extends Transformer {

	@Override
	public boolean willModify(@NotNull String className) {
		return className.startsWith("net/minecraft/");
	}

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		node.access = fixAccess(node.access);
		node.fields.forEach((field) -> field.access = fixAccess(field.access));
		node.methods.forEach((method) -> method.access = fixAccess(method.access));
		node.innerClasses.forEach((clazz) -> clazz.access = fixAccess(clazz.access));
	}

	// based off Fabric loader's method - pretty compact way to do things
	private static int fixAccess(int access) {
		if((access & 7) != Opcodes.ACC_PRIVATE) {
			return (access & ~7) | Opcodes.ACC_PUBLIC;
		}

		return access;
	}

}
