package io.github.solclient.client.mod.hud;

import java.util.Arrays;
import java.util.List;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.PrimaryIntegerSettingMod;
import io.github.solclient.client.mod.annotation.AbstractTranslationKey;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import net.minecraft.client.gui.FontRenderer;

/**
 * Represents a mod with only a single HUD.
 */
@AbstractTranslationKey("sol_client.mod.hud")
public abstract class HudMod extends Mod implements PrimaryIntegerSettingMod {

	/**
	 * Represents the single element that this mod contains.
	 */
	protected final HudElement element = new HudModElement();

	@Expose
	private HudPosition position;
	@Expose
	@Option(priority = 1)
	@Slider(min = 50, max = 150, step = 1, format = "sol_client.slider.percent")
	public float scale = 100;
	protected FontRenderer font;

	public HudMod() {
		position = getDefaultPosition();
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.HUD;
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

	@Override
	public void decrement() {
		scale = Math.max(50, scale - 10);
	}

	@Override
	public void increment() {
		scale = Math.min(150, scale + 10);
	}

	class HudModElement extends BaseHudElement {

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
			return isEnabled() && HudMod.this.isVisible();
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

