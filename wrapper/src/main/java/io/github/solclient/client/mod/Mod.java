package io.github.solclient.client.mod;

import java.nio.file.Path;
import java.util.*;

import org.apache.logging.log4j.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.mod.option.impl.FieldOption;
import io.github.solclient.client.ui.screen.mods.MoveHudsScreen;
import lombok.*;
import net.minecraft.client.MinecraftClient;

@AbstractTranslationKey("sol_client.mod.generic")
public abstract class Mod {

	@Getter
	private final Logger logger = LogManager.getLogger();

	@Getter
	private List<ModOption<?>> options;
	private boolean blocked;

	@Expose
	@Option(priority = 2)
	private boolean enabled = isEnabledByDefault();

	@Getter
	private boolean pinned;

	@Getter
	@Setter
	private int index = -1;

	/**
	 * Called when the mod is registered.
	 */
	public void init() {
		options = FieldOption.getFieldOptionsFromClass(this);

		if (this.enabled)
			tryEnable();
	}

	/**
	 * Called after the game has started.
	 */
	public void lateInit() {
	}

	/**
	 * Gets a translation key from the mod. Usually will return prefix + key.
	 *
	 * @param key the key.
	 * @return the full translation key.
	 */
	public abstract String getTranslationKey(String key);

	/**
	 * Gets the name of the mod.
	 *
	 * @return the name translation key.
	 */
	public String getName() {
		return getTranslationKey("name");
	}

	/**
	 * Gets the description of the mod.
	 *
	 * @return the description translation key.
	 */
	public String getDescription() {
		return getTranslationKey("description");
	}

	/**
	 * @return a unique id.
	 */
	public abstract String getId();

	public abstract Path getConfigFolder();

	/**
	 * Choose a string to display on the right side of the mod component.
	 *
	 * @return an additional credit string.
	 */
	public String getCredit() {
		return null;
	}

	/**
	 * @return a useful category, otherwise general.
	 */
	public abstract ModCategory getCategory();

	/**
	 * Gets whether the mod should be enabled by default.
	 *
	 * @return <code>true</code> to keep the mod on by default.
	 */
	public abstract boolean isEnabledByDefault();

	/**
	 * Fired before an option is changed.
	 *
	 * @param key   the option key.
	 * @param value the option value.
	 * @return <code>true</code> to proceed.
	 */
	public boolean onOptionChange(String key, Object value) {
		if (key.equals("enabled")) {
			if (this instanceof ConfigOnlyMod)
				return false;

			setEnabled((boolean) value);
		}

		return true;
	}

	public void postOptionChange(String key, Object value) {
	}

	/**
	 * Fired when a file is edited.
	 *
	 * @param key the option key.
	 */
	public void onFileUpdate(String key) {
	}

	public final void setEnabled(boolean enabled) {
		if (blocked)
			return;
		if (this instanceof ConfigOnlyMod)
			return;

		if (enabled != this.enabled) {
			if (enabled) {
				tryEnable();
			} else {
				try {
					onDisable();
				} catch (Throwable error) {
					logger.error("Error while disabling mod", error);
				}
			}
		}
		this.enabled = enabled;
	}

	public final boolean isEnabled() {
		if (this instanceof ConfigOnlyMod)
			return true;

		return enabled && !blocked;
	}

	private void tryEnable() {
		try {
			onEnable();
		} catch (Throwable error) {
			logger.error("Could not enable mod", error);
			setEnabled(false);
		}
	}

	protected void onEnable() {
		Client.INSTANCE.getEvents().register(this);
	}

	protected void onDisable() {
		Client.INSTANCE.getEvents().unregister(this);
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		if (enabled && !blocked) {
			onDisable();
		}
		blocked = true;
	}

	public void unblock() {
		if (enabled && blocked) {
			onEnable();
		}
		blocked = false;
	}

	public void setPinned(boolean pinned) {
		if (pinned == this.pinned) {
			return;
		}

		this.pinned = pinned;

		if (pinned) {
			Client.INSTANCE.getPins().notifyPin(this);
		} else {
			Client.INSTANCE.getPins().notifyUnpin(this);
		}
	}

	void notifyUnpin() {
		pinned = false;
	}

	void notifyPin() {
		pinned = true;
	}

	public List<HudElement> getHudElements() {
		return Collections.emptyList();
	}

	public void render(boolean editMode) {
		for (HudElement element : getHudElements())
			element.render(editMode);
	}

	@EventHandler
	public void onRender(PostGameOverlayRenderEvent event) {
		if (event.type == GameOverlayElement.ALL) {
			render(MinecraftClient.getInstance().currentScreen instanceof MoveHudsScreen);
		}
	}

}
