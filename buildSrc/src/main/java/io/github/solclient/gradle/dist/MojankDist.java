package io.github.solclient.gradle.dist;

import static io.toadlabs.jfgjds.JsonGlobal.arr;
import static io.toadlabs.jfgjds.JsonGlobal.obj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gradle.api.Project;

import io.toadlabs.jfgjds.JsonSerializer;

public final class MojankDist {

	public static void export(DistTask task, Path input, Path output, Project project, String version)
			throws IOException {
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(output))) {
			Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

			String name = "Sol Client " + version;
			out.putNextEntry(new ZipEntry("versions/" + name + '/' + name + ".json"));
			// @formatter:off
			JsonSerializer.write(
					obj(
						"id", name,
						"inheritsFrom", "1.8.9",
						"type", "release",
						"libraries", arr(
							obj(
								"name", "io.github.solclient:wrapper:" + version
							)
						),
						"mainClass", "io.github.solclient.wrapper.Launcher"
					),
			writer);
			// @formatter:on
			writer.flush();

			out.putNextEntry(
					new ZipEntry("libraries/io/github/solclient/wrapper/" + version + "/wrapper-" + version + ".jar"));
			try (InputStream wrapperIn = Files.newInputStream(input)) {
				wrapperIn.transferTo(out);
			}
		}
	}

}
