package me.mcblueparrot.client.mod;

import java.util.List;
import java.util.stream.Collectors;

import me.mcblueparrot.client.Client;

/**
 * Categories of Sol Client mods.
 */
public enum ModCategory {
    /**
     * HUD widgets.
     */
    HUD("HUD"),
    /**
     * Utility mods.
     */
    UTILITY("Utility"),
    /**
     * Aesthetic/graphical mods.
     */
    VISUAL("Visual");

    private String name;
    private List<Mod> mods;

    private ModCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public List<Mod> getMods() {
        if(mods == null) {
            return mods = Client.INSTANCE.getMods().stream().filter((mod) -> mod.getCategory() == ModCategory.this)
                    .collect(Collectors.toList());
        }
        return mods;
    }

}
