package io.github.solclient.client.mod.impl.hud;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.mod.annotation.*;
import io.github.solclient.client.mod.hud.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.effect.*;
import net.minecraft.util.Identifier;

public class PotionEffectsMod extends SolClientHudMod {

	@Expose
	@Option
	private VerticalAlignment alignment = VerticalAlignment.MIDDLE;
	@Expose
	@Option
	private boolean icon = true;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean background = false;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@Option
	private boolean title = true;
	@Expose
	@Option(applyToAllClass = Option.TEXT_COLOUR_CLASS)
	private Colour titleColour = Colour.WHITE;
	@Expose
	@Option
	private boolean duration = true;
	@Expose
	@Option
	private Colour durationColour = new Colour(8355711);
	@Expose
	@Option
	@Slider(min = 2, max = 25, step = 1)
	private float spacing = 15;

	@Override
	public String getId() {
		return "potion_effects";
	}

	@Override
	public Rectangle getBounds(Position position) {
		int y = position.getY();

		switch (alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= getHeight(2) / 2 * getScale();
				break;
			case BOTTOM:
				y -= getHeight(2) * getScale();
				break;
		}

		return new Rectangle(position.getX(), y, getWidth(), getHeight(2) + 12 + (background ? 2 : 0));
	}

	private int getHeight(int size) {
		return (int) (getEffectHeight() * size - spacing);
	}

	private int getEffectHeight() {
		return (int) (18 + spacing);
	}

	@Override
	public void render(Position position, boolean editMode) {
		int x = position.getX();
		int y = position.getY();
		Collection<StatusEffectInstance> effects;

		if (editMode || mc.player == null)
			effects = Arrays.asList(new StatusEffectInstance(1, 0), new StatusEffectInstance(5, 0));
		else {
			GlStateManager.enableBlend();
			effects = mc.player.getStatusEffectInstances();
		}

		switch (alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= (getHeight(effects.size()) / 2);
				break;
			case BOTTOM:
				y -= getHeight(effects.size());
		}

		if (!effects.isEmpty()) {
			GlStateManager.disableLighting();

			for (StatusEffectInstance effect : effects) {
				StatusEffect effectType = StatusEffect.STATUS_EFFECTS[effect.getEffectId()];
				GlStateManager.color(1, 1, 1, 1);
				mc.getTextureManager().bindTexture(new Identifier("textures/gui/container/inventory.png"));

				int width = getWidth();
				int iconX = x + 6;
				int textX = x + 28;

				if (!title && !duration) {
					iconX++;
				}

				if (!icon) {
					textX -= 18;
				}

				if (background) {
					MinecraftUtils.drawTexture(x, y, 0, 166, width / 2, 32, 0);
					MinecraftUtils.drawTexture(x + width / 2, y, 120 - width / 2, 166, width / 2, 32, 0);
				}

				int centreText = y + 12;

				if (icon && effectType.hasIcon()) {
					int icon = effectType.getIconLevel();
					MinecraftUtils.drawTexture(iconX, y + 7, icon % 8 * 18, 198 + icon / 8 * 18, 18, 18, 0);
				}

				if (title) {
					String titleText = I18n.translate(effectType.getTranslationKey());

					if (effect.getAmplifier() > 0 && effect.getAmplifier() < 4) {
						if (TweaksMod.enabled && TweaksMod.instance.arabicNumerals) {
							titleText += " " + (effect.getAmplifier() + 1);
						} else {
							titleText += " " + I18n.translate("enchantment.level." + (effect.getAmplifier() + 1));
						}
					}

					font.draw(titleText, textX, duration ? y + 7 : centreText, titleColour.getValue(), shadow);
				}

				if (duration) {
					String duration = StatusEffect.getFormattedDuration(effect);
					font.draw(duration, textX, title ? y + 17 : centreText, durationColour.getValue(), shadow);
				}

				y += getEffectHeight();
			}
		}
	}

	private int getWidth() {
		int base = 0;

		if (!icon) {
			base = -18;
		}

		if (!title) {
			if (!duration) {
				return base + 32;
			}

			return base + 56;
		}

		return base + 140;
	}

}
