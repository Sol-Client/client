/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod;

import java.lang.reflect.Field;
import java.util.*;

import org.apache.logging.log4j.*;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.event.*;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.mod.option.impl.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.ui.screen.mods.MoveHudsScreen;
import lombok.*;
import net.minecraft.client.MinecraftClient;

@AbstractTranslationKey("sol_client.mod.generic")
public abstract class Mod extends Object {

	@Getter
	private final Logger logger = LogManager.getLogger();

	@Getter
	private ModInfo info = ModInfo.inject;

	@Getter
	private List<ModOption<?>> options;
	private boolean blocked;

	@Expose
	private boolean enabled = isEnabledByDefault();

	@Getter
	private boolean pinned;

	/**
	 * Called when the mod is registered.
	 */
	public void init() {
		options = createOptions();

		if (this.enabled)
			tryEnable();
	}

	/**
	 * This is called on initialisation to create and populate the option list. Do
	 * not call this yourself - only as a super call!
	 *
	 * @return the list. will be mutable.
	 */
	protected List<ModOption<?>> createOptions() {
		if (options != null)
			logger.warn("Please use getOptions instead of recreating them", new Exception("options != null"));

		List<ModOption<?>> options = new ArrayList<>();

		if (!isForcedOn()) {
			options.add(new ToggleOption("sol_client.mod.generic.enabled",
					ModOptionStorage.of(boolean.class, () -> enabled, (value) -> {
						if (enabled != value)
							setEnabled(value);
					})));
		}

		try {
			FieldOptions.visit(this, options::add);
		} catch (IllegalAccessException error) {
			throw new AssertionError(error);
		}
		return options;
	}

	/**
	 * Gets a field from the mod. Override this if you want to protect access.
	 *
	 * @param name the field name.
	 * @return a storage object for the field.
	 * @throws NoSuchFieldException   if the field doesn't exist.
	 * @throws IllegalAccessException if the access failed.
	 */
	public FieldStorage<?> getField(String name) throws NoSuchFieldException, IllegalAccessException {
		Field field;
		try {
			field = getClass().getDeclaredField(name);
		} catch (NoSuchFieldException error) {
			try {
				field = getClass().getField(name);
			} catch (NoSuchFieldException | SecurityException e) {
				throw error;
			}
		} catch (SecurityException error) {
			throw error;
		}
		field.setAccessible(true);
		return new FieldStorage<>(this, field);
	}

	/**
	 * Gets all options filtered to a type.
	 *
	 * @param <T>  the type.
	 * @param type the type class.
	 * @return the options.
	 */
	public <T> Iterable<ModOption<T>> getOptions(Class<T> type) {
		return new Iterable<ModOption<T>>() {

			@Override
			public Iterator<ModOption<T>> iterator() {
				return getOptions().stream().filter((option) -> option.getType() == type)
						.map((option) -> (ModOption<T>) option).iterator();
			}

		};
	};

	/**
	 * Gets all options filtered to a type.
	 *
	 * @param <T>  the type.
	 * @param type the type class.
	 * @return the options.
	 */
	public <T> Iterable<T> getFlatOptions(Class<T> type) {
		return new Iterable<T>() {

			@Override
			public Iterator<T> iterator() {
				return getOptions().stream().filter((option) -> option.getClass() == type).map((option) -> (T) option)
						.iterator();
			}

		};
	};

	/**
	 * Creates the configuration component.
	 *
	 * @return the component.
	 */
	public Component createConfigComponent() {
		ListComponent container = new ScrollListComponent();
		for (ModOption<?> option : options)
			container.add(option.createComponent());
		return container;
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
	public final String getId() {
		return info.getId();
	}

	/**
	 * Choose a string to display on the right side of the mod component.
	 *
	 * @return an additional string.
	 */
	public String getDetail() {
		return null;
	}

	/**
	 * @return a useful category, otherwise general.
	 */
	public final ModCategory getCategory() {
		return info.getCategory();
	}

	/**
	 * Gets whether the mod should be enabled by default.
	 *
	 * @return <code>true</code> to keep the mod on by default.
	 */
	public final boolean isEnabledByDefault() {
		return info.isEnabledByDefault() || isForcedOn();
	}

	public final boolean isForcedOn() {
		return info.isForcedOn();
	}

	/**
	 * Fired before an option is changed.
	 *
	 * @param key   the option key.
	 * @param value the option value.
	 * @return <code>true</code> to proceed.
	 */
	public boolean onOptionChange(String key, Object value) {
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
		if (isForcedOn())
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
		return isForcedOn() || (enabled && !blocked);
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
		EventBus.INSTANCE.register(this);
	}

	protected void onDisable() {
		EventBus.INSTANCE.unregister(this);
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
			ModUiStateManager.INSTANCE.notifyPin(this);
		} else {
			ModUiStateManager.INSTANCE.notifyUnpin(this);
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
