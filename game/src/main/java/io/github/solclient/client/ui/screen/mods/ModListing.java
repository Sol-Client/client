package io.github.solclient.client.ui.screen.mods;

import java.net.URI;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ModListing extends ColouredComponent {

	@Getter
	private final Mod mod;
	private final ModsScreenComponent screen;
	private final Component settingsButton;
	private final ScaledIconComponent pinButton;
	private final boolean pinnedCategory;
	private Position dragStart;
	private boolean dragging;

	public ModListing(Mod mod, ModsScreenComponent screen, boolean pinnedCategory) {
		super(new AnimatedColourController((component, defaultColour) -> {
			if (mod.isEnabled()) {
				return component.isHovered() ? SolClientMod.instance.uiHover : SolClientMod.instance.uiColour;
			} else if (mod.isBlocked()) {
				return component.isHovered() ? Colour.RED_HOVER : Colour.PURE_RED;
			}

			return component.isHovered() ? Colour.DISABLED_MOD_HOVER : Colour.DISABLED_MOD;
		}));

		this.mod = mod;
		this.screen = screen;

		add(new ScaledIconComponent("sol_client_" + mod.getId(), 16, 16),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		add(settingsButton = new ScaledIconComponent(
				(component, defaultIcon) -> mod.isBlocked() ? "sol_client_lock" : "sol_client_settings", 16, 16,
				new AnimatedColourController((component,
						defaultColour) -> isHovered() ? (component.isHovered() || mod.isLocked() || mod.isBlocked()
								? Colour.LIGHT_BUTTON_HOVER
								: Colour.LIGHT_BUTTON) : Colour.TRANSPARENT)),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(
								getBounds().getWidth() - defaultBounds.getWidth() - defaultBounds.getY(),
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight())));

		Component name;
		add(name = new LabelComponent(
				(component, defaultText) -> mod.getName() + (mod.isBlocked() ? " (blocked)" : "")),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 30,
								defaultBounds.getY() - (font.getHeight() / 2)
										- (SolClientMod.instance.fancyFont ? 0 : 1),
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		add(new LabelComponent((component, defaultText) -> mod.getDescription(),
				(component, defaultColour) -> new Colour(160, 160, 160)),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 30,
								defaultBounds.getY() + (font.getHeight() / 2)
										+ (SolClientMod.instance.fancyFont ? 0 : 1),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		Component credit;
		add(credit = new LabelComponent((component, defaultText) -> mod.getCredit(),
				(component, defaultColour) -> new Colour(120, 120, 120)),
				new AlignedBoundsController(Alignment.START, Alignment.START,
						(component, defaultBounds) -> defaultBounds.offset(name.getBounds().getEndX(), 5)));

		Controller<Rectangle> favouriteBounds = new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
				(component, defaultBounds) -> new Rectangle(credit.getBounds().getEndX() + 2, defaultBounds.getY() + 5,
						defaultBounds.getWidth(), defaultBounds.getHeight()));

		add(pinButton = new ScaledIconComponent((component, defaultIcon) -> "sol_client_favourite", 8, 8,
				new AnimatedColourController((component, defaultColour) -> isHovered() || mod.isPinned()
						? (component.isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)
						: Colour.TRANSPARENT)),
				favouriteBounds);

		add(new ScaledIconComponent((component, defaultIcon) -> "sol_client_favourited", 8, 8,
				new AnimatedColourController((component, defaultColour) -> mod.isPinned()
						? (component.isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)
						: Colour.TRANSPARENT)),
				favouriteBounds);

		this.pinnedCategory = pinnedCategory;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();

		if (SolClientMod.instance.roundedUI) {
			Colour.BLACK_128.bind();
			mc.getTextureManager().bindTexture(
					new ResourceLocation("textures/gui/sol_client_mod_listing_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 300, 30, 300, 30);

			getColour().bind();
			mc.getTextureManager().bindTexture(new ResourceLocation(
					"textures/gui/sol_client_mod_listing_outline_" + Utils.getTextureScale() + ".png"));
			Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 300, 30, 300, 30);
		} else {
			Utils.drawRectangle(getRelativeBounds(), Colour.BLACK_128);
			Utils.drawOutline(getRelativeBounds(), getColour());
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
		return new Rectangle(0, 0, 300, 30);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button != 0 && button != 1) {
			return false;
		}

		if (!mod.isBlocked() && (settingsButton.isHovered() || button == 1)) {
			Utils.playClickSound(true);
			screen.switchMod(mod);
			return true;
		}

		if (button == 0) {
			if (pinButton.isHovered()) {
				Utils.playClickSound(true);
				if (!mod.isPinned()) {
					screen.getScroll().notifyAddPin(mod);
				} else {
					screen.getScroll().notifyRemovePin(mod);
				}
				mod.togglePin();
				return true;
			}

			if (pinnedCategory && Client.INSTANCE.getPins().getMods().size() > 1) {
				dragStart = new Position(info.getRelativeMouseX(), info.getRelativeMouseY());
				return true;
			}

			Utils.playClickSound(true);
			primaryFunction();
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (dragStart != null && button == 0) {
			if (!dragging) {
				Utils.playClickSound(true);
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
			if (Client.INSTANCE.detectedServer == null) {
				return;
			}

			URI blockedModPage = Client.INSTANCE.detectedServer.getBlockedModPage();
			if (blockedModPage != null) {
				Utils.openUrl(blockedModPage.toString());
			}
		} else if (mod.isLocked()) {
			screen.switchMod(mod);
		} else {
			mod.toggle();
		}
	}

}
