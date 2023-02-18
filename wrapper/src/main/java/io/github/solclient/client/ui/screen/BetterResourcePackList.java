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

package io.github.solclient.client.ui.screen;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.screen.resourcepack.*;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.util.Formatting;

public class BetterResourcePackList extends AvailableResourcePackListWidget {

	private final ResourcePackScreen parent;
	public BetterResourcePackList delegate;
	protected List<ResourcePackWidget> entries;
	private List<ResourcePackWidget> filteredEntries;
	private Supplier<String> supplier;
	private File folder;
	private int prevHash;

	public int getHeight() {
		return height;
	}

	public File getFolder() {
		if (delegate != null)
			return delegate.getFolder();

		return folder;
	}

	public BetterResourcePackList(MinecraftClient client, ResourcePackScreen parent, int x, int y,
			ResourcePackLoader loader, Supplier<String> supplier) throws IOException {
		super(client, x, y, null);
		this.supplier = supplier;
		this.folder = loader.getResourcePackDir();

		this.parent = parent;

		entries = new ArrayList<>();

		for (File file : folder.listFiles()) {
			if (!file.isDirectory()) {
				continue;
			}

			// Do not include actual packs.
			if (new File(file, "pack.mcmeta").exists()) {
				continue;
			}

			AtomicBoolean result = new AtomicBoolean();

			Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if (file.toString().endsWith(".zip")) {
						ZipFile zip = new ZipFile(file.toFile());

						if (zip.getEntry("pack.mcmeta") == null) {
							zip.close();
							return FileVisitResult.CONTINUE;
						}

						zip.close();

						result.set(true);
						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					if (Files.isRegularFile(dir.resolve("pack.mcmeta"))) {
						result.set(true);
						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

			if (result.get())
				entries.add(new FolderResourcePackEntry(parent, this, file));
		}

		for (ResourcePackLoader.Entry entry : loader.getAvailableResourcePacks())
			entries.add(new ResourcePackEntryWidget(parent, entry));
	}

	@Override
	protected void renderEntry(int index, int x, int y, int rowHeight, int mouseX, int mouseY) {
		GlStateManager.enableBlend();
		GlStateManager.enableAlphaTest();

		super.renderEntry(index, x, y, rowHeight, mouseX, mouseY);
	}

	@Override
	public List<ResourcePackWidget> getWidgets() {
		if (delegate != null)
			return delegate.getWidgets();

		String search = supplier.get();
		int hash = Objects.hash(search, parent.getSelectedPacks());
		if (hash == prevHash)
			return filteredEntries;

		prevHash = hash;

		List<ResourcePackWidget> entries;

		if (search.isEmpty())
			entries = this.entries;
		else {
			entries = new ArrayList<>();
			populate(entries);
		}

		filteredEntries = entries.stream().filter((entry) -> {
			String name = "";
			String description = "";

			if (entry instanceof ResourcePackEntryWidget) {
				ResourcePackLoader.Entry repoEntry = ((ResourcePackEntryWidget) entry).getEntry();

				name = repoEntry.getName();
				description = repoEntry.getDescription();

				for (ResourcePackWidget compareEntry : parent.getSelectedPacks())
					if (compareEntry instanceof ResourcePackEntryWidget)
						if (((ResourcePackEntryWidget) compareEntry).getEntry().equals(repoEntry))
							return false;
			} else if (entry instanceof FolderResourcePackEntry) {
				FolderResourcePackEntry folder = (FolderResourcePackEntry) entry;

				name = folder.getName();
				description = folder.getDescription();
			}

			return Formatting.strip(name + ' ' + description.replace("\n", " ")).toLowerCase()
					.contains(supplier.get().toLowerCase());
		}).collect(Collectors.toList());

		return filteredEntries;
	}

	private void populate(List<ResourcePackWidget> entries) {
		for (ResourcePackWidget entry : this.entries) {
			if (entry instanceof FolderResourcePackEntry) {
				try {
					((FolderResourcePackEntry) entry).getSublist().populate(entries);
				} catch (IOException error) {
				}
			} else {
				entries.add(entry);
			}
		}
	}

	public Supplier<String> getSupplier() {
		return supplier;
	}

	public void up() {
		if (delegate != null && delegate.delegate != null) {
			delegate.up();
			return;
		}

		delegate = null;
	}

}
