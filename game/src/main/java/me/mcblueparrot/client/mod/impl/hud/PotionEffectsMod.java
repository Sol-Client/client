package me.mcblueparrot.client.mod.impl.hud;

import java.util.Arrays;
import java.util.Collection;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.mod.impl.TweaksMod;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.VerticalAlignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class PotionEffectsMod extends HudMod {

	@Expose
	@Option
	private VerticalAlignment alignment = VerticalAlignment.MIDDLE;
	@Expose
	@Option
	private boolean icon = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean background = false;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
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

		switch(alignment) {
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
		Collection<PotionEffect> effects;

		if(editMode || mc.thePlayer == null) {
			effects = Arrays.asList(new PotionEffect(1, 0), new PotionEffect(5, 0));
		}
		else {
			GlStateManager.enableBlend();
			effects = mc.thePlayer.getActivePotionEffects();
		}

		switch(alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= (getHeight(effects.size()) / 2);
				break;
			case BOTTOM:
				y -= getHeight(effects.size());
		}

		if(!effects.isEmpty()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();

			for(PotionEffect effect : effects) {
				Potion potion = Potion.potionTypes[effect.getPotionID()];
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));

				int width = getWidth();
				int iconX = x + 6;
				int textX = x + 28;

				if(!title && !duration) {
					iconX++;
				}

				if(!icon) {
					textX -= 18;
				}

				if(background) {
					Utils.drawTexture(x, y, 0, 166, width / 2, 32, 0);
					Utils.drawTexture(x + width / 2, y, 120 - width / 2, 166, width / 2, 32, 0);
				}

				int centreText = y + 12;

				if(icon && potion.hasStatusIcon()) {
					int icon = potion.getStatusIconIndex();
					Utils.drawTexture(iconX, y + 7, icon % 8 * 18, 198 + icon / 8 * 18, 18,
							18, 0);
				}

				if(title) {
					String titleText = I18n.format(potion.getName());

					if(effect.getAmplifier() > 0 && effect.getAmplifier() < 4) {
						if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals) {
							titleText += " " + (effect.getAmplifier() + 1);
						}
						else {
							titleText += " " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1));
						}
					}

					font.drawString(titleText, textX, duration ? y + 7 : centreText, titleColour.getValue(), shadow);
				}

				if(duration) {
					String duration = Potion.getDurationString(effect);
					font.drawString(duration, textX, title ? y + 17 : centreText, durationColour.getValue(), shadow);
				}

				y += getEffectHeight();
			}
		}
	}

	private int getWidth() {
		int base = 0;

		if(!icon) {
			base = -18;
		}

		if(!title) {
			if(!duration) {
				return base + 32;
			}

			return base + 56;
		}

		return base + 140;
	}

}
