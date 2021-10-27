package me.mcblueparrot.client.ui;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiResourcePackAvailable;
import net.minecraft.client.resources.ResourcePackListEntry;
import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.util.EnumChatFormatting;

public class SearchableResourcePackList extends GuiResourcePackAvailable {

    private Supplier<String> supplier;

    public SearchableResourcePackList(Minecraft mcIn, int p_i45054_2_, int p_i45054_3_,
                                      List<ResourcePackListEntry> p_i45054_4_, Supplier<String> supplier) {
        super(mcIn, p_i45054_2_, p_i45054_3_, p_i45054_4_);
        this.supplier = supplier;
    }

    @Override
    public List<ResourcePackListEntry> getList() {

        return super.getList().stream().filter((entry) -> entry instanceof ResourcePackListEntryFound && EnumChatFormatting
                .getTextWithoutFormattingCodes(
                        ((ResourcePackListEntryFound) entry).func_148318_i().getResourcePackName() + " "
                                + ((ResourcePackListEntryFound) entry).func_148318_i()
                                .getTexturePackDescription().replace("\n", " "))
                .toLowerCase().contains(supplier.get().toLowerCase())).collect(Collectors.toList());
    }

}
