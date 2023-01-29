package io.github.solclient.client.ui.screen.mods;

import java.net.URI;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

public class ModEntry extends ColouredComponent {

	@Getter
	private final Mod mod;
	private final Controller<Colour> stripeColour;
	private final ModsScreenComponent screen;
	private final ModSettingsButton settingsButton;
	private final IconComponent pinButton;
	private final boolean pinnedCategory;
	private Position dragStart;
	private boolean dragging;

	public ModEntry(Mod mod, ModsScreenComponent screen, boolean pinnedCategory) {
		super(new AnimatedColourController((component,
				defaultColour) -> ((ModEntry) component).isFullyHovered() ? theme.buttonHover : theme.button));

		this.mod = mod;
		this.screen = screen;

		add(new IconComponent("mod/" + mod.getId(), 16, 16),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY() + 3, defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		Component name = new LabelComponent(
				(component, defaultText) -> I18n.translate(mod.getName()) + (mod.isBlocked() ? " (blocked)" : ""));
		add(name,
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 43,
								(int) (defaultBounds.getY() - (regularFont.getLineHeight(nvg) / 2)) - 1,
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		add(new LabelComponent((component, defaultText) -> I18n.translate(mod.getDescription()),
				(component, defaultColour) -> new Colour(160, 160, 160)).scaled(0.8F),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 33,
								(int) (defaultBounds.getY() + (regularFont.getLineHeight(nvg) / 2)) + 1,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		Component credit = new LabelComponent((component, defaultText) -> {
			String text = mod.getDetail();
			if (text == null)
				text = "";

			return I18n.translate(text);
		}, (component, defaultColour) -> new Colour(120, 120, 120)).scaled(0.8F);
		add(credit, new AlignedBoundsController(Alignment.START, Alignment.START, (component,
				defaultBounds) -> defaultBounds.offset(name.getBounds().getEndX() - 1, name.getBounds().getY() + 2)));

		Controller<Rectangle> pinBounds = new AlignedBoundsController(Alignment.START, Alignment.START,
				(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 33, defaultBounds.getY() + 7,
						defaultBounds.getWidth(), defaultBounds.getHeight()));

		add(pinButton = new IconComponent((component, defaultIcon) -> mod.isPinned() ? "pinned" : "pin", 8, 8,
				new AnimatedColourController((component, defaultColour) -> component.isHovered() ? theme.fgButtonHover
						: mod.isPinned() ? theme.fgButton : new Colour(100, 100, 100))),
				pinBounds);

		add(settingsButton = new ModSettingsButton(), new AlignedBoundsController(Alignment.END, null));

		this.pinnedCategory = pinnedCategory;

		stripeColour = new AnimatedColourController((component, defaultColour) -> {
			if (mod.isEnabled()) {
				return isFullyHovered() ? theme.accentHover : theme.accent;
			} else if (mod.isBlocked()) {
				return isFullyHovered() ? Colour.RED_HOVER : Colour.PURE_RED;
			}

			return isFullyHovered() ? theme.buttonSecondaryHover : theme.buttonSecondary;
		});
	}

	public boolean isFullyHovered() {
		return isHovered() && (mod instanceof ConfigOnlyMod || mod.isBlocked() || !settingsButton.isHovered());
	}

	@Override
	public void render(ComponentRenderInfo info) {
		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, getColour().nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight(), 4);
		NanoVG.nvgFill(nvg);

		if (!(mod instanceof ConfigOnlyMod)) {
			// bar on left
			NanoVG.nvgSave(nvg);
			NanoVG.nvgIntersectScissor(nvg, 0, 0, 3, getBounds().getHeight());
			NanoVG.nvgFillColor(nvg, stripeColour.get(this).nvg());
			NanoVG.nvgFill(nvg);
			NanoVG.nvgRestore(nvg);
		}

		if (dragStart != null && !dragging) {
			dragging = Math.abs(info.getRelativeMouseX() - dragStart.getX()) > 2
					|| Math.abs(info.getRelativeMouseY() - dragStart.getY()) > 2;
			if (dragging) {
				screen.notifyDrag(this, dragStart.getX(), dragStart.getY());
			}
		}

		super.render(info);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 30);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button != 0 && button != 1) {
			return false;
		}

		if (!mod.isBlocked() && (settingsButton.isHovered() || button == 1)) {
			MinecraftUtils.playClickSound(true);
			screen.switchMod(mod);
			return true;
		}

		if (button == 0) {
			if (pinButton.isHovered()) {
				MinecraftUtils.playClickSound(true);
				if (!mod.isPinned()) {
					screen.getScroll().notifyAddPin(mod);
				} else {
					screen.getScroll().notifyRemovePin(mod);
				}
				mod.setPinned(!mod.isPinned());
				return true;
			}

			if (pinnedCategory && Client.INSTANCE.getModUiState().getPins().size() > 1) {
				dragStart = new Position((int) info.getRelativeMouseX(), (int) info.getRelativeMouseY());
				return true;
			}

			MinecraftUtils.playClickSound(true);
			primaryFunction();
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (dragStart != null && button == 0) {
			if (!dragging) {
				MinecraftUtils.playClickSound(true);
				primaryFunction();
			} else {
				screen.notifyDrop(this);
			}

			dragging = false;
			dragStart = null;
			return true;
		}

		return super.mouseReleasedAnywhere(info, button, inside);
	}

	private void primaryFunction() {
		if (mod.isBlocked()) {
			// passive-agressive
			if (DetectedServer.current() == null)
				return;

			URI blockedModPage = DetectedServer.current().getBlockedModPage();
			if (blockedModPage != null) {
				MinecraftUtils.openUrl(blockedModPage.toString());
			}
		} else if (mod instanceof ConfigOnlyMod) {
			screen.switchMod(mod);
		} else {
			mod.setEnabled(!mod.isEnabled());
		}
	}

	private final class ModSettingsButton extends ColouredComponent {

		public ModSettingsButton() {
			super(theme.buttonSecondary());
			add(new IconComponent(mod.isBlocked() ? "lock" : "settings", 16, 16),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
		}

		@Override
		public void render(ComponentRenderInfo info) {
			if (!(mod instanceof ConfigOnlyMod) && !mod.isBlocked()) {
				NanoVG.nvgBeginPath(nvg);
				NanoVG.nvgRoundedRectVarying(nvg, 0, 0, getBounds().getWidth(), getBounds().getHeight(), 0, 3, 3, 0);
				NanoVG.nvgFillColor(nvg, getColour().nvg());
				NanoVG.nvgFill(nvg);
			}

			super.render(info);
		}

		@Override
		protected Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(getParent().getBounds().getHeight(), getParent().getBounds().getHeight());
		}

	}

}
