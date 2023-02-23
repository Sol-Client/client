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
