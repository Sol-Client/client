package me.mcblueparrot.client.mod;

import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.GameOverlayElement;
import me.mcblueparrot.client.event.impl.PostGameOverlayRenderEvent;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.hud.HudElement;
import me.mcblueparrot.client.ui.screen.mods.MoveHudsScreen;
import net.minecraft.client.Minecraft;

public abstract class Mod {

	protected Minecraft mc;
	private List<ConfigOptionData> options;
	private String name, id, description;
	private boolean blocked;
	@Expose
	@ConfigOption(value = "Enabled", priority = 2)
	private boolean enabled;
	protected Logger logger;
	public ModCategory category;

	public Mod(String name, String id, String description, ModCategory category) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.category = category;
		mc = Minecraft.getMinecraft();
		logger = LogManager.getLogger();
		enabled = isEnabledByDefault();
	}

	public void postStart() {
	}

	public void onRegister() {
		if(this.enabled) {
			onEnable();
		}
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public ModCategory getCategory() {
		return category;
	}

	public boolean isEnabledByDefault() {
		return false;
	}

	public boolean onOptionChange(String key, Object value) {
		if(key.equals("enabled")) {
			if(isLocked()) {
				return false;
			}
			setEnabled((boolean) value);
		}
		return true;
	}

	public void postOptionChange(String key, Object value) {
	}

	public List<ConfigOptionData> getOptions() {
		if(options == null) {
			return options = ConfigOptionData.get(this);
		}

		return options;
	}

	public void setEnabled(boolean enabled) {
		if(blocked) return;
		if(isLocked()) return;

		if(enabled != this.enabled) {
			if(enabled) {
				onEnable();
			}
			else {
				onDisable();
			}
		}
		this.enabled = enabled;
	}

	protected void onEnable() {
		Client.INSTANCE.bus.register(this);
	}

	protected void onDisable() {
		Client.INSTANCE.bus.unregister(this);
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		if(enabled && !blocked) {
			onDisable();
		}
		blocked = true;
	}

	public void unblock() {
		if(enabled && blocked) {
			onEnable();
		}
		blocked = false;
	}

	public boolean isEnabled() {
		return enabled && !blocked;
	}

	public void toggle() {
		setEnabled(!isEnabled());
	}

	public void disable() {
		setEnabled(false);
	}

	public void enable() {
		setEnabled(true);
	}

	public int getIndex() {
		return Client.INSTANCE.getMods().indexOf(this);
	}

	public List<HudElement> getHudElements() {
		return Collections.emptyList();
	}

	public boolean isLocked() {
		return false;
	}

	public String getLockMessage() {
		return "";
	}

	public void render(boolean editMode) {
		for(HudElement element : getHudElements()) {
			element.render(editMode);
		}
	}

	@EventHandler
	public void onRender(PostGameOverlayRenderEvent event) {
		if(event.type == GameOverlayElement.ALL) {
			render(mc.currentScreen instanceof MoveHudsScreen);
		}
	}

}
