package io.github.solclient.client.wrapper.transformer.impl.guava;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.ClassNode;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import io.github.solclient.client.wrapper.transformer.Transformer;

public class LegacyFuturesTransformer extends Transformer {

	@Override
	public boolean willModify(@NotNull String className) {
		return className.equals("com/google/common/util/concurrent/Futures");
	}

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		node.methods.add(
				getSelf().methods.stream().filter((method) -> method.name.equals("addCallback")).findFirst().get());
	}

	public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback) {
		Futures.addCallback(future, callback, MoreExecutors.directExecutor());
	}

}
