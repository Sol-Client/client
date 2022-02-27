package me.mcblueparrot.client.ui.screen.mods;

import java.io.IOException;

import lombok.Getter;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.controller.AnimatedColourController;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.component.impl.LabelComponent;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.GuiMainMenu;

public class ModsScreen extends Screen {

	private ModsScreenComponent component;

	public ModsScreen() {
		this(null);
	}

	public ModsScreen(Mod mod) {
		super(new ModsScreenComponent(mod));

		component = (ModsScreenComponent) root;
	}

	public void switchMod(Mod mod) {
		component.switchMod(mod);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Client.INSTANCE.save();
	}

	public static class ModsScreenComponent extends Component {

		@Getter
		private Mod mod;
		private ModsScroll scroll;
		private int noModsScroll;
		private boolean singleModMode;

		public ModsScreenComponent(Mod startingMod) {
			if(startingMod != null) {
				singleModMode = true;
			}

			add(new LabelComponent((component, defaultText) -> mod != null ? mod.getName() : "Mods"),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), 10, defaultBounds.getWidth(),
									defaultBounds.getHeight())));

			add(ButtonComponent.done(() -> {
				if(mod == null || singleModMode) {
					getScreen().close();
				}
				else {
					switchMod(null);
				}
			}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
					(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - (singleModMode ? 0 : 51),
							getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));

			if(!singleModMode) {
				add(new ButtonComponent("Edit HUD",
						new AnimatedColourController(
								(component, defaultColour) -> component.isHovered() ? new Colour(255, 165, 65)
										: new Colour(255, 120, 20))).onClick((info, button) -> {
											if (button == 0) {
												Utils.playClickSound(true);
												mc.displayGuiScreen(new MoveHudsScreen(getScreen(), getScreen().getParentScreen() instanceof GuiMainMenu ? (GuiMainMenu) getScreen().getParentScreen() : null));
												return true;
											}
											return false;
										}).withIcon("sol_client_hud"),
						new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
								(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 51,
								getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));
			}

			add(scroll = new ModsScroll(this), (component, defaultBounds) -> new Rectangle(0, 25, getBounds().getWidth(), getBounds().getHeight() - 62));

			switchMod(mod);
		}

		public void singleModMode() {
			this.singleModMode = true;
		}

		public void switchMod(Mod mod) {
			this.mod = mod;
			scroll.load();
			if(mod == null) {
				scroll.snapTo(noModsScroll);
			}
			else {
				noModsScroll = scroll.getScroll();
				scroll.snapTo(0);
			}
		}

	}

}
