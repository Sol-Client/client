package io.github.solclient.client.mod.impl.chathotkeys;

import io.github.solclient.client.mod.option.impl.KeyBindingOption;
import io.github.solclient.client.ui.Theme;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Rectangle;

public class HotkeyComponent extends Component {

	public HotkeyComponent(HotkeysComponent parent, Hotkey hotkey) {
		add(KeyBindingOption.createEditButton(hotkey, 20, false, parent::isConflicting, () -> parent.updateMap()),
				Controller.none());
		add(new TextFieldComponent(0, 100 /* TODO: overflows ;-; */, hovered)
				.withPlaceholder("sol_client.mod.chat_hotkeys.message").autoFlush().onUpdate(value -> {
					hotkey.value = value;
					parent.updateMap();
					return true;
				}).setText(hotkey.value), Controller.of(new Rectangle(50, 0, 155, 20)));
		add(new ButtonComponent("", Theme.button(), Theme.fg()).width(20).withIcon("minus").onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			parent.remove(hotkey);
			return true;
		}), Controller.of(new Rectangle(210, 0, 20, 20)));
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 20);
	}

}
