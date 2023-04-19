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

import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.impl.core.mixins.client.MinecraftClientAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ResourcePackScreen;
import net.minecraft.client.gui.screen.resourcepack.ResourcePackWidget;
import net.minecraft.client.resource.ResourcePackLoader;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;

public class FolderResourcePackEntry extends ResourcePackWidget {

	private static final Identifier FOLDER_ICON = new Identifier("textures/gui/folder.png");
	private String name;
	private int size;
	private File folder;
	private BetterResourcePackList list;
	private BetterResourcePackList sublist;

	public FolderResourcePackEntry(ResourcePackScreen screen, BetterResourcePackList list, File folder) {
		super(screen);
		this.folder = folder;
		this.list = list;
		this.name = folder.getName();
		this.size = folder.list().length;
	}

	@Override
	public void render(int index, int x, int y, int rowWidth, int rowHeight, int mouseX, int mouseY, boolean hovered) {
		super.render(index, x, y, rowWidth, rowHeight, mouseX, mouseY, hovered);

		if (client.options.touchscreen || hovered)
			DrawableHelper.fill(x, y, x + 32, y + 32, -1601138544);
	}

	public BetterResourcePackList getSublist() throws IOException {
		if (sublist == null) {
			ResourcePackLoader repo = new ResourcePackLoader(folder,
					new File(client.runDirectory, "server-resource-packs"),
					((MinecraftClientAccessor) MinecraftClient.getInstance()).getDefaultResourcePack(),
					((MinecraftClientAccessor) MinecraftClient.getInstance()).getMetadataSerialiser(), client.options);

			repo.initResourcePacks();

			sublist = new BetterResourcePackList(client, screen, 200, list.getHeight(), repo, list.getSupplier());
		}

		return sublist;
	}

	@Override
	public boolean mouseClicked(int index, int mouseX, int mouseY, int button, int x, int y) {
		if (!folder.exists()) {
			return false;
		}

		try {
			list.delegate = getSublist();
		} catch (IOException error) {
		}

		return true;
	}

	@Override
	protected int getFormat() {
		return 1;
	}

	@Override
	protected String getDescription() {
		return I18n.translate("sol_client.packs.folder", size);
	}

	@Override
	protected String getName() {
		return name;
	}

	@Override
	protected boolean isVisible() {
		return false;
	}

	@Override
	protected void bindIcon() {
		GlStateManager.enableBlend();

		client.getTextureManager().bindTexture(FOLDER_ICON);
	}

}
