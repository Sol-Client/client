package io.github.solclient.client.mod.impl.chathotkeys;

import java.util.*;

import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;

final class HotkeysOption extends ModOption<List<Hotkey>> {

	private final ChatHotkeysMod mod;

	public HotkeysOption(ChatHotkeysMod mod) {
		super(mod.getTranslationKey("hotkeys"),
				ModOptionStorage.of((Class<List<Hotkey>>) (Object) Map.class, () -> mod.entries));
		this.mod = mod;
	}

	@Override
	public Component createComponent() {
		return new HotkeysComponent(mod, this);
	}

}
