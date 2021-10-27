package me.mcblueparrot.client.packeditor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.gui.GuiResourcePackList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;

public class PackSelectorScreen extends GuiScreen {

    private GuiResourcePackList list;

    @Override
    public void initGui() {
        ResourcePackRepository repo = mc.getResourcePackRepository();
        List<ResourcePackListEntry> foundPacks = repo.getRepositoryEntriesAll().stream()
                .filter((entry) -> !repo.getRepositoryEntries().contains(entry))
                .map((entry) -> new ResourcePackListEntryFound(null, entry))
                .collect(Collectors.toList());
        list = new GuiResourcePackAvailable(mc, width, height, foundPacks);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        list.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        list.handleMouseInput();
    }

}
