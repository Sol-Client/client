package io.github.solclient.client.ui.screen;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.options.LanguageOptionsScreen;
import net.minecraft.client.gui.widget.ListWidget;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.util.Window;

public class BetterLanguageGui extends ListWidget {

	private final List<String> langCodeList = new ArrayList<>();
	private final Map<String, LanguageDefinition> languageMap = new HashMap<>();

	public BetterLanguageGui(MinecraftClient mc, LanguageOptionsScreen parent) {
		super(mc, parent.width, parent.height, 32, parent.height - 65 + 4, 18);

		for (LanguageDefinition language : MinecraftClient.getInstance().getLanguageManager().getAllLanguages()) {
			this.languageMap.put(language.getCode(), language);
			this.langCodeList.add(language.getCode());
		}
	}

	@Override
	protected int getEntryCount() {
		return this.langCodeList.size();
	}

	@Override
	protected void selectEntry(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
		LanguageDefinition language = this.languageMap.get(this.langCodeList.get(slotIndex));
		client.getLanguageManager().setLanguage(language);
		client.options.language = language.getCode();

		client.getLanguageManager().reload(client.getResourceManager());
		try {
			Class.forName("net.optifine.Lang").getMethod("resourcesReloaded").invoke(null);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException
				| InvocationTargetException error) {
			// OptiFine not installed
		}

		client.textRenderer
				.setUnicode(client.getLanguageManager().forcesUnicodeFont() || client.options.forcesUnicodeFont);
		client.textRenderer.setRightToLeft(client.getLanguageManager().isRightToLeft());

		Window window = new Window(client);
		client.currentScreen.init(client, window.getWidth(), window.getHeight());

		client.options.save();
	}

	@Override
	protected boolean isEntrySelected(int slotIndex) {
		return (this.langCodeList.get(slotIndex)).equals(client.getLanguageManager().getLanguage().getCode());
	}

	@Override
	protected int getMaxPosition() {
		return getEntryCount() * 18;
	}

	@Override
	protected void renderBackground() {
		client.currentScreen.renderBackground();
	}

	@Override
	protected void renderEntry(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn,
			int mouseYIn) {
		client.textRenderer.setRightToLeft(true);
		client.currentScreen.drawCenteredString(client.textRenderer,
				languageMap.get(this.langCodeList.get(entryID)).toString(), this.width / 2, p_180791_3_ + 1, 16777215);
		client.textRenderer.setRightToLeft(client.getLanguageManager().getLanguage().isRightToLeft());
	}

}
