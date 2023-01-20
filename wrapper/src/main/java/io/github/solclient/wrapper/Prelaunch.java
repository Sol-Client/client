package io.github.solclient.wrapper;

import java.io.*;
import java.lang.invoke.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

import org.apache.logging.log4j.*;

import io.github.solclient.util.*;
import net.fabricmc.tinyremapper.*;

/**
 * Pre-launch jar stuff.
 */
public final class Prelaunch {

	private static final Logger LOGGER = LogManager.getLogger();
	private static final String OPTIFINE_USER_AGENT = "Mozilla/5.0";

	public static URL[] prepareClasspath() throws IOException {
		List<URL> result = new ArrayList<>(2);
		Path cache = Paths.get(".sol-client-wrapper");
		if (!Files.isDirectory(cache))
			Files.createDirectories(cache);

		String jar = System.getProperty("io.github.solclient.wrapper.jar");
		if (jar != null) {
			Path jarPath = Paths.get(jar);
			result.add(remapGameJar(cache, jarPath).toUri().toURL());

			if (GlobalConstants.optifine) {
				try {
					result.add(0, fetchAndRemapOptiFineJar(cache, jarPath).toUri().toURL());
				} catch (Throwable error) {
					GlobalConstants.optifine = false;
					LOGGER.error("Could not fetch and remap OptiFine jar", error);
				}
			}
		} else if (!GlobalConstants.DEV || GlobalConstants.optifine)
			throw new UnsupportedOperationException("-Dio.github.solclient.wrapper.jar is required");

		return result.toArray(new URL[0]);
	}

	private static Path remapGameJar(Path cache, Path gameJar) throws IOException {
		String gameJarName = gameJar.getFileName().toString();
		if (gameJarName.indexOf('.') != -1)
			gameJarName = gameJarName.substring(0, gameJarName.lastIndexOf('.'));
		gameJarName += "-intermediary.jar";

		Path intermediaryGameJar = cache.resolve(gameJarName);
		Path intermediaryGameJarTemp = intermediaryGameJar.resolveSibling(intermediaryGameJar.getFileName() + ".temp");

		if (Files.exists(intermediaryGameJar))
			return intermediaryGameJar;

		LOGGER.info("Remapping game jar...");
		remap(gameJar, intermediaryGameJarTemp);
		Files.move(intermediaryGameJarTemp, intermediaryGameJar);

		return intermediaryGameJar;
	}

	private static Path fetchAndRemapOptiFineJar(Path cache, Path gameJar) throws Throwable {
		LOGGER.info("Fetching and remapping OptiFine jar...");

		Path optifineJar = cache.resolve(GlobalConstants.OPTIFINE_JAR + ".jar");
		Path optifineExtractedJar = cache.resolve(GlobalConstants.OPTIFINE_JAR + "-extracted.jar");
		Path optifineIntermediaryJar = cache.resolve(GlobalConstants.OPTIFINE_JAR + "-intermediary.jar");
		Path optifineIntermediaryJarTemp = optifineIntermediaryJar
				.resolveSibling(optifineIntermediaryJar.getFileName() + ".temp");

		if (Files.exists(optifineIntermediaryJar))
			return optifineIntermediaryJar;

		URL source = extractOptiFineUrl(GlobalConstants.OPTIFINE_JAR);
		Utils.urlToFile(OPTIFINE_USER_AGENT, source, optifineJar);

		try (URLClassLoader loader = new URLClassLoader(new URL[] { optifineJar.toUri().toURL() })) {
			Class<?> patcher = loader.loadClass("optifine.Patcher");
			MethodHandle main = MethodHandles.lookup().findStatic(patcher, "main", GlobalConstants.MAIN_METHOD);
			main.invokeExact(
					new String[] { gameJar.toString(), optifineJar.toString(), optifineExtractedJar.toString() });
		}

		remap(optifineExtractedJar, optifineIntermediaryJarTemp, gameJar);
		Files.move(optifineIntermediaryJarTemp, optifineIntermediaryJar);

		return optifineIntermediaryJar;
	}

	// kind of makes me feel guilty but it works...
	private static URL extractOptiFineUrl(String version) throws IOException, IndexOutOfBoundsException {
		String html = Utils.urlToString(OPTIFINE_USER_AGENT,
				new URL("https://optifine.net/adloadx?f=" + version + ".jar"));
		int start = html.indexOf("<a href='downloadx?f=") + 21;
		String url = "downloadx?f=" + html.substring(start, html.indexOf("'", start));
		if (!url.startsWith("https://")) {
			if (url.charAt(0) != '/')
				url = '/' + url;
			url = "https://optifine.net" + url;
		}
		return new URL(url);
	}

	private static void remap(Path in, Path out, Path... classpath) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(Prelaunch.class.getResourceAsStream("/mappings/mappings.tiny")))) {
			// @formatter:off
			TinyRemapper remapper = TinyRemapper.newRemapper()
					.withMappings(TinyUtils.createTinyMappingProvider(reader, "official", "intermediary"))
					.build();
			// @formatter:on
			remapper.readClassPathAsync(classpath);
			try (OutputConsumerPath output = new OutputConsumerPath.Builder(out).assumeArchive(true).build()) {
				output.addNonClassFiles(in, NonClassCopyMode.FIX_META_INF, remapper);
				remapper.readInputsAsync(in);
				remapper.apply(output);
				remapper.finish();
			}
		}
	}

}
