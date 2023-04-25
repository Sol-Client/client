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

// TODO multimc dist with advanced features bool or such
public final class PrismDist {

	private static final String UID = "io.github.solclient.wrapper";
	private static final String OPTIFINE_UID = "io.github.solclient.wrapper.optiflag";

	public static void export(DistTask task, Path input, Path output, Project project, String version)
			throws IOException {
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
							obj( "uid", UID ),
							obj( "uid", OPTIFINE_UID )
						),
						"formatVersion", 1
					),
			writer);
			// @formatter:on

			out.putNextEntry(new ZipEntry("patches/" + UID + ".json"));
			// @formatter:off
			JsonSerializer.write(
					obj(
						"formatVersion", 1,
						"name", "Sol Client",
						"uid", UID,
						"version", version,
						"+libraries", arr(
							obj(
								"name", "io.github.solclient:wrapper:" + version,
								"MMC-hint", "local",
								"MMC-filename", "sol-client-wrapper.jar"
							)
						),
						"mainClass", "io.github.solclient.wrapper.Launcher"
					),
			writer);
			// @formatter:on

			out.putNextEntry(new ZipEntry("patches/" + OPTIFINE_UID + ".json"));
			// @formatter:off
			JsonSerializer.write(
					obj(
						"formatVersion", 1,
						"name", "OptiFine",
						"uid", OPTIFINE_UID,
						"version", "(default)",
						"+jvmArgs", arr( "-Dio.github.solclient.wrapper.optifine=true" )
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
		}
	}

}
