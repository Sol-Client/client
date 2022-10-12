package io.github.solclient.client.ui.screen.mods;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;

public class ModsScreen extends PanoramaBackgroundScreen {

	private ModsScreenComponent component;

	public ModsScreen() {
		this(null);
	}

	public ModsScreen(Mod mod) {
		super(Text.translation("sol_client.mod.screen.title"), new ModsScreenComponent(mod));

		component = (ModsScreenComponent) root;
		background = false;
	}

	@Override
	public void renderScreen(int x, int y, float tickDelta) {
		if(!mc.hasLevel()) {
			if(SolClientConfig.INSTANCE.fancyMainMenu) {
				background = false;
				renderPanorama(x, y, tickDelta);
			}
			else {
				background = true;
			}
		}
		else {
			renderDefaultBackground();
		}

		super.renderScreen(x, y, tickDelta);
	}

	public void switchMod(Mod mod) {
		component.switchMod(mod);
	}

	@Override
	protected void onClose() {
		Client.INSTANCE.save();
	}

	@Override
	public void closeAll() {
		if(!mc.hasLevel() && SolClientConfig.INSTANCE.fancyMainMenu) {
			mc.setScreen(mc.getTitleScreen());
			return;
		}

		super.closeAll();
	}

	public static class ModsScreenComponent extends Component {

		@Getter
		private Mod mod;
		private TextFieldComponent search;
		private ModsScroll scroll;
		private int noModsScroll;
		private boolean singleModMode;

		public ModsScreenComponent(Mod startingMod) {
			if(startingMod != null) {
				singleModMode = true;
			}

			add(new LabelComponent((component, defaultText) -> mod != null ? mod.getName()
					: I18n.translate("sol_client.mod.screen.title")),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), 10,
									defaultBounds.getWidth(), defaultBounds.getHeight())));

			add(ButtonComponent.done(() -> {
				if(mod == null || singleModMode) {
					if(!search.getText().isEmpty()) {
						search.setText("");
						search.setFocused(false);
						scroll.load();
					}
					else {
						screen.close();
						if(screen.getParentScreen() instanceof Screen) {
							((Screen) screen.getParentScreen()).getRoot().setFont(font);
						}
						mc.setScreen(((ModsScreen) screen).parentScreen);
					}
				}
				else {
					switchMod(null);
				}
			}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
					(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - (singleModMode ? 0 : 51),
							getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));

			if(!singleModMode) {
				add(new ButtonComponent("sol_client.hud.edit", new AnimatedColourController((component,
						defaultColour) -> component.isHovered() ? new Colour(255, 165, 65) : new Colour(255, 120, 20)))
								.onClick((info, button) -> {
									if(button == 0) {
										Utils.playClickSound(true);
										mc.setScreen(new MoveHudsScreen());
										return true;
									}

									return false;
								}).withIcon("sol_client_hud"),
						new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
								(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 51,
										getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));
			}

			add(scroll = new ModsScroll(this), (component, defaultBounds) -> new Rectangle(0, 25,
					getBounds().getWidth(), getBounds().getHeight() - 62));

			search = new TextFieldComponent(100, false).autoFlush().onUpdate((value) -> {
				scroll.snapTo(0);
				scroll.load();
				return true;
			}).placeholder("sol_client.mod.screen.search").withIcon("sol_client_search");

			switchMod(startingMod, true);
		}

		public void singleModMode() {
			singleModMode = true;
		}

		public void switchMod(Mod mod) {
			switchMod(mod, false);
		}

		public void switchMod(Mod mod, boolean first) {
			this.mod = mod;
			scroll.load();

			if(mod == null) {
				scroll.snapTo(noModsScroll);
				add(search, (component, defaultBounds) -> new Rectangle(6, 6, defaultBounds.getWidth(),
						defaultBounds.getHeight()));
			}
			else {
				noModsScroll = scroll.getScroll();
				scroll.snapTo(0);
				if(!first) {
					remove(search);
				}
			}
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int code, int scancode, int mods) {
			boolean result = super.keyPressed(info, code, scancode, mods);

			if((screen.getRoot().getDialog() == null && (code == Input.ENTER || code == Input.KP_ENTER))
					&& !scroll.getSubComponents().isEmpty()) {
				Component firstComponent = scroll.getSubComponents().get(0);

				return firstComponent.mouseClickedAnywhere(info, firstComponent instanceof ModListing ? 1 : 0, true,
						false);
			}

			if(result) {
				return true;
			}

			if(mod == null && code == Input.F && (mods & Input.COMMAND_MODIFIER) != 0
					&& (mods & Input.SHIFT_MODIFIER) == 0 && (mods & Input.ALT_MODIFIER) == 0) {
				search.setFocused(true);
				return true;
			}
			else if(mod != null
					&& (code == Input.BACKSPACE || (code == Input.LEFT && (mods & Input.ALT_MODIFIER) != 0
							&& (mods & Input.COMMAND_MODIFIER) == 0 && (mods & Input.SHIFT_MODIFIER) == 0))
					&& screen.getRoot().getDialog() == null) {
				switchMod(null);
				return true;
			}

			if(code == SolClientConfig.INSTANCE.modsKey.getKeyCode()) {
				mc.closeScreen();
				return true;
			}

			return false;
		}

		@Override
		public boolean characterTyped(ComponentRenderInfo info, char character) {
			if(!search.isFocused() && mod == null) {
				search.setFocused(true);
				search.setText("");
			}

			return super.characterTyped(info, character);
		}

		public String getQuery() {
			return search.getText();
		}

	}

}
