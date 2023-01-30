package io.github.solclient.client.ui.screen.mods;

import java.net.URI;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.*;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.ModsScreen.ModsScreenComponent;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.resource.language.I18n;

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
				return component.isHovered() ? SolClientConfig.instance.uiHover : SolClientConfig.instance.uiColour;
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
				new AnimatedColourController((component, defaultColour) -> isHovered()
						? (component.isHovered() && !(mod instanceof ConfigOnlyMod || mod.isBlocked())
								? Colour.LIGHT_BUTTON_HOVER
								: Colour.LIGHT_BUTTON)
						: Colour.TRANSPARENT)),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(
								getBounds().getWidth() - defaultBounds.getWidth() - defaultBounds.getY(),
								defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight())));

		Component name;
		add(name = new LabelComponent(
				(component, defaultText) -> I18n.translate(mod.getName()) + (mod.isBlocked() ? " (blocked)" : "")),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 30,
								(int) (defaultBounds.getY() - (regularFont.getLineHeight(nvg) / 2)) - 1,
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		add(new LabelComponent((component, defaultText) -> I18n.translate(mod.getDescription()),
				(component, defaultColour) -> new Colour(160, 160, 160)),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 30,
								(int) (defaultBounds.getY() + (regularFont.getLineHeight(nvg) / 2)) + 1,
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		Component credit;
		add(credit = new LabelComponent((component, defaultText) -> {
			String text = mod.getDetail();
			if (text == null)
				text = "";
			
			return I18n.translate(text);
		}, (component, defaultColour) -> new Colour(120, 120, 120)),
				new AlignedBoundsController(Alignment.START, Alignment.START, (component,
						defaultBounds) -> defaultBounds.offset(name.getBounds().getEndX(), name.getBounds().getY())));

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
		float radius = 0;

		if (SolClientConfig.instance.roundedUI)
			radius = 10;

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, Colour.BLACK_128.nvg());
		NanoVG.nvgRoundedRect(nvg, 0, 0, 300, 30, radius + 1);
		NanoVG.nvgFill(nvg);

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgStrokeColor(nvg, getColour().nvg());
		NanoVG.nvgStrokeWidth(nvg, 1);
		NanoVG.nvgRoundedRect(nvg, .5F, .5F, 299, 29, radius);
		NanoVG.nvgStroke(nvg);

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
		return Rectangle.ofDimensions(300, 30);
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

			if (pinnedCategory && Client.INSTANCE.getPins().getMods().size() > 1) {
				dragStart = new Position(info.getRelativeMouseX(), info.getRelativeMouseY());
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

}
