package io.github.solclient.client.v1_8_9.transformers;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import io.github.solclient.client.wrapper.transformer.CaptureClassNames;
import net.minecraft.client.gui.screen.Screen;

@CaptureClassNames(Screen.class)
public class ProxyScreenTransformer
		extends io.github.solclient.client.wrapper.transformer.impl.mc.ProxyScreenTransformer {

	@Override
	public void accept(@NotNull ClassNode node) throws ClassNotFoundException, IOException {
		super.accept(node);
		MethodNode constructor = node.methods
				.stream()
				.filter((method) -> method.name.equals("<init>"))
				.findFirst()
				.orElseThrow();

		constructor.instructions.clear();
		constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
		constructor.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, extractName(0), "<init>", "()V"));
		constructor.instructions.add(new InsnNode(Opcodes.RETURN));
		constructor.maxStack = 1;
		constructor.maxLocals = 2;
		constructor.localVariables.clear();
	}

}
