package io.github.solclient.wrapper.transformer;

import org.objectweb.asm.*;

import lombok.experimental.UtilityClass;

@UtilityClass
class GuavaTransformer {

	private final String OBJECTS = "com.google.common.base.Objects";
	private final String ITERATORS = "com.google.common.collect.Iterators";

	public byte[] transform(String name, byte[] input) {
		switch (name) {
			case OBJECTS:
				return transformObjects(input);
			case ITERATORS:
				return transformIterators(input);
		}

		return input;
	}

	private byte[] transformObjects(byte[] input) {
		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(writer, 0);

		MethodVisitor firstNonNull = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "firstNonNull",
				"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", null, null);
		firstNonNull.visitVarInsn(Opcodes.ALOAD, 0);
		firstNonNull.visitVarInsn(Opcodes.ALOAD, 1);
		firstNonNull.visitMethodInsn(Opcodes.INVOKESTATIC, "com/google/common/base/MoreObjects", "firstNonNull",
				"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
		firstNonNull.visitInsn(Opcodes.ARETURN);
		firstNonNull.visitMaxs(3, 3);

		return writer.toByteArray();
	}

	private byte[] transformIterators(byte[] input) {
		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
					String[] exceptions) {
				if (name.equals("emptyIterator"))
					access |= Opcodes.ACC_PUBLIC;

				return super.visitMethod(access, name, descriptor, signature, exceptions);
			}

		}, 0);
		return writer.toByteArray();
	}

}
