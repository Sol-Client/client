package io.github.solclient.gradle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import masecla.modrinth4j.main.ModrinthAPI;
import masecla.modrinth4j.model.version.ProjectVersion;
import masecla.modrinth4j.model.version.ProjectVersion.ProjectFile;
import net.fabricmc.mappingio.MappingReader;
import net.fabricmc.mappingio.tree.MappingTree;
import net.fabricmc.mappingio.tree.MemoryMappingTree;
import net.md_5.specialsource.JarMapping;

public final class ReplayModRemapper {

	public static final Map<String, String> RELOCATIONS = new HashMap<>();
	private static final String REMAPPER_VERSION = "1";
	private static final String VERSION_ID = "thijJjIp";
	private static final String VERSION_COMBINED;

	static {
		RELOCATIONS.put("com/replaymod/core/ReplayModBackend",
				"io/github/solclient/client/mod/impl/replay/fix/SCReplayModBackend");
		RELOCATIONS.put("com/replaymod/core/SettingsRegistry",
				"io/github/solclient/client/mod/impl/replay/fix/SCSettingsRegistry");
		RELOCATIONS.put("com/replaymod/core/versions/scheduler/SchedulerImpl",
				"io/github/solclient/client/mod/impl/replay/fix/SCScheduler");
		RELOCATIONS.put("com/replaymod/core/utils/ModInfoGetter",
				"io/github/solclient/client/mod/impl/replay/fix/SCModInfoGetter");
		RELOCATIONS.put("com/replaymod/lib/de/johni0702/minecraft/gui/utils/EventRegistrations",
				"io/github/solclient/client/mod/impl/replay/fix/SCEventRegistrations");
		RELOCATIONS.put("com/replaymod/recording/ReplayModRecording",
				"io/github/solclient/client/mod/impl/replay/fix/SCReplayModRecording");
		RELOCATIONS.put("com/replaymod/compat/ReplayModCompat",
				"io/github/solclient/client/mod/impl/replay/fix/SCReplayModCompat");
		RELOCATIONS.put("net/minecraftforge/client/event/GuiScreenEvent$ActionPerformedEvent$Pre",
				"io/github/solclient/client/event/impl/ActionPerformedEvent");
		RELOCATIONS.put("net/minecraftforge/fml/client/registry/ClientRegistry",
				"io/github/solclient/client/mod/impl/replay/fix/SCClientRegistry");
		RELOCATIONS.put("net/minecraftforge/fml/common/eventhandler/SubscribeEvent",
				"io/github/solclient/client/event/EventHandler");
		RELOCATIONS.put("net/minecraftforge/fml/common/gameevent/PlayerEvent$ItemPickupEvent",
				"io/github/solclient/client/event/impl/ItemPickupEvent");
		RELOCATIONS.put("net/minecraftforge/event/entity/player/PlayerSleepInBedEvent",
				"io/github/solclient/client/event/impl/PlayerSleepEvent");
		RELOCATIONS.put("net/minecraftforge/client/event/EntityViewRenderEvent$CameraSetup",
				"io/github/solclient/client/event/impl/CameraRotateEvent");
		RELOCATIONS.put("net/minecraftforge/client/event/RenderGameOverlayEvent$ElementType",
				"io/github/solclient/client/event/impl/GameOverlayElement");
		RELOCATIONS.put("net/minecraftforge/client/event/RenderGameOverlayEvent$Pre",
				"io/github/solclient/client/event/impl/PreGameOverlayRenderEvent");
		RELOCATIONS.put("net/minecraftforge/client/event/RenderGameOverlayEvent$Post",
				"io/github/solclient/client/event/impl/PostGameOverlayRenderEvent");
		RELOCATIONS.put("net/minecraftforge/fml/common/gameevent/TickEvent$RenderTickEvent",
				"io/github/solclient/client/event/impl/RenderTickEvent");
		RELOCATIONS.put("net/minecraftforge/fml/common/network/internal/FMLProxyPacket",
				"io/github/solclient/client/util/Appendix");
		RELOCATIONS.put("net/minecraftforge/client/event/GuiScreenEvent$MouseInputEvent$Pre",
				"io/github/solclient/client/event/impl/PreGuiMouseInputEvent");
		RELOCATIONS.put("net/minecraftforge/client/event/GuiScreenEvent$KeyboardInputEvent$Pre",
				"io/github/solclient/client/event/impl/PreGuiKeyboardInputEvent");
		RELOCATIONS.put("net/minecraftforge/fml/common/FMLCommonHandler",
				"io/github/solclient/client/mod/impl/replay/fix/SCFMLCommonHandler");
		RELOCATIONS.put("net/minecraftforge/client/ForgeHooksClient", "io/github/solclient/client/mod/impl/replay/fix/SCForgeHooksClient");
		RELOCATIONS.put("com/replaymod/core/SettingsRegistry$SettingKey", "io/github/solclient/client/mod/impl/replay/fix/SCSettingsRegistry$SettingKey");

		VERSION_COMBINED = REMAPPER_VERSION + '-' + VERSION_ID + "-" + RELOCATIONS.hashCode();
	}

	private final Path workDir;
	private final Path versionFile;
	private final Path replayModForgeJar;

	private ReverseMCP mcp;
	private final MemoryMappingTree yarn = new MemoryMappingTree();

	private final Map<String, List<String>> classParents = new HashMap<>();

	public ReplayModRemapper(org.gradle.api.Project project) {
		workDir = project.getProjectDir().toPath().resolve(".gradle/replay-mod");
		versionFile = workDir.resolve("version.txt");
		replayModForgeJar = workDir.resolve("replay-mod-forge.jar");
	}

	public void prepare() throws InterruptedException, ExecutionException, MalformedURLException, IOException {
		if (!Files.isDirectory(workDir))
			Files.createDirectories(workDir);
		else if (Files.exists(versionFile)) {
			try (InputStream in = Files.newInputStream(versionFile)) {
				String currentVersion = new String(in.readAllBytes(), StandardCharsets.US_ASCII);
				if (!VERSION_COMBINED.equals(currentVersion))
					clear();
			}
		}

		if (!Files.exists(versionFile)) {
			try (BufferedWriter writer = Files.newBufferedWriter(versionFile)) {
				writer.write(VERSION_COMBINED);
			}
		}

		ModrinthAPI modrinth = ModrinthAPI.unlimited("");
		ProjectVersion replayMod = modrinth.versions().getVersion(VERSION_ID).get();
		ProjectFile primary = null;

		if (replayMod.getFiles().length == 0)
			throw new IllegalStateException("No files found for " + VERSION_ID);

		for (ProjectFile file : replayMod.getFiles()) {
			if (file.isPrimary()) {
				primary = file;
				break;
			}
		}

		if (primary == null)
			primary = replayMod.getFiles()[0];

		Utils.download(new URL(primary.getUrl()), replayModForgeJar, primary.getHashes().getSha1());

		JarMapping mcp = new JarMapping();
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(getClass().getResourceAsStream("/joined.srg"), StandardCharsets.UTF_8))) {
			mcp.loadMappings(reader, null, null, false);
		}

		this.mcp = new ReverseMCP(mcp);

		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/mappings/mappings.tiny"),
				StandardCharsets.UTF_8)) {
			MappingReader.read(reader, yarn);
		}

		// potentially should be replaced with an on the fly fancy parser thing
		try (Reader reader = new InputStreamReader(getClass().getResourceAsStream("/class_parents.json"),
				StandardCharsets.UTF_8)) {
			for (Entry<String, JsonElement> entry : JsonParser.parseReader(reader).getAsJsonObject().entrySet()) {
				List<String> list = new LinkedList<>();
				for (JsonElement parent : entry.getValue().getAsJsonArray())
					list.add(parent.getAsString());

				classParents.put(entry.getKey(), list);
			}
		}
	}

	public Path createIntermediaryMapped() throws IOException {
		return createMapped("intermediary");
	}

	public Path createNamedMapped() throws IOException {
		return createMapped("named");
	}

	private Path createMapped(String namespace) throws IOException {
		Path dest = getRemappedJar(namespace);
		try (ZipFile temp = new ZipFile(dest.toFile())) {
			return dest;
		} catch (IOException e) {
		}

		remap(replayModForgeJar, dest, namespace);
		return dest;
	}

	public void remap(Path src, Path dest, String namespace) throws IOException {
		remap(src, dest, mcp, yarn, namespace);
	}

	public void remap(Path src, Path dest, ReverseMCP mcp, MappingTree yarn, String namespace) throws IOException {
		try (ZipFile in = new ZipFile(src.toFile());
				ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(dest))) {
			for (ZipEntry entry : Collections.list(in.entries())) {
				if (entry.isDirectory())
					continue;

				try (InputStream entryIn = in.getInputStream(entry)) {
					ZipEntry newEntry = new ZipEntry(entry.getName());
					newEntry.setLastModifiedTime(FileTime.from(Instant.now()));
					out.putNextEntry(newEntry);

					if (!entry.getName().endsWith(".class")) {
						entryIn.transferTo(out);
						continue;
					}

					byte[] bytes = entryIn.readAllBytes();
					ClassReader reader = new ClassReader(bytes);
					ClassWriter writer = new ClassWriter(0);
					reader.accept(
							new ClassRemapper(writer, new ReplayModClassRemapper(mcp, yarn, classParents, namespace)),
							0);
					bytes = writer.toByteArray();
					out.write(bytes);
				}
			}
		}
	}

	private Path getRemappedJar(String namespace) {
		return workDir.resolve("replay-mod-remapped-" + namespace + ".jar");
	}

	private void clear() throws IOException {
		Files.walkFileTree(workDir, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				super.postVisitDirectory(dir, exc);
				if (dir.equals(workDir))
					return FileVisitResult.CONTINUE;

				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				super.visitFile(file, attrs);
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

		});
	}

}
