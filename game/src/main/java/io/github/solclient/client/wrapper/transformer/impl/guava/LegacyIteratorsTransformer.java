package io.github.solclient.client.wrapper.transformer.impl.guava;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.wrapper.transformer.Transformer;

public class LegacyIteratorsTransformer extends Transformer {

	@Override
	public boolean willModify(@NotNull String className) {
		return className.equals("com/google/common/collect/Iterators");
	}

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		node.methods.stream().filter((method) -> method.name.equals("emptyIterator")).findFirst()
				.get().access |= Opcodes.ACC_PUBLIC;
	}

}
