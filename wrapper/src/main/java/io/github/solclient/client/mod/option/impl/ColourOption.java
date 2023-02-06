package io.github.solclient.client.mod.option.impl;

import java.util.Optional;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ColourPickerDialog;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;

public class ColourOption extends ModOption<Colour> {

	private final Optional<String> applyToAllKey;

	public ColourOption(String name, ModOptionStorage<Colour> storage, Optional<String> applyToAllKey) {
		super(name, storage);
		this.applyToAllKey = applyToAllKey;
	}

	@Override
	public Component createComponent() {
		Component container = createDefaultComponent();

		ColourBoxComponent colour = new ColourBoxComponent(Controller.of(() -> getValue()));
		container.add(colour, new AlignedBoundsController(Alignment.END, Alignment.CENTRE));

		container.add(new LabelComponent(Controller.of(() -> getValue().toHexString())).scaled(0.8F),
				new AlignedBoundsController(Alignment.END, Alignment.CENTRE, (component, defaultBounds) -> {
					return new Rectangle(
							(int) (container.getBounds().getWidth() - NanoVGManager.getRegularFont()
									.getWidth(NanoVGManager.getNvg(), ((LabelComponent) component).getText()) - 12),
							defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
				}));

		colour.onClick((info, button) -> {
			if (button != 0)
				return false;

			MinecraftUtils.playClickSound(true);
			container.getScreen().getRoot().setDialog(new ColourPickerDialog(this, getValue(), this::setValue));
			return true;
		});

		return container;
	}

	public boolean canApplyToAll() {
		return applyToAllKey.isPresent();
	}

	public void applyToAll() {
		for (Mod mod : Client.INSTANCE.getMods()) {
			for (ColourOption option : mod.getFlatOptions(ColourOption.class)) {
				if (!option.applyToAllKey.equals(applyToAllKey))
					continue;
				option.setFrom(this);
			}
		}
	}

}
