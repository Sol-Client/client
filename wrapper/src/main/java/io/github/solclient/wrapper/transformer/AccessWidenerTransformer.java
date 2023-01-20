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
