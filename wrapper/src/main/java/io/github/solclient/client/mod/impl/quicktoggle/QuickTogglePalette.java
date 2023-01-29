package io.github.solclient.client.mod.impl.quicktoggle;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.packet.Popup;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentScreen;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.impl.BlockComponent;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import org.lwjgl.input.Keyboard;

public class QuickTogglePalette extends ComponentScreen {

	public QuickTogglePalette(QuickToggleMod mod) {
		super(new Component() {
			{
				add(new QuickTogglePaletteComponent(mod), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
			}
		});
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void removed() {
		super.removed();
		Keyboard.enableRepeatEvents(false);
	}

	public static class QuickTogglePaletteComponent extends BlockComponent {
		@Getter
		private final QuickToggleMod mod;

		public QuickTogglePaletteComponent(QuickToggleMod mod) {
			super(Colour.DISABLED_MOD.add(-15), 12, 0);
			this.mod = mod;
			int y = 50;
			for (Mod modification : Client.INSTANCE.getPins().getMods().subList(0, Client.INSTANCE.getPins().getMods().size())) {
					int thisY = y;
					add(new ButtonComponent(modification.getName(),
									new AnimatedColourController(
											(component, defaultColour) -> modification.isEnabled() ? new Colour(0, 255, 0) : new Colour(255, 0, 0)
									)).onClick((info, button) -> {
								if (button == 0) {
									MinecraftUtils.playClickSound(true);
									if (mod.closeUi) getScreen().close();
									modification.setEnabled(!modification.isEnabled());
									return true;
								}
								return false;
							}).withIcon("sol_client_" + modification.getId()),
							new AlignedBoundsController(Alignment.CENTRE, Alignment.START, (component, defaultBounds) -> defaultBounds.offset(0, thisY)));
					y += 25;
			}
		}

		@Override
		protected Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(200, 200);
		}

	}


}

