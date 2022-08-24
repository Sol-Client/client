package io.github.solclient.client.v1_19_2.transformers;

import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import io.github.solclient.client.wrapper.transformer.CaptureClassNames;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@CaptureClassNames({ Screen.class, Text.class })
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
		constructor.instructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
		constructor.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, extractName(1)));
		constructor.instructions.add(
				new MethodInsnNode(Opcodes.INVOKESPECIAL, extractName(0), "<init>", "(L" + extractName(1) + ";)V"));
		constructor.instructions.add(new InsnNode(Opcodes.RETURN));
		constructor.maxStack = 2;
		constructor.maxLocals = 2;
		constructor.localVariables.clear();
	}

}
