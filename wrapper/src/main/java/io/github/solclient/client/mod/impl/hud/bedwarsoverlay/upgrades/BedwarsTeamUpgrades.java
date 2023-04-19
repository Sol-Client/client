package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;


import java.util.regex.Pattern;

public class BedwarsTeamUpgrades {

    public final TrapUpgrade trap = new TrapUpgrade();

    public final TeamUpgrade sharpness = new BinaryUpgrade(
            "sharp", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Sharpened Swords"),
            8, 4
    );

    public final TeamUpgrade dragonBuff = new BinaryUpgrade(
            "dragonbuff", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Dragon Buff\\s*$"),
            5, 5
    );

    public final TeamUpgrade healPool = new BinaryUpgrade(
            "healpool", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Heal Pool\\s*$"),
            3, 1
    );

    public final TeamUpgrade protection = new TieredUpgrade(
            "prot", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Reinforced Armor .{1,2}\\s*$"),
            new int[]{5, 10, 20, 30}, new int[]{2, 4, 8, 16}
    );

    public final TeamUpgrade maniacMiner = new TieredUpgrade(
            "haste", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased Maniac Miner .{1,2}\\s*$"),
            new int[]{2, 4}, new int[]{4, 6}
    );

    public final TeamUpgrade forge = new TieredUpgrade(
            "forge", Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (?:Iron|Golden|Emerald|Molten) Forge\\s*$"),
            new int[]{2, 4}, new int[]{4, 6}
    );

    public final TeamUpgrade[] upgrades = {trap, sharpness, dragonBuff, healPool, protection, maniacMiner, forge};

    public BedwarsTeamUpgrades() {

    }

    public void onMessage(String rawMessage) {
        for (TeamUpgrade upgrade : upgrades) {
            if (upgrade.match(rawMessage)) {
                return;
            }
        }
    }

}
