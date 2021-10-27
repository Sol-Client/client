package me.mcblueparrot.client.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.Language;

public class BetterLanguageGui extends GuiSlot {

    private final List<String> langCodeList = Lists.<String>newArrayList();
    private final Map<String, Language> languageMap = Maps.<String, Language>newHashMap();

    public BetterLanguageGui(Minecraft mcIn, GuiLanguage parent) {
        super(mcIn, parent.width, parent.height, 32, parent.height - 65 + 4, 18);

        for(Language language : Minecraft.getMinecraft().getLanguageManager().getLanguages()) {
            this.languageMap.put(language.getLanguageCode(), language);
            this.langCodeList.add(language.getLanguageCode());
        }
    }

    protected int getSize() {
        return this.langCodeList.size();
    }

    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        Language language = this.languageMap.get(this.langCodeList.get(slotIndex));
        mc.getLanguageManager().setCurrentLanguage(language);
        mc.gameSettings.language = language.getLanguageCode();

        mc.getLanguageManager().onResourceManagerReload(mc.getResourceManager());
        try {
            Class.forName("net.optifine.Lang").getMethod("resourcesReloaded").invoke(null);
        }
        catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException error) {
            // OptiFine not installed
        }

        mc.fontRendererObj.setUnicodeFlag(mc.getLanguageManager().isCurrentLocaleUnicode() || mc.gameSettings.forceUnicodeFont);
        mc.fontRendererObj.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());

        ScaledResolution resolution = new ScaledResolution(mc);
        mc.currentScreen.setWorldAndResolution(mc, resolution.getScaledWidth(), resolution.getScaledHeight());

        mc.gameSettings.saveOptions();
    }

    protected boolean isSelected(int slotIndex) {
        return (this.langCodeList.get(slotIndex)).equals(mc.getLanguageManager().getCurrentLanguage().getLanguageCode());
    }

    protected int getContentHeight() {
        return this.getSize() * 18;
    }

    protected void drawBackground() {
        mc.currentScreen.drawDefaultBackground();
    }

    protected void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn) {
        mc.fontRendererObj.setBidiFlag(true);
        mc.currentScreen.drawCenteredString(mc.fontRendererObj,
                ((Language)this.languageMap.get(this.langCodeList.get(entryID))).toString(), this.width / 2, p_180791_3_ + 1, 16777215);
        mc.fontRendererObj.setBidiFlag(mc.getLanguageManager().getCurrentLanguage().isBidirectional());
    }

}
