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
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;

import io.github.solclient.gradle.Utils;
import io.toadlabs.jfgjds.JsonSerializer;
import io.toadlabs.jfgjds.data.JsonArray;

// TODO multimc dist with advanced features bool or such
public final class PrismDist {

	private static final String UID = "io.github.solclient.wrapper";

	public static void export(DistTask task, Path input, Path output, Project project, Configuration libs, String version) throws IOException {
		try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(output))) {
			Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);

			out.putNextEntry(new ZipEntry("mmc-pack.json"));
			// @formatter:off
			JsonSerializer.write(
					obj(
						"components", arr(
							obj(
								"uid", "org.lwjgl",
								"version", "2.9.4-nightly-20150209"
							),
							obj(
								"important", true,
								"uid", "net.minecraft",
								"version", "1.8.9"
							),
							obj(
								"important", true,
								"uid", UID
							)
						),
						"formatVersion", 1
					),
			writer);
			// @formatter:on

			JsonArray libsArray = new JsonArray();
			// @formatter:off
			libsArray.add(
					obj(
						"name", "io.github.solclient:wrapper:" + version,
						"MMC-hint", "local",
						"MMC-filename", "sol-client-wrapper.jar"
					)
			);

			for (Dependency lib : libs.getDependencies()) {
				libsArray.add(
						obj(
							"name", Utils.format(lib),
							"url", Utils.getRepo(lib, project.getRepositories())
						)
				);
			}
			// @formatter:on

			out.putNextEntry(new ZipEntry("patches/" + UID + ".json"));
			// @formatter:off
			JsonSerializer.write(
					obj(
						"formatVersion", 1,
						"name", "Sol Client",
						"uid", UID,
						"version", version,
						"+libraries", libsArray,
						"mainClass", "io.github.solclient.wrapper.Launcher"
					),
			writer);
			// @formatter:on

			out.putNextEntry(new ZipEntry("instance.cfg"));
			writer.write("InstanceType=OneSix\n");
			writer.write("name=Sol Client\n");
			writer.flush();

			out.putNextEntry(new ZipEntry("libraries/sol-client-wrapper.jar"));
			try (InputStream wrapperIn = Files.newInputStream(input)) {
				wrapperIn.transferTo(out);
			}
			writer.flush();
		}
	}

}
