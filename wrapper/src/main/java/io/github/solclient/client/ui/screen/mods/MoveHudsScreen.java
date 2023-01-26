package io.github.solclient.client.ui.screen.mods;

import org.lwjgl.input.Mouse;

import io.github.solclient.client.Client;
import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.*;

public class MoveHudsScreen extends ComponentScreen {

	private Screen title;
	private HudElement movingHud;
	private Position moveOffset;

	public MoveHudsScreen() {
		super(new MoveHudsComponent());
		background = false;
		if (parentScreen instanceof ComponentScreen) {
			Screen grandparentScreen = ((ComponentScreen) parentScreen).getParentScreen();

			if (grandparentScreen instanceof TitleScreen || grandparentScreen instanceof SolClientMainMenu)
				title = grandparentScreen;
		}
	}

	public HudElement getSelectedHud(int mouseX, int mouseY) {
		for (HudElement hud : Client.INSTANCE.getMods().getHuds()) {
			if (!hud.isVisible())
				continue;

			if (hud.isHovered(mouseX, mouseY))
				return hud;
		}
		return null;
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		if (button == 1) {
			HudElement hud = getSelectedHud(x, y);

			if ((hud != null) && (parentScreen instanceof ModsScreen)) {
				MinecraftUtils.playClickSound(true);

				((ModsScreen) parentScreen).switchMod(hud.getMod());

				MinecraftClient.getInstance().setScreen(parentScreen);
			}
		}
	}

	@Override
	public void init(MinecraftClient mc, int width, int height) {
		super.init(mc, width, height);

		if (title != null)
			title.init(mc, width, height);
	}

	@Override
	public void tick() {
		super.tick();

		if (title != null)
			title.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (title != null)
			title.render(0, 0, tickDelta);

		for (HudElement hud : Client.INSTANCE.getMods().getHuds()) {
			if (!hud.isVisible())
				continue;

			Rectangle bounds = hud.getMultipliedBounds();

			if (client.world == null)
				hud.render(true);

			if (bounds != null)
				bounds.stroke(Component.getTheme().accent);
		}

		HudElement selectedHud = getSelectedHud(mouseX, mouseY);
		if (Mouse.isButtonDown(0)) {
			if (movingHud == null) {
				if (selectedHud != null) {
					movingHud = selectedHud;
					moveOffset = new Position(selectedHud.getPosition().getX() - mouseX,
							selectedHud.getPosition().getY() - mouseY);
				}
			} else
				movingHud.setPosition(new Position(mouseX + moveOffset.getX(), mouseY + moveOffset.getY()));
		} else
			movingHud = null;

		super.render(mouseX, mouseY, tickDelta);
	}

	@Override
	protected void keyPressed(char character, int code) {
		if (code == 1 || (code == SolClientConfig.instance.editHudKey.getCode()
				&& KeyBindingExtension.from(SolClientConfig.instance.editHudKey).areModsPressed())) {
			Client.INSTANCE.save();
			if (title != null) {
				client.setScreen(title);
			} else {
				client.setScreen(null);
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
