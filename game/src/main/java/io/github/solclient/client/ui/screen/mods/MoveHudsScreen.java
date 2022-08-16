package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.platform.mc.screen.TitleScreen;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.Screen;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;

public class MoveHudsScreen extends Screen {

	private io.github.solclient.client.platform.mc.screen.Screen title;
	private HudElement movingHud;
	private Position moveOffset;

	public MoveHudsScreen() {
		super(Text.translation("sol_client.hud.edit"), new MoveHudsComponent());
		background = false;
		if(parentScreen instanceof Screen) {
			io.github.solclient.client.platform.mc.screen.Screen grandparentScreen = ((Screen) parentScreen).getParentScreen();

			if(grandparentScreen instanceof TitleScreen || grandparentScreen instanceof SolClientMainMenu) {
				title = grandparentScreen;
			}
		}
	}

	public HudElement getSelectedHud(int mouseX, int mouseY) {
		for(HudElement hud : Client.INSTANCE.getHuds()) {
			if(!hud.isVisible()) continue;

			if(hud.isSelected(mouseX, mouseY)) {
				return hud;
			}
		}
		return null;
	}

	@Override
	public void mouseDown(int x, int y, int button) {
		super.mouseDown(x, y, button);
		if(button == 1) {
			HudElement hud = getSelectedHud(x, y);

			if(hud != null) {
				if (parentScreen instanceof ModsScreen) {
					Utils.playClickSound(true);
					((ModsScreen) parentScreen).switchMod(hud.getMod());
					mc.setScreen(parentScreen);
				}
			}
		}
	}



	@Override
	public void update(MinecraftClient mc, int width, int height) {
		super.update(mc, width, height);

		if(title != null) title.update(mc, width, height);
	}

	@Override
	public void tickScreen() {
		super.tickScreen();

		if(title != null) title.tickScreen();
	}

	@Override
	public void renderScreen(int x, int y, float tickDelta) {
		if(title != null) {
			title.renderScreen(0, 0, tickDelta);
		}

		for(HudElement hud : Client.INSTANCE.getHuds()) {
			if(!hud.isVisible()) continue;

			Rectangle bounds = hud.getMultipliedBounds();

			if(!mc.hasLevel()) {
				hud.render(true);
			}

			if(bounds != null) {
				bounds.stroke(SolClientConfig.instance.uiColour);
			}
		}

		HudElement selectedHud = getSelectedHud(x, y);
		if(Input.isMouseButtonHeld(0)) {
			if(movingHud == null) {
				if(selectedHud != null) {
					movingHud = selectedHud;
					moveOffset = new Position(selectedHud.getPosition().getX() - x,
							selectedHud.getPosition().getY() - y);
				}
			}
			else {
				movingHud.setPosition(new Position(x + moveOffset.getX(), y + moveOffset.getY()));
			}
		}
		else {
			movingHud = null;
		}

		super.renderScreen(x, y, tickDelta);
	}

	@Override
	public void keyDown(char character, int key) {
		if(key == 1 || key == SolClientConfig.instance.editHudKey.getKeyCode()) {
			Client.INSTANCE.save();
			if(title != null) {
				mc.setScreen(title);
			}
			else {
				mc.closeScreen();
			}
		}
	}

	public static class MoveHudsComponent extends Component {

		public MoveHudsComponent() {
			add(ButtonComponent.done(() -> screen.onClose()),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), defaultBounds.getY() - 30,
									defaultBounds.getWidth(), defaultBounds.getHeight())));
		}

	}

}
