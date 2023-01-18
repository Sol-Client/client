package io.github.solclient.wrapper.transformer;

import org.objectweb.asm.*;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ReplayModMixinPluginCompat {

	public byte[] transform(String name, byte[] input) {
		if (!name.equals("com.replaymod.core.ReplayModMixinConfigPlugin"))
			return input;

		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
					String[] exceptions) {
				if (name.equals("hasClass"))
					return null;

				return super.visitMethod(access, name, descriptor, signature, exceptions);
			}

		}, 0);

		MethodVisitor hasClass = writer.visitMethod(Opcodes.ACC_STATIC, "hasClass", "(Ljava/lang/String;)Z", null, null);
		hasClass.visitCode();
		Label label0 = new Label();
		hasClass.visitLabel(label0);
		hasClass.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/solclient/wrapper/ClassWrapper", "getInstance", "()Lio/github/solclient/wrapper/ClassWrapper;", false);
		hasClass.visitVarInsn(Opcodes.ALOAD, 0);
		hasClass.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "io/github/solclient/wrapper/ClassWrapper", "isAvailable", "(Ljava/lang/String;)Z", false);
		hasClass.visitInsn(Opcodes.IRETURN);
		Label label1 = new Label();
		hasClass.visitLabel(label1);
		hasClass.visitMaxs(2, 1);
		hasClass.visitEnd();

		return writer.toByteArray();
	}

}
