package io.github.solclient.client.ui.screen;

import java.io.*;

import io.github.solclient.client.util.access.AccessMinecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;

public class FolderResourcePackEntry extends ResourcePackListEntry {

	private static final ResourceLocation FOLDER_ICON = new ResourceLocation("textures/gui/folder.png");
	private String name;
	private int size;
	private File folder;
	private BetterResourcePackList list;
	private BetterResourcePackList sublist;

	public FolderResourcePackEntry(GuiScreenResourcePacks resourcePacksGUIIn, BetterResourcePackList list,
			File folder) {
		super(resourcePacksGUIIn);
		this.folder = folder;
		this.list = list;
		this.name = folder.getName();
		this.size = folder.list().length;
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY,
			boolean isSelected) {
		super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);

		if (mc.gameSettings.touchscreen || isSelected) {
			Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
		}
	}

	public BetterResourcePackList getSublist() throws IOException {
		if (sublist == null) {
			ResourcePackRepository repo = new ResourcePackRepository(folder,
					new File(mc.mcDataDir, "server-resource-packs"),
					AccessMinecraft.getInstance().getDefaultResourcePack(),
					AccessMinecraft.getInstance().getMetadataSerialiser(), mc.gameSettings);

			repo.updateRepositoryEntriesAll();

			sublist = new BetterResourcePackList(mc, resourcePacksGUI, 200, list.getHeight(), repo, list.getSupplier());
		}

		return sublist;
	}

	@Override
	public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_,
			int p_148278_6_) {
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
	protected int func_183019_a() {
		return 1;
	}

	@Override
	protected String func_148311_a() {
		return I18n.format("sol_client.packs.folder", size);
	}

	@Override
	protected String func_148312_b() {
		return name;
	}

	@Override
	protected boolean func_148310_d() {
		return false;
	}

	@Override
	protected void func_148313_c() {
		GlStateManager.enableBlend();

		mc.getTextureManager().bindTexture(FOLDER_ICON);
	}

}
