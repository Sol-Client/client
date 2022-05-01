package me.mcblueparrot.client.ui.screen;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.util.EnumChatFormatting;

public class BetterResourcePackList extends GuiResourcePackAvailable {

	private GuiScreenResourcePacks parent;
	public BetterResourcePackList delegate;
	protected List<ResourcePackListEntry> entries;
	private Supplier<String> supplier;
	private File folder;

	public int getHeight() {
		return height;
	}

	public File getFolder() {
		if(delegate != null) {
			return delegate.getFolder();
		}

		return folder;
	}

	public BetterResourcePackList(Minecraft mcIn, GuiScreenResourcePacks parent, int p_i45054_2_, int p_i45054_3_,
									  ResourcePackRepository repository, Supplier<String> supplier) throws IOException {
		super(mcIn, p_i45054_2_, p_i45054_3_, null);
		this.supplier = supplier;
		this.folder = repository.getDirResourcepacks();

		this.parent = parent;

		entries = new ArrayList<>();

		for(File file : folder.listFiles()) {
			if(!file.isDirectory()) {
				continue;
			}

			// Do not include actual packs.
			if(new File(file, "pack.mcmeta").exists()) {
				continue;
			}

			AtomicBoolean result = new AtomicBoolean();

			Files.walkFileTree(file.toPath(), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(file.toString().endsWith(".zip")) {
						ZipFile zip = new ZipFile(file.toFile());

						if(zip.getEntry("pack.mcmeta") == null) {
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
					if(Files.isRegularFile(dir.resolve("pack.mcmeta"))) {
						result.set(true);
						return FileVisitResult.TERMINATE;
					}

					return FileVisitResult.CONTINUE;
				}

			});

			if(result.get()) {
				entries.add(new FolderResourcePackEntry(parent, this, file));
			}
		}

		for(ResourcePackRepository.Entry entry : repository.getRepositoryEntriesAll()) {
			entries.add(new ResourcePackListEntryFound(parent, entry));
		}
	}

	@Override
	protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn,
			int mouseYIn) {
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();

		super.drawSlot(entryID, p_180791_2_, p_180791_3_, p_180791_4_, mouseXIn, mouseYIn);
	}

	@Override
	public List<ResourcePackListEntry> getList() {
		if(delegate != null) {
			return delegate.getList();
		}

		List<ResourcePackListEntry> entries;

		if(supplier.get().isEmpty()) {
			entries = this.entries;
		}
		else {
			entries = new ArrayList<>();
			populate(entries);
		}

		return entries.stream().filter((entry) -> {
			String name = "";
			String description = "";

			if(entry instanceof ResourcePackListEntryFound) {
				ResourcePackRepository.Entry repoEntry = ((ResourcePackListEntryFound) entry).func_148318_i();

				name = repoEntry.getResourcePackName();
				description = repoEntry.getTexturePackDescription();


				for(ResourcePackListEntry compareEntry : parent.getSelectedResourcePacks()) {

					if(compareEntry instanceof ResourcePackListEntryFound) {
						if(((ResourcePackListEntryFound) compareEntry).func_148318_i().equals(repoEntry)) {
							return false;
						}
					}
				}
			}
			else if(entry instanceof FolderResourcePackEntry) {
				FolderResourcePackEntry folder = (FolderResourcePackEntry) entry;

				name = folder.func_148312_b();
				description = folder.func_148311_a();
			}

			return EnumChatFormatting
					.getTextWithoutFormattingCodes(name + " " + description.replace("\n", " "))
					.toLowerCase().contains(supplier.get().toLowerCase());
		}).collect(Collectors.toList());
	}

	private void populate(List<ResourcePackListEntry> entries) {
		for(ResourcePackListEntry entry : this.entries) {
			if(entry instanceof FolderResourcePackEntry) {
				try {
					((FolderResourcePackEntry) entry).getSublist().populate(entries);
				}
				catch(IOException error) {
				}
			}
			else {
				entries.add(entry);
			}
		}
	}

	public Supplier<String> getSupplier() {
		return supplier;
	}

	public void up() {
		if(delegate != null && delegate.delegate != null) {
			delegate.up();
			return;
		}

		delegate = null;
	}

}
