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

package io.github.solclient.wrapper;

import java.io.*;
import java.lang.invoke.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipFile;

import org.apache.logging.log4j.*;

import io.github.solclient.util.*;
import net.fabricmc.tinyremapper.*;

/**
 * Pre-launch jar stuff.
 */
public final class Prelaunch {

	private static Path gameJar;
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String OPTIFINE_USER_AGENT = "Mozilla/5.0";

	private static Path getGameJar() {
		if (gameJar != null)
			return gameJar;

		String jar = System.getProperty("io.github.solclient.wrapper.jar");
		if (jar == null) {
			for (String file : System.getProperty("java.class.path").split(File.pathSeparator)) {
				try {
					try (ZipFile zip = new ZipFile(file)) {
						if (zip.getEntry("a.class") != null) {
							jar = file;
							break;
						}
					}
				} catch (Throwable ignored) {
				}
			}
		}

		if (jar == null)
			throw new UnsupportedOperationException("-Dio.github.solclient.wrapper.jar is required");

		gameJar = Paths.get(jar);

		return gameJar;
	}

	public static void prepare() {
		if (System.getProperty("mixin.service") == null)
			System.setProperty("mixin.service", "io.github.solclient.wrapper.WrapperMixinService");

		if (GlobalConstants.NO_LAUNCHER_WARNINGS)
			return;

		try {

			StackTraceElement[] trace = Thread.currentThread().getStackTrace();
			String top = trace[trace.length - 1].getClassName();

			if (top.startsWith("org.multimc.")) {
				LOGGER.warn(
						"This instance was intended for Prism Launcher, and will not work with MultiMC until a specific issue is fixed.");
				LOGGER.warn("If you do find a workaround, we are happy to accept it!");
			} else if (top.startsWith("org.polymc.")) {
				LOGGER.warn(
						"We no longer support your launcher! Please use Prism Launcher instead (https://prismlauncher.org).");
				LOGGER.warn("Since it is outdated, do not be surprised if it breaks or is already broken.");
			} else
				return;

			LOGGER.warn(
					"(you can disable these warnings with -Dio.github.solclient.wrapper.no_launcher_warnings=true)");
		} catch (Throwable ignored) {
		}
	}

	public static URL[] prepareClasspath() throws IOException {
		List<URL> result = new ArrayList<>(2);
		Path cache = Paths.get(".sol-client-launch");
		if (!Files.isDirectory(cache))
			Files.createDirectories(cache);

		if (!GlobalConstants.DEV)
			result.add(remapGameJar(cache).toUri().toURL());

		if (GlobalConstants.optifine) {
			try {
				result.add(0, fetchAndRemapOptiFineJar(cache).toUri().toURL());
			} catch (Throwable error) {
				GlobalConstants.optifine = false;
				LOGGER.error("Could not fetch and remap OptiFine jar", error);
			}
		}

		return result.toArray(new URL[0]);
	}

	private static Path remapGameJar(Path cache) throws IOException {
		String gameJarName = getGameJar().getFileName().toString();
		if (gameJarName.indexOf('.') != -1)
			gameJarName = gameJarName.substring(0, gameJarName.lastIndexOf('.'));
		gameJarName += "-intermediary.jar";

		Path intermediaryGameJar = cache.resolve(gameJarName);
		Path intermediaryGameJarTemp = intermediaryGameJar.resolveSibling(intermediaryGameJar.getFileName() + ".temp");

		if (Files.exists(intermediaryGameJar))
			return intermediaryGameJar;

		LOGGER.info("Remapping game jar...");
		remap(getGameJar(), intermediaryGameJarTemp);
		Files.move(intermediaryGameJarTemp, intermediaryGameJar);

		return intermediaryGameJar;
	}

	private static Path fetchAndRemapOptiFineJar(Path cache) throws Throwable {
		Path optifineJar = cache.resolve(GlobalConstants.OPTIFINE_JAR + ".jar");
		Path optifineExtractedJar = cache.resolve(GlobalConstants.OPTIFINE_JAR + "-extracted.jar");
		Path optifineIntermediaryJar = cache.resolve(GlobalConstants.OPTIFINE_JAR + "-intermediary.jar");
		Path optifineIntermediaryJarTemp = optifineIntermediaryJar
				.resolveSibling(optifineIntermediaryJar.getFileName() + ".temp");

		if (Files.exists(optifineIntermediaryJar))
			return optifineIntermediaryJar;

		LOGGER.info("Fetching and remapping OptiFine jar...");

		URL source = extractOptiFineUrl(GlobalConstants.OPTIFINE_JAR);
		Utils.urlToFile(OPTIFINE_USER_AGENT, source, optifineJar);

		try (URLClassLoader loader = new URLClassLoader(
				new URL[] { optifineJar.toUri().toURL(), GlobalConstants.DEV ? getGameJar().toUri().toURL() : null })) {
			Class<?> patcher = loader.loadClass("optifine.Patcher");
			MethodHandle main = MethodHandles.lookup().findStatic(patcher, "main", GlobalConstants.MAIN_METHOD);
			main.invokeExact(
					new String[] { getGameJar().toString(), optifineJar.toString(), optifineExtractedJar.toString() });
		}

		remap(optifineExtractedJar, optifineIntermediaryJarTemp, getGameJar());
		Files.move(optifineIntermediaryJarTemp, optifineIntermediaryJar);
		Files.delete(optifineJar);
		Files.delete(optifineExtractedJar);

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
					.withMappings(TinyUtils.createTinyMappingProvider(reader, "official", GlobalConstants.MAPPINGS))
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
