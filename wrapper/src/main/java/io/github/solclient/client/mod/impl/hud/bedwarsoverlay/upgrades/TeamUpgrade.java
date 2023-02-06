package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsGame;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TeamUpgrade {
    private final String name;
    private final Pattern[] regex;

    public TeamUpgrade(String name, Pattern pattern) {
        this(name, new Pattern[]{pattern});
    }

    public TeamUpgrade(String name, Pattern[] pattern) {
        this.name = name;
        this.regex = pattern;
    }

    public boolean match(String unformatedMessage) {
        return BedwarsGame.matched(regex, unformatedMessage, matcher -> onMatch(this, matcher));
    }

    protected abstract void onMatch(TeamUpgrade upgrade, Matcher matcher);

    public abstract int getPrice(BedwarsMode mode);


}

