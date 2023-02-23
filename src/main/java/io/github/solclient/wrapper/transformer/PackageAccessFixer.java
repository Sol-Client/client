/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.wrapper.transformer;

import org.objectweb.asm.*;

import io.github.solclient.util.GlobalConstants;

public final class PackageAccessFixer extends ClassVisitor {

	public PackageAccessFixer(ClassVisitor classVisitor) {
		super(Opcodes.ASM9, classVisitor);
	}

	public static byte[] fix(String name, byte[] input) {
		if (!(GlobalConstants.DEV && (name.startsWith("net.minecraft.") || name.startsWith("com.mojang.blaze3d."))))
			return input;

		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(new PackageAccessFixer(writer), 0);
		return writer.toByteArray();
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
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
	private static int mod(int access) {
		if ((access & 0x7) != Opcodes.ACC_PRIVATE)
			return (access & (~0x7)) | Opcodes.ACC_PUBLIC;

		return access;
	}

}
