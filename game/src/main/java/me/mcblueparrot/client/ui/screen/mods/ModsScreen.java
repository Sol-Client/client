package me.mcblueparrot.client.ui.screen.mods;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.Screen;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.ui.component.controller.AnimatedController;
import me.mcblueparrot.client.ui.component.impl.ButtonComponent;
import me.mcblueparrot.client.ui.component.impl.LabelComponent;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;

public class ModsScreen extends Screen {

	public ModsScreen(Mod mod) {
		this();
	}

	public ModsScreen() {
		super(new ModsScreenComponent());
	}

	private static class ModsScreenComponent extends Component {

		public ModsScreenComponent() {
			add(new LabelComponent("Mods"),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), 10, defaultBounds.getWidth(),
									defaultBounds.getHeight())));
			add(new ButtonComponent("Done",
					new AnimatedController<Colour>(
							(component, defaultColour) -> component.isHovered() ? new Colour(20, 120, 20)
									: new Colour(0, 100, 0))).onClick((button) -> {
										if (button == 0) {
											Utils.playClickSound();
											getScreen().close();
											return true;
										}
										return false;
									}).withIcon("sol_client_done"),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(),
							getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));
			add(new ModsScroll(), (component, defaultBounds) -> new Rectangle(0, 25, getBounds().getWidth(), getBounds().getHeight() - 62));
		}

	}

}
