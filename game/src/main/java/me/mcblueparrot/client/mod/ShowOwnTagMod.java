package me.mcblueparrot.client.mod;

public class ShowOwnTagMod extends Mod {

    public static boolean enabled;

    public ShowOwnTagMod() {
        super("Show own Tag", "arabic_numerals", "Show your own nametag.", ModCategory.UTILITY);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        enabled = true;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        enabled = false;
    }

}
