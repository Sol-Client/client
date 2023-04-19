package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BinaryUpgrade extends TeamUpgrade {

    private boolean purchased = false;

    private final int foursPrice;
    private final int doublesPrice;

    public BinaryUpgrade(String name, Pattern regex, int foursPrice, int doublesPrice) {
        super(name, regex);
        this.foursPrice = foursPrice;
        this.doublesPrice = doublesPrice;
    }
    @Override
    protected void onMatch(TeamUpgrade upgrade, Matcher matcher) {
        purchased = true;
    }

    @Override
    public String[] getTexture() {
        return new String[]{name + "_" + (purchased ? "1" : "0")};
    }

    @Override
    public boolean isPurchased() {
        return purchased;
    }

    @Override
    public int getPrice(BedwarsMode mode) {
        if (mode.getTeams().length == 8) {
            return doublesPrice;
        }
        return foursPrice;
    }

}
