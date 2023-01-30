package io.github.solclient.client.ui.screen.mods;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.Client;
import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.util.*;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class ModsScreen extends PanoramaBackgroundScreen {

	private final ModsScreenComponent component;

	public ModsScreen() {
		this(null);
	}

	public ModsScreen(Mod mod) {
		super(new Component() {
			{
				add(new ModsScreenComponent(mod), new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
			}
		});

		component = (ModsScreenComponent) root.getSubComponents().get(0);
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
		}

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

	public static class ModsScreenComponent extends BlockComponent {

		@Getter
		private Mod mod;
		private TextFieldComponent search;
		private ButtonComponent back;
		@Getter
		private ModsScroll scroll;
		private int noModsScroll;
		private boolean singleModMode;

		private ModEntry targetDraggingMod;
		private ModEntry draggingMod;
		private boolean drop;
		private ModGhost ghost;
		private int modIndex;
		private int mouseX;
		private int mouseY;
		private int dragX;
		private int dragY;

		public ModsScreenComponent(Mod startingMod) {
			super(theme.bg, 12, 0);

			if (startingMod != null) {
				singleModMode = true;
			}

			add(new LabelComponent((component, defaultText) -> mod != null ? I18n.translate(mod.getName())
					: I18n.translate("sol_client.mod.screen.title")).scaled(1.45F),
					new AlignedBoundsController(Alignment.START, Alignment.START, (component, defaultBounds) -> {
						Rectangle result = new Rectangle(getBaseX(), getBaseX() + 3, defaultBounds.getWidth(),
								defaultBounds.getHeight());
						if (!singleModMode && mod != null)
							result = result.offset(18, 0);

						return result;
					}));

			add(scroll = new ModsScroll(this), (component, defaultBounds) -> {
				int y = 60;
				if (mod != null)
					y -= 15;
				return new Rectangle(0, y, getBounds().getWidth(), getBounds().getHeight() - y);
			});

			ButtonComponent done = ButtonComponent.done(() -> getScreen().close()).width(50);
			add(done, new AlignedBoundsController(Alignment.END, Alignment.START,
					(component, defaultBounds) -> defaultBounds.offset(-getBaseX(), getBaseX())));

			if (!singleModMode) {
				add(new ButtonComponent("sol_client.hud.edit", theme.button(), theme.fg()).onClick((info, button) -> {
					if (button == 0) {
						MinecraftUtils.playClickSound(true);
						mc.setScreen(new MoveHudsScreen());
						return true;
					}

					return false;
				}).withIcon("edit").width(60), (component, bounds) -> bounds
						.offset(done.getBounds().getX() - bounds.getWidth() - 4, getBaseX()));
			}

			search = new TextFieldComponent(0, false).autoFlush().onUpdate((ignored) -> {
				scroll.snapTo(0);
				scroll.load();
				return true;
			}).withPlaceholder("sol_client.mod.screen.search").withIcon("search");
			back = new ButtonComponent("", theme.button(), theme.fg()).width(16).height(16).withIcon("back")
					.onClick((info, button) -> {
						if (button != 0)
							return false;

						MinecraftUtils.playClickSound(true);
						switchMod(null, false);
						return true;
					});

			switchMod(startingMod, true);
		}

		// based on start x for mods
		private int getBaseX() {
			return getBounds().getWidth() / 2 - 230 / 2;
		}

		public void singleModMode() {
			this.singleModMode = true;
		}

		public void switchMod(Mod mod) {
			switchMod(mod, false);
		}

		public void switchMod(Mod mod, boolean first) {
			if (mod == null) {
				scroll.snapTo(noModsScroll);
				if (this.mod != null || first)
					add(0, search, (component, bounds) -> new Rectangle(getBaseX(), 38,
							getBounds().getWidth() - getBaseX() * 2, bounds.getHeight()));
				if (!first)
					remove(back);
			} else {
				noModsScroll = scroll.getScroll();
				scroll.snapTo(0);
				if (!first)
					remove(search);
				if (!singleModMode && this.mod == null)
					add(back, (component, defaultBounds) -> defaultBounds.offset(getBaseX(), getBaseX() + 2));
			}

			this.mod = mod;
			scroll.load();
		}

		@Override
		public void render(ComponentRenderInfo info) {
			super.render(info);

			mouseX = (int) info.getRelativeMouseX();
			mouseY = (int) info.getRelativeMouseY();

			if (targetDraggingMod != null) {
				draggingMod = targetDraggingMod;
				targetDraggingMod = null;
				getScroll().getPinned().remove(draggingMod);
				ghost = new ModGhost();
				getScroll().getPinned().add(modIndex, ghost);
				add(draggingMod, (component, defaultBounds) -> defaultBounds.offset(mouseX - dragX, mouseY - dragY));
			} else if (draggingMod != null) {
				if (drop) {
					drop = false;
					remove(draggingMod);
					getScroll().getPinned().remove(ghost);
					getScroll().getPinned().add(modIndex, draggingMod);

					Client.INSTANCE.getModUiState().reorderPin(draggingMod.getMod(), modIndex - 1);

					draggingMod = null;
				} else {
					int ghostY = ghost.getBounds().getY();
					int mouse = draggingMod.getBounds().getY() - getScroll().getBounds().getY()
							+ getScroll().getScroll();
					getScroll().getPinned().remove(ghost);

					if (mouse > ghostY + 20) {
						modIndex++;
					} else if (mouse < ghostY - 20) {
						modIndex--;
					}

					int max = Client.INSTANCE.getModUiState().getPins().size();
					if (modIndex < 1) {
						modIndex = 1;
					} else if (modIndex > max) {
						modIndex = max;
					}

					getScroll().getPinned().add(modIndex, ghost);
				}
			}
		}

		@Override
		public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
			if (draggingMod != null)
				return false;

			return super.mouseClickedAnywhere(info, button, inside, processed);
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if ((screen.getRoot().getDialog() == null
					&& (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER))
					&& (mod != null || !search.getText().isEmpty()) && !scroll.getSubComponents().isEmpty()) {
				Component firstComponent = scroll.getSubComponents().get(0);
				if (mod != null)
					firstComponent = firstComponent.getSubComponents().get(1);
				return firstComponent.mouseClickedAnywhere(info, firstComponent instanceof ModEntry ? 1 : 0, true,
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

		void notifyDrag(ModEntry listing, int xOffset, int yOffset) {
			targetDraggingMod = listing;
			modIndex = getScroll().getPinned().getSubComponents().indexOf(listing);
			this.dragX = xOffset;
			this.dragY = yOffset;
		}

		void notifyDrop(ModEntry listing) {
			drop = true;
		}

		@Override
		public Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(256, 290);
		}

	}

}
