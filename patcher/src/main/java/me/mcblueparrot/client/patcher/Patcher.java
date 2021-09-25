package me.mcblueparrot.client.patcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import io.sigpipe.jbsdiff.Patch;

public class Patcher {

	public static void main(String[] args) throws Exception {
		File vanillaJar = new File(args[0]);
		File patchedJar = new File(args[1]);
		File optiFineJar = new File(args[2]);
		File optiFineExtract = new File(vanillaJar.getParentFile(), "OptiFine-Mod.jar");

		Method patcherMethod = Class.forName("optifine.Patcher").getMethod("process", File.class, File.class, File.class);
		patcherMethod.invoke(null, vanillaJar, optiFineJar, optiFineExtract);
		ZipFile optiFineExtractZip = new ZipFile(optiFineExtract);

		if(patchedJar.exists()) {
			patchedJar.delete();
		}

		try(
				ZipOutputStream jarOut = new ZipOutputStream(
					new FileOutputStream(patchedJar));
				ZipFile jarIn = new ZipFile(vanillaJar)
		) {
			class Entry {

				Supplier<InputStream> input;
				String name;

				public Entry(Supplier<InputStream> input, String name) {
					this.input = input;
					this.name = name;
				}

			}

			List<Entry> entries = new ArrayList<>();
			if(optiFineJar != null) {
				String[] files = toString("/patches/list.txt").split(":");
				System.out.println(String.join(", ", files));
				for(String file : files) {
					if(file.isEmpty() || file.charAt(0) == '\n') continue;
					jarOut.putNextEntry(new ZipEntry(file));
					jarOut.write(toByteArray("/patches/" + file));
				}

				Enumeration<? extends ZipEntry> optiFineEntries = optiFineExtractZip.entries();

				while(optiFineEntries.hasMoreElements()) {
					ZipEntry entry = optiFineEntries.nextElement();
					if(jarIn.getEntry(entry.getName()) == null) {
						//jarOut.putNextEntry(new ZipEntry(entry.getName()));
						InputStream input = optiFineExtractZip.getInputStream(entry);
						//copy(input, jarOut);
						entries.add(new Entry(() -> input, entry.getName()));
					}
				}
			}

			Enumeration<? extends ZipEntry> entriesEnum = jarIn.entries();
			while(entriesEnum.hasMoreElements()) {
				ZipEntry entry = entriesEnum.nextElement();
				entries.add(new Entry(() -> {
					try {
						return jarIn.getInputStream(entry);
					} catch (IOException error) {
						throw new IllegalStateException(error);
					}
				}, entry.getName()));
			}
			for(Entry entry : entries) {
				if(entry.name.startsWith("META-INF")) {
					continue;
				}
				InputStream entryInput = entry.input.get();
				InputStream patchInput;
				jarOut.putNextEntry(new ZipEntry(entry.name));
				byte[] bytes = toByteArray(entryInput);

				if(optiFineExtractZip.getEntry(entry.name) != null) {
					try(InputStream input = optiFineExtractZip.getInputStream(optiFineExtractZip.getEntry(entry.name))) {
						bytes = toByteArray(input);
					}
				}

				if((patchInput = Patcher.class.getResourceAsStream("/patches/" + entry.name + ".patch")) != null) {
					ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
					Patch.patch(bytes, toByteArray(patchInput), byteOutput);
					bytes = byteOutput.toByteArray();
				}
				jarOut.write(bytes);
				entryInput.close();
			}
		}
		optiFineExtractZip.close();
	}

	private static void copy(InputStream input, OutputStream output) throws IOException {
		long count = 0;
		int number;
		byte[] buffer = new byte[8192];
		while(-1 != (number = input.read(buffer))) {
			output.write(buffer, 0, number);
			count += number;
		}
	}

	private static byte[] toByteArray(String resource) throws IOException {
		return toByteArray(Patcher.class.getResourceAsStream(resource));
	}

	private static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		int length = 0;
		byte[] buffer = new byte[8192];
		while((length = input.read(buffer, 0, buffer.length)) != -1) {
			output.write(buffer, 0, length);
		}
		byte[] result = output.toByteArray();
		output.close();
		return result;
	}

	private static String toString(String resource) throws IOException {
		StringBuilder result = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(Patcher.class.getResourceAsStream(resource)))) {
			String line;
			boolean first = true;
			while((line = reader.readLine()) != null) {
				if(!first) {
					result.append("\n");
				}
				result.append(line);
				first = false;
			}
		}
		return result.toString();
	}

}
