package io.github.solclient.client.addon;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.*;

import io.github.solclient.client.mod.ModManager;
import io.github.solclient.util.Utils;
import io.github.solclient.wrapper.ClassWrapper;
import lombok.*;

@RequiredArgsConstructor
public final class AddonManager {

	private static final Logger LOGGER = LogManager.getLogger();

	@Getter
	private static AddonManager instance;
	private final Path directory;
	private List<AddonInfo> queuedAddons;
	@Getter
	private boolean loaded;

	public static void premain(String[] args) throws IOException {
		if (!Boolean.getBoolean("io.github.solclient.client.addon.disabled")) {
			// we don't have an MC instance, so we must manually parse

			Path folder;
			int gameDirIndex = ArrayUtils.indexOf(args, "--gameDir");
			if (gameDirIndex < args.length - 1 && gameDirIndex > 0)
				folder = Paths.get(args[gameDirIndex + 1]);
			else
				folder = Paths.get(".");

			folder = folder.resolve("addons");
			Utils.ensureDirectory(folder);

			LOGGER.info("Scanning for addons...");

			AddonManager addons = new AddonManager(folder);
			instance = addons;
			addons.discover();

			LOGGER.info("Discovered {} addons", addons.queuedAddons == null ? 0 : addons.queuedAddons.size());
		}
	}

	public void discover() throws IOException {
		Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (!(Files.isRegularFile(path) && path.getFileName().toString().endsWith(".jar")))
					return FileVisitResult.CONTINUE;

				try {
					queue(AddonInfo.parse(path));
				} catch (Throwable error) {
					LOGGER.error("Could not load addon info from {}", directory.relativize(path), error);
				}
				return FileVisitResult.CONTINUE;
			}

		});
	}

	/**
	 * Queue an addon for loading on initialisation.
	 *
	 * @param addon the addon.
	 */
	public void queue(AddonInfo addon) {
		if (queuedAddons == null)
			queuedAddons = new LinkedList<>();

		queuedAddons.add(addon);
	}

	public void load(ModManager mods) {
		if (queuedAddons == null)
			return;
		if (loaded)
			throw new UnsupportedOperationException("Already loaded");
		loaded = true;

		// arraylist since it apparently has better sorting speeds
		List<Addon> addons = new ArrayList<>();

		for (AddonInfo addon : queuedAddons) {
			try {
				ClassWrapper wrapper = ClassWrapper.getInstance();
				wrapper.addURL(addon.getPath().toUri().toURL());
				Class<?> mainClass = wrapper.loadClass(addon.getMain());
				addons.add(construct(mainClass));
			} catch (Throwable error) {
				LOGGER.error("Could not load addon {} from {}", addon.getId(), directory.relativize(addon.getPath()),
						error);
			}
		}

		addons.sort(Comparator.comparing(Addon::getName));

		// allow gc to do a thing
		queuedAddons.clear();
		queuedAddons = null;
	}

	private Addon construct(Class<?> clazz) throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// special method
		try {
			Method instanceMethod = clazz.getMethod("findAddonInstance");
			if (Addon.class.isAssignableFrom(instanceMethod.getReturnType()))
				return (Addon) instanceMethod.invoke(null);
		} catch (NoSuchMethodException ignored) {
		} catch (ReflectiveOperationException error) {
			LOGGER.warn(error);
		}

		Constructor<?> constructor = clazz.getConstructor();
		return (Addon) constructor.newInstance();
	}

	public List<AddonInfo> getQueuedAddons() {
		if (queuedAddons == null)
			return Collections.emptyList();

		return queuedAddons;
	}

}
