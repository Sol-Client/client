package io.github.solclient.wrapper;

import org.objectweb.asm.*;

import com.google.common.util.concurrent.*;

public final class ClassTransformer {

	private static final String OBJECTS = "com.google.common.base.Objects";
	private static final String ITERATORS = "com.google.common.collect.Iterators";
	private static final String FUTURES = "com.google.common.util.concurrent.Futures";

	private static final boolean DEV = Boolean.getBoolean("loader.development");

	public static byte[] transformClass(String name, byte[] input) {
		input = transformGuava(name, input);
		if (DEV && (name.startsWith("net.minecraft.") || name.startsWith("com.mojang.blaze3d.")))
			input = fixPackageAccess(input);
		return input;
	}

	private static byte[] fixPackageAccess(byte[] input) {
		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				super.visit(version, mod(access), name, signature, superName, interfaces);
			}

			@Override
			public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
				return super.visitField(mod(access), name, descriptor, signature, value);
			}

			@Override
			public MethodVisitor visitMethod(int access, String name, String descriptor, String signature,
					String[] exceptions) {
				return super.visitMethod(mod(access), name, descriptor, signature, exceptions);
			}

			@Override
			public void visitInnerClass(String name, String outerName, String innerName, int access) {
				super.visitInnerClass(name, outerName, innerName, mod(access));
			}

			// from
			// https://github.com/FabricMC/fabric-loader/blob/master/src/main/java/net/fabricmc/loader/impl/transformer/PackageAccessFixer.java
			private int mod(int access) {
				if ((access & 0x7) != Opcodes.ACC_PRIVATE)
					return (access & (~0x7)) | Opcodes.ACC_PUBLIC;

				return access;
			}

		}, 0);
		return writer.toByteArray();
	}

	private static byte[] transformGuava(String name, byte[] input) {
		switch (name) {
			case OBJECTS:
				return transformObjects(input);
			case ITERATORS:
				return transformIterators(input);
			case FUTURES:
				return transformFutures(input);
		}

		return input;
	}

	private static byte[] transformObjects(byte[] input) {
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

	private static byte[] transformIterators(byte[] input) {
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

	private static byte[] transformFutures(byte[] input) {
		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(writer, 0);

		MethodVisitor addCallback = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "addCallback",
				"(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/FutureCallback;)V",
				null, null);
		addCallback.visitVarInsn(Opcodes.ALOAD, 0);
		addCallback.visitVarInsn(Opcodes.ALOAD, 1);
		addCallback.visitMethodInsn(Opcodes.INVOKESTATIC, "io/github/solclient/wrapper", "addCallback",
				"(Lcom/google/common/util/concurrent/ListenableFuture;Lcom/google/common/util/concurrent/FutureCallback;)V",
				false);
		addCallback.visitInsn(Opcodes.ARETURN);
		addCallback.visitMaxs(3, 3);

		return writer.toByteArray();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void addCallback(ListenableFuture future, FutureCallback callback) {
		Futures.addCallback(future, callback, MoreExecutors.directExecutor());
	}

}
