package me.mcblueparrot.client.mod.impl.hud;

import java.util.Arrays;
import java.util.Collection;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.HudMod;
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

	private static final int EFFECT_HEIGHT = 33;

	@Expose
	@ConfigOption("Alignment")
	private VerticalAlignment alignment = VerticalAlignment.MIDDLE;
	@Expose
	@ConfigOption("Icon")
	private boolean icon = true;
	@Expose
	@ConfigOption("Background")
	private boolean background = false;
	@Expose
	@ConfigOption("Text Shadow")
	private boolean shadow = true;
	@Expose
	@ConfigOption("Title Colour")
	private Colour titleColour = Colour.WHITE;
	@Expose
	@ConfigOption("Duration Colour")
	private Colour durationColour = new Colour(8355711);

	public PotionEffectsMod() {
		super("Potion Effects", "potion_effects", "Display your potion effects on the HUD.");
	}

	@Override
	public Rectangle getBounds(Position position) {
		int y = position.getY();
		switch(alignment) {
			case TOP:
				break;
			case MIDDLE:
				y -= 33 * getScale();
				break;
			case BOTTOM:
				y -= 66 * getScale();
				break;
		}
		return new Rectangle(position.getX(), y, 120, 64);
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
				y -= ((EFFECT_HEIGHT * (effects.size())) / 2);
				break;
			case BOTTOM:
				y -= EFFECT_HEIGHT * (effects.size());
		}

		if(!effects.isEmpty()) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableLighting();

			for(PotionEffect effect : effects) {
				Potion potion = Potion.potionTypes[effect.getPotionID()];
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/inventory.png"));

				if(background) {
					Utils.drawTexture(x, y, 0, 166, 140, 32, 0);
				}

				if(icon && potion.hasStatusIcon()) {
					int icon = potion.getStatusIconIndex();
					Utils.drawTexture(x + 6, y + 7, icon % 8 * 18, 198 + icon / 8 * 18, 18,
							18, 0);
				}

				String title = I18n.format(potion.getName());

				if(effect.getAmplifier() > 0 && effect.getAmplifier() < 4) {
					if(TweaksMod.enabled && TweaksMod.instance.arabicNumerals) {
						title += " " + (effect.getAmplifier() + 1);
					}
					else {
						title += " " + I18n.format("enchantment.level." + (effect.getAmplifier() + 1));
					}
				}

				font.drawString(title, x + 10 + 18, y + 6, titleColour.getValue(), shadow);
				String duration = Potion.getDurationString(effect);
				font.drawString(duration, x + 10 + 18, y + 6 + 10, durationColour.getValue(), shadow);
				y += EFFECT_HEIGHT;
			}
		}
	}

	public VerticalAlignment getAlignment() {
		return alignment;
	}

}
