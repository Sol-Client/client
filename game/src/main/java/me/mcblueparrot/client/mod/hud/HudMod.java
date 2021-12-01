package me.mcblueparrot.client.mod.hud;

import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.util.data.Position;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.FontRenderer;

/**
 * Represents a mod with only a single HUD.
 */
public abstract class HudMod extends Mod {

	/**
	 * Represents the single element that this mod contains.
	 */
	private HudModElement element = new HudModElement();

	@Expose
	private HudPosition position;
	@Expose
	@ConfigOption(value = "Scale", priority = 1)
	@Slider(min = 50, max = 150, step = 1, suffix = "%")
	public float scale = 100;
	protected FontRenderer font;

	public HudMod(String name, String id, String description) {
		super(name, id, description, ModCategory.HUD);
		this.position = getDefaultPosition();
	}

	@Override
	public void postStart() {
		super.postStart();
		this.font = mc.fontRendererObj;
	}

	protected float getScale() {
		return scale / 100;
	}

	@Override
	public List<HudElement> getHudElements() {
		return Arrays.asList(element);
	}

	public void setPosition(Position position) {
		element.setPosition(position);
	}

	public boolean isVisible() {
		return true;
	}

	public Rectangle getBounds(Position position) {
		return null;
	}

	@Override
	public void render(boolean editMode) {
		element.render(editMode);
	}

	public void render(Position position, boolean editMode) {}

	public boolean isShownInReplay() {
		return false;
	}

	public HudPosition getDefaultPosition() {
		return new HudPosition(0, 0);
	}

	class HudModElement extends BaseHudElement {

		@Override
		public boolean isEnabled() {
			return HudMod.this.isEnabled();
		}

		@Override
		public Mod getMod() {
			return HudMod.this;
		}

		@Override
		public HudPosition getHudPosition() {
			return position;
		}

		@Override
		public void setHudPosition(HudPosition position) {
			HudMod.this.position = position;
		}

		@Override
		public float getHudScale() {
			return scale / 100F;
		}

		@Override
		public boolean isVisible() {
			return HudMod.this.isVisible();
		}

		@Override
		public void render(Position position, boolean editMode) {
			HudMod.this.render(position, editMode);
		}

		@Override
		public boolean isShownInReplay() {
			return HudMod.this.isShownInReplay();
		}

		@Override
		public Rectangle getBounds(Position position) {
			return HudMod.this.getBounds(position);
		}

	}

}

