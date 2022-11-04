package io.github.solclient.client.wrapper.mixin;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.IClassBytecodeProvider;

import io.github.solclient.client.wrapper.WrapperClassLoader;

public final class ClientBytecodeProvider implements IClassBytecodeProvider {

	@Override
	public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
		ClassNode node = new ClassNode();
		ClassReader reader = new ClassReader(WrapperClassLoader.INSTANCE.getModifiedBytes(name));
		reader.accept(node, 0);
		return node;
	}

	@Override
	public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
		return getClassNode(name);
	}

}
