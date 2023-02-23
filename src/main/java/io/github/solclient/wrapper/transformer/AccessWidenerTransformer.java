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

import java.io.*;

import org.objectweb.asm.*;

import io.github.solclient.util.GlobalConstants;
import io.github.solclient.wrapper.ClassWrapper;
import lombok.experimental.UtilityClass;
import net.fabricmc.accesswidener.*;

@UtilityClass
public class AccessWidenerTransformer {

	private final AccessWidener WIDENER = new AccessWidener();

	public void addWideners(String... resources) throws IOException {
		if (resources.length == 0)
			return;

		AccessWidenerReader reader = new AccessWidenerReader(WIDENER);

		for (String resource : resources) {
			try (BufferedReader resourceReader = new BufferedReader(
					new InputStreamReader(ClassWrapper.getInstance().getResourceAsStream(resource)))) {
				reader.read(resourceReader, GlobalConstants.DEV ? "named" : "intermediary");
			}
		}
	}

	public byte[] transform(String name, byte[] input) {
		if (name.equals("org.lwjgl.opengl.LinuxKeycodes"))
			return transformLwjgl(input);

		if (!WIDENER.getTargets().contains(name))
			return input;

		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(AccessWidenerClassVisitor.createClassVisitor(Opcodes.ASM9, writer, WIDENER), 0);
		return writer.toByteArray();
	}

	// unfortunately access wideners only work on MC classes
	private byte[] transformLwjgl(byte[] input) {
		ClassReader reader = new ClassReader(input);
		ClassWriter writer = new ClassWriter(0);
		reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {

			@Override
			public void visit(int version, int access, String name, String signature, String superName,
					String[] interfaces) {
				super.visit(version, access | Opcodes.ACC_PUBLIC, name, signature, superName, interfaces);
			}

		}, 0);
		return writer.toByteArray();
	}

}
