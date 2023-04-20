package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMode;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TieredUpgrade extends TeamUpgrade {

    private final int[] doublesPrice;
    private final int[] foursPrice;
    @Getter
    private int level = 0;

    public TieredUpgrade(String name, Pattern regex, int[] foursPrice, int[] doublesPrice) {
        super(name, regex);
        this.foursPrice = foursPrice;
        this.doublesPrice = doublesPrice;
    }

    @Override
    public String[] getTexture() {
        return new String[]{name + "_" + level};
    }

    @Override
    public boolean isPurchased() {
        return level > 0;
    }

    @Override
    protected void onMatch(TeamUpgrade upgrade, Matcher matcher) {
        level += 1;
    }

    public boolean isMaxedOut(BedwarsMode mode) {
        if (mode.getTeams().length == 8) {
            return level >= doublesPrice.length;
        }
        return level >= foursPrice.length;
    }

    @Override
    public int getPrice(BedwarsMode mode) {
        if (mode.getTeams().length == 8) {
            return doublesPrice[level];
        }
        return foursPrice[level];
    }
}
