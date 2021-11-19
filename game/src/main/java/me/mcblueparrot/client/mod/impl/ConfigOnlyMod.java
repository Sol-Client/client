package me.mcblueparrot.client.mod.impl;

import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;

/**
 * Represents a mod that is cannot be disabled.
 */
public class ConfigOnlyMod extends Mod {

    public ConfigOnlyMod(String name, String id, String description, ModCategory category) {
        super(name, id, description, category);
    }

    @Override
    public boolean isEnabled() {
        return isEnabledByDefault();
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public boolean isLocked() {
        return true;
    }

}
