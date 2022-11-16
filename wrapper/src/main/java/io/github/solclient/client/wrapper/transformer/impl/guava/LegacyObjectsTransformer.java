package io.github.solclient.client.wrapper.transformer.impl.guava;

import java.io.IOException;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.base.MoreObjects;

import io.github.solclient.client.wrapper.transformer.Transformer;

public final class LegacyObjectsTransformer extends Transformer {

	@Override
	public boolean willModify(@NotNull String className) {
		return className.equals("com/google/common/base/Objects");
	}

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		node.methods.add(
				getSelf().methods.stream().filter((method) -> method.name.equals("firstNonNull")).findFirst().get());
	}

	public static <T> T firstNonNull(@Nullable T a, @Nullable T b) {
		return MoreObjects.firstNonNull(a, b);
	}

}
