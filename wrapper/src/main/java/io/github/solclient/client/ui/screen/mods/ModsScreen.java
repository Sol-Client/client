package io.github.solclient.client.ui.screen.mods;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import io.github.solclient.client.util.extension.KeyBindingExtension;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class ModsScreen extends PanoramaBackgroundScreen {

	private ModsScreenComponent component;

	public ModsScreen() {
		this(null);
	}

	public ModsScreen(Mod mod) {
		super(new ModsScreenComponent(mod));

		component = (ModsScreenComponent) root;
		background = false;
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void render(int mouseX, int mouseY, float tickDelta) {
		if (client.world == null) {
			if (SolClientConfig.instance.fancyMainMenu) {
				background = false;
				drawPanorama(mouseX, mouseY, tickDelta);
			} else
				background = true;
		} else
			renderBackground();

		super.render(mouseX, mouseY, tickDelta);
	}

	public void switchMod(Mod mod) {
		component.switchMod(mod);
	}

	@Override
	public void removed() {
		super.removed();
		Client.INSTANCE.save();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void closeAll() {
		if (client.world == null && SolClientConfig.instance.fancyMainMenu) {
			client.setScreen(ActiveMainMenu.getInstance());
			return;
		}

		super.closeAll();
	}

	public static class ModsScreenComponent extends Component {

		@Getter
		private Mod mod;
		private TextFieldComponent search;
		@Getter
		private ModsScroll scroll;
		private int noModsScroll;
		private boolean singleModMode;

		private ModListing targetDraggingMod;
		private ModListing draggingMod;
		private boolean drop;
		private ModGhost ghost;
		private int modIndex;
		private int mouseX;
		private int mouseY;
		private int dragX;
		private int dragY;

		public ModsScreenComponent(Mod startingMod) {
			if (startingMod != null) {
				singleModMode = true;
			}

			add(new LabelComponent((component, defaultText) -> mod != null ? mod.getName()
					: I18n.translate("sol_client.mod.screen.title")),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), 10,
									defaultBounds.getWidth(), defaultBounds.getHeight())));

			add(scroll = new ModsScroll(this), (component, defaultBounds) -> new Rectangle(0, 25,
					getBounds().getWidth(), getBounds().getHeight() - 62));

			add(ButtonComponent.done(() -> {
				if (mod == null || singleModMode) {
					if (!search.getText().isEmpty()) {
						search.setText("");
						search.setFocused(false);
						scroll.load();
					} else {
						getScreen().close();
					}
				} else {
					switchMod(null);
				}
			}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
					(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - (singleModMode ? 0 : 53),
							getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));

			if (!singleModMode) {
				add(new ButtonComponent("sol_client.hud.edit", new AnimatedColourController((component,
						defaultColour) -> component.isHovered() ? new Colour(255, 165, 65) : new Colour(255, 120, 20)))
						.onClick((info, button) -> {
							if (button == 0) {
								Utils.playClickSound(true);
								mc.setScreen(new MoveHudsScreen());
								return true;
							}

							return false;
						}).withIcon("sol_client_hud"),
						new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
								(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 53,
										getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));
			}

			search = new TextFieldComponent(100, false).autoFlush().onUpdate((ignored) -> {
				scroll.snapTo(0);
				scroll.load();
				return true;
			}).withPlaceholder("sol_client.mod.screen.search").withIcon("sol_client_search");

			add(new ScaledIconComponent("sol_client_about", 16, 16,
					new AnimatedColourController((component,
							defaultColour) -> component.isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON))
					.onClick((info, button) -> {
						if (button != 0) {
							return false;
						}

						Utils.playClickSound(true);
						setDialog(new AboutDialog());
						return true;
					}),
					new AlignedBoundsController(Alignment.END, Alignment.START,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - 3,
									defaultBounds.getY() + 3, defaultBounds.getWidth(), defaultBounds.getHeight())));

			switchMod(startingMod, true);
		}

		public void singleModMode() {
			this.singleModMode = true;
		}

		public void switchMod(Mod mod) {
			switchMod(mod, false);
		}

		public void switchMod(Mod mod, boolean first) {
			this.mod = mod;
			scroll.load();

			if (mod == null) {
				scroll.snapTo(noModsScroll);
				add(0, search, (component, defaultBounds) -> new Rectangle(6, 6, defaultBounds.getWidth(),
						defaultBounds.getHeight()));
			} else {
				noModsScroll = scroll.getScroll();
				scroll.snapTo(0);
				if (!first) {
					remove(search);
				}
			}
		}

		@Override
		public void render(ComponentRenderInfo info) {
			super.render(info);

			mouseX = info.getRelativeMouseX();
			mouseY = info.getRelativeMouseY();

			if (targetDraggingMod != null) {
				draggingMod = targetDraggingMod;
				targetDraggingMod = null;
				getScroll().remove(draggingMod);
				ghost = new ModGhost();
				getScroll().add(modIndex, ghost);
				add(draggingMod, (component, defaultBounds) -> defaultBounds.offset(mouseX - dragX, mouseY - dragY));
			} else if (draggingMod != null) {
				if (drop) {
					drop = false;
					remove(draggingMod);
					getScroll().remove(ghost);
					getScroll().add(modIndex, draggingMod);

					Client.INSTANCE.getPins().reorder(draggingMod.getMod(), modIndex - 1);

					draggingMod = null;
				} else {
					int ghostY = ghost.getBounds().getY();
					int mouse = draggingMod.getBounds().getY() - getScroll().getBounds().getY()
							+ getScroll().getScroll();
					getScroll().remove(ghost);

					if (mouse > ghostY + 20) {
						modIndex++;
					} else if (mouse < ghostY - 20) {
						modIndex--;
					}

					int max = Client.INSTANCE.getPins().getMods().size();
					if (modIndex < 1) {
						modIndex = 1;
					} else if (modIndex > max) {
						modIndex = max;
					}

					getScroll().add(modIndex, ghost);
				}
			}
		}

		@Override
		public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
			if (draggingMod != null) {
				return false;
			}

			return super.mouseClickedAnywhere(info, button, inside, processed);
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if ((screen.getRoot().getDialog() == null
					&& (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER))
					&& !scroll.getSubComponents().isEmpty()) {
				Component firstComponent = scroll.getSubComponents().get(0);
				return firstComponent.mouseClickedAnywhere(info, firstComponent instanceof ModListing ? 1 : 0, true,
						false);
			} else if (draggingMod == null && mod == null && keyCode == Keyboard.KEY_F && hasControlDown()
					&& !hasShiftDown() && !hasAltDown()) {
				search.setFocused(true);
				return true;
			}

			if (character > 31 && !search.isFocused() && mod == null && draggingMod == null) {
				search.setFocused(true);
				search.setText("");
			}

			boolean result = super.keyPressed(info, keyCode, character);

			if (!result) {
				if (keyCode == SolClientConfig.instance.modsKey.getCode()
						&& KeyBindingExtension.from(SolClientConfig.instance.modsKey).areModsPressed()) {
					mc.setScreen(null);
					return true;
				} else if (mod != null && (keyCode == Keyboard.KEY_BACK
						|| (keyCode == Keyboard.KEY_LEFT && hasAltDown() && !hasControlDown() && !hasShiftDown()))
						&& screen.getRoot().getDialog() == null) {
					switchMod(null);
					return true;
				}
			}

			return result;
		}

		public String getFilter() {
			return search.getText();
		}

		void notifyDrag(ModListing listing, int xOffset, int yOffset) {
			targetDraggingMod = listing;
			modIndex = getScroll().getSubComponents().indexOf(listing);
			this.dragX = xOffset;
			this.dragY = yOffset;
		}

		void notifyDrop(ModListing listing) {
			drop = true;
		}

	}

}
