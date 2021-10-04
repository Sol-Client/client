package me.mcblueparrot.client.mod;

public class ArabicNumeralsMod extends Mod {

    public static boolean enabled;

    public ArabicNumeralsMod() {
        super("Arabic Numerals", "arabic_numerals", "Convert Roman Numerals (IX) to Arabic Numerals (9).", ModCategory.UTILITY);
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
