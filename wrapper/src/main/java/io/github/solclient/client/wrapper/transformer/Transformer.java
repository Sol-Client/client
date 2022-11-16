package io.github.solclient.client.wrapper.transformer;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

import io.github.solclient.client.wrapper.mixin.ClientMixinService;

public abstract class Transformer {

	private ClassNode self;
	private List<String> capturedNames;

	/**
	 * @return <code>true</code> if the transformer will modify its input class node.
	 */
	public abstract boolean willModify(@NotNull String className);

	public abstract void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException;

	@SuppressWarnings("unchecked")
	public @NotNull String extractName(int index) throws IndexOutOfBoundsException, ClassNotFoundException, IOException {
		if(capturedNames == null) {
			getSelf();

			if(self.visibleAnnotations != null) {
				self.visibleAnnotations
						.stream()
						.filter((annotation) -> annotation.desc.equals("Lio/github/solclient/client/wrapper/transformer/CaptureClassNames;"))
						.findFirst()
						.ifPresent((annotation) -> {
							List<Type> values = (List<Type>) annotation.values.get(1);
							capturedNames = values
									.stream()
									.map((type) -> type.getInternalName())
									.collect(Collectors.toList());
						});
			}

			if(capturedNames == null) {
				capturedNames = Collections.emptyList();
			}
		}

		return capturedNames.get(index);
	}

	public ClassNode getSelf() throws ClassNotFoundException, IOException {
		if(self == null) {
			return self = ClientMixinService.getInstance().getBytecodeProvider()
					.getClassNode(getClass().getName().replace('.', '/'));
		}

		return self;
	}

}
