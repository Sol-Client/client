package io.github.solclient.client.ui.screen.mods;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.client.util.extension.KeyBindingExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;

public class MoveHudsScreen extends Screen {

	private GuiScreen title;
	private HudElement movingHud;
	private Position moveOffset;

	public MoveHudsScreen() {
		super(new MoveHudsComponent());
		background = false;
		if (parentScreen instanceof Screen) {
			GuiScreen grandparentScreen = ((Screen) parentScreen).getParentScreen();

			if (grandparentScreen instanceof GuiMainMenu || grandparentScreen instanceof SolClientMainMenu) {
				title = grandparentScreen;
			}
		}
	}

	public HudElement getSelectedHud(int mouseX, int mouseY) {
		for (HudElement hud : Client.INSTANCE.getHuds()) {
			if (!hud.isVisible())
				continue;

			if (hud.isSelected(mouseX, mouseY)) {
				return hud;
			}
		}
		return null;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		if (mouseButton == 1) {
			HudElement hud = getSelectedHud(mouseX, mouseY);

			if (hud != null) {
				if (parentScreen instanceof ModsScreen) {
					Utils.playClickSound(true);

					((ModsScreen) parentScreen).switchMod(hud.getMod());

					Minecraft.getMinecraft().displayGuiScreen(parentScreen);
				}
			}
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);

		if (title != null)
			title.setWorldAndResolution(mc, width, height);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();

		if (title != null)
			title.updateScreen();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (title != null) {
			title.drawScreen(0, 0, partialTicks);
		}

		for (HudElement hud : Client.INSTANCE.getHuds()) {
			if (!hud.isVisible())
				continue;

			Rectangle bounds = hud.getMultipliedBounds();

			if (mc.theWorld == null) {
				hud.render(true);
			}

			if (bounds != null) {
				bounds.stroke(SolClientMod.instance.uiColour);
			}
		}

		HudElement selectedHud = getSelectedHud(mouseX, mouseY);
		if (Mouse.isButtonDown(0)) {
			if (movingHud == null) {
				if (selectedHud != null) {
					movingHud = selectedHud;
					moveOffset = new Position(selectedHud.getPosition().getX() - mouseX,
							selectedHud.getPosition().getY() - mouseY);
				}
			} else {
				movingHud.setPosition(new Position(mouseX + moveOffset.getX(), mouseY + moveOffset.getY()));
			}
		} else {
			movingHud = null;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == 1 || (keyCode == SolClientMod.instance.editHudKey.getKeyCode() && KeyBindingExtension.from(SolClientMod.instance.editHudKey).areModsPressed())) {
			Client.INSTANCE.save();
			if (title != null) {
				mc.displayGuiScreen(title);
			} else {
				mc.displayGuiScreen(null);
			}
		}
	}

	public static class MoveHudsComponent extends Component {

		public MoveHudsComponent() {
			add(ButtonComponent.done(() -> screen.close()),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 30,
									defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

	}

}
