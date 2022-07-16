package io.github.solclient.client.wrapper.transformer;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.wrapper.BootstrapMain;
import io.github.solclient.client.wrapper.WrapperClassLoader;
import io.github.solclient.client.wrapper.mixin.ClientMixinService;

public abstract class Transformer {

	private ClassNode self;

	/**
	 * @return <code>true</code> if the transformer will modify its input class node.
	 */
	public abstract boolean willModify(@NotNull String className);

	public abstract void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException;

	public ClassNode getSelf() throws ClassNotFoundException, IOException {
		if(self == null) {
			return self = ClientMixinService.getInstance().getBytecodeProvider().getClassNode(getClass().getName().replace('.', '/'));
		}

		return self;
	}

}
