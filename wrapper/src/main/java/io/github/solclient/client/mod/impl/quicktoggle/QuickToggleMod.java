package io.github.solclient.client.mod.impl.quicktoggle;

import com.google.gson.annotations.Expose;
import net.minecraft.client.resource.language.I18n;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;

public class QuickToggleMod extends SolClientMod {

	@Option
	private final KeyBinding menuKey = new KeyBinding(getTranslationKey("key"), Keyboard.KEY_G,
			GlobalConstants.KEY_CATEGORY);

	@Expose
	@Option
	public boolean closeUi = true;

	@Override
	public String getDetail() {
		return I18n.translate("sol_client.mod.screen.by", "ArikSquad");
	}

	@Override
	public String getId() {
		return "quicktoggle";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@EventHandler
	public void onTick(PreTickEvent event) {
		if (menuKey.isPressed()) {
			mc.setScreen(new QuickTogglePalette(this));
		}
	}
}
