package io.github.solclient.client.wrapper.transformer.impl.optifine;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.wrapper.transformer.Transformer;

// TODO: implement optifine
public class OptiFineTransformer extends Transformer {

	public static final boolean ACTIVE = Boolean.getBoolean("io.github.solclient.use_optifine");

	@Override
	public boolean isEnabled() {
		return ACTIVE;
	}

	@Override
	public boolean willModify(@NotNull String className) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		// TODO Auto-generated method stub

	}

}
