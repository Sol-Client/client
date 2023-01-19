package io.github.solclient.wrapper;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import org.apache.logging.log4j.*;

import net.fabricmc.tinyremapper.*;

public final class InjectedLibraries {

	private static final Logger LOGGER = LogManager.getLogger();

	public static URL[] get() throws IOException {
		List<URL> result = new ArrayList<>(2);
		Path cache = Paths.get(".sol-client-wrapper");
		if (!Files.isDirectory(cache))
			Files.createDirectories(cache);

		String jar = System.getProperty("io.github.solclient.wrapper.jar");
		if (jar != null) {
			Path gameJarLocation = Paths.get(jar);

			String gameJarName = gameJarLocation.getFileName().toString();
			if (gameJarName.indexOf('.') != -1)
				gameJarName = gameJarName.substring(0, gameJarName.lastIndexOf('.'));

			Path intermediaryGameJar = cache.resolve(gameJarName + "-intermediary.jar");
			Path intermediaryGameJarTemp = intermediaryGameJar
					.resolveSibling(intermediaryGameJar.getFileName() + ".temp");

			if (!Files.exists(intermediaryGameJar)) {
				LOGGER.info("Remapping game jar...");
				remap(gameJarLocation, intermediaryGameJarTemp);
				Files.move(intermediaryGameJarTemp, intermediaryGameJar);
			}

			result.add(intermediaryGameJar.toUri().toURL());
		}

		return result.toArray(new URL[0]);
	}

	private static void remap(Path in, Path out) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(InjectedLibraries.class.getResourceAsStream("/mappings/mappings.tiny")))) {
			// @formatter:off
			TinyRemapper remapper = TinyRemapper.newRemapper()
					.withMappings(TinyUtils.createTinyMappingProvider(reader, "official", "intermediary"))
					.build();
			// @formatter:on
			try (OutputConsumerPath output = new OutputConsumerPath.Builder(out).assumeArchive(true).build()) {
				output.addNonClassFiles(in, NonClassCopyMode.FIX_META_INF, remapper);
				remapper.readInputsAsync(in);
				remapper.apply(output);
				remapper.finish();
			}
		}
	}

}
