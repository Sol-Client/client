package io.github.solclient.client.wrapper.transformer.impl.mc;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.wrapper.transformer.Transformer;

public class ProxyScreenTransformer extends Transformer {

	@Override
	public boolean willModify(@NotNull String className) {
		return className.equals("io/github/solclient/client/platform/mc/screen/ProxyScreen");
	}

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		node.superName = extractName(0);
	}

}
