package me.mcblueparrot.client.mod;

import java.util.List;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import net.minecraft.client.Minecraft;

public abstract class Mod {

    protected Minecraft mc;
    private List<ConfigOption.Cached> options;
    private String name, id, description;
    private boolean blocked;
    @Getter
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

    public void onOptionChange(String key, Object value) {
        if(key.equals("enabled")) {
            setEnabled((boolean) value);
        }
    }

    public List<ConfigOption.Cached> getOptions() {
        if(options == null) {
            return options = ConfigOption.Cached.get(this);
        }

        return options;
    }

    public void setEnabled(boolean enabled) {
        if(blocked) {
            return;
        }
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

    public void toggle() {
        setEnabled(!isEnabled());
    }

    public void disable() {
        setEnabled(false);
    }

    public void enable() {
        setEnabled(true);
    }

}
