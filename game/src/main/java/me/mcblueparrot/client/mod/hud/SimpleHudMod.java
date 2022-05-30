package me.mcblueparrot.client.mod.hud;

import java.util.Map;
import java.util.WeakHashMap;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.annotation.AbstractTranslationKey;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.util.DirtyMapper;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

/**
 * A simple HUD mod that rendered a simple string.
 */
@AbstractTranslationKey(SimpleHudMod.TRANSLATION_KEY)
public abstract class SimpleHudMod extends HudMod {

	public static final String TRANSLATION_KEY = "sol_client.mod.simple_hud";

	@Expose
	@Option
	protected boolean background = true;
	@Expose
	@Option(applyToAllClass = Option.BACKGROUND_COLOUR_CLASS)
	protected Colour backgroundColour = new Colour(0, 0, 0, 100);
	@Expose
	@Option
	protected boolean border = false;
	@Expose
	@Option(applyToAllClass = Option.BORDER_COLOUR_CLASS)
	protected Colour borderColour = Colour.BLACK;
	@Expose
	@Option(applyToAllClass = Option.TEXT_COLOUR_CLASS)
	protected Colour textColour = Colour.WHITE;
	@Expose
	@Option
	protected boolean shadow = true;
	private DirtyMapper<String, Integer> langWidth = new DirtyMapper<>(() -> mc.getLanguageManager().getCurrentLanguage().getLanguageCode(), (key) -> {
		String translationKey = getTranslationKey() + ".default_width";
		String width = I18n.format(translationKey);

		if(width.equals(translationKey)) {
			return 53;
		}

		return Integer.parseInt(width);
	});

	@Override
	public Rectangle getBounds(Position position) {
		return new Rectangle(position.getX(), position.getY(), getWidth(), 16);
	}

	private int getWidth() {
		return langWidth.get();
	}

	@Override
	public void render(Position position, boolean editMode) {
		String text = getText(editMode);
		if(text != null) {
			if(background) {
				getBounds(position).fill(backgroundColour);
			}
			else {
				if(!text.isEmpty()) {
					text = "[" + text + "]";
				}
			}

			if(border) {
				getBounds(position).stroke(borderColour);
			}
 			font.drawString(text,
					position.getX() + (getBounds(position).getWidth() / 2F) - (font.getStringWidth(text) / 2F),
					position.getY() + 4, textColour.getValue(), shadow);
		}
	}

	public abstract String getText(boolean editMode);

}
