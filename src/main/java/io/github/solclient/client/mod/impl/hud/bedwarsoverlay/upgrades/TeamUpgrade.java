package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMessages;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMode;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TeamUpgrade {
    @Getter
    protected final String name;
    protected final Pattern[] regex;

    public TeamUpgrade(String name, Pattern pattern) {
        this(name, new Pattern[]{pattern});
    }

    public TeamUpgrade(String name, Pattern[] pattern) {
        this.name = name;
        this.regex = pattern;
    }

    public boolean match(String unformatedMessage) {
        return BedwarsMessages.matched(regex, unformatedMessage, matcher -> onMatch(this, matcher));
    }

    public abstract String[] getTexture();

    public boolean isMultiUpgrade() {
        // Basically only trap
        return false;
    }

    protected abstract void onMatch(TeamUpgrade upgrade, Matcher matcher);

    public abstract int getPrice(BedwarsMode mode);


    public abstract boolean isPurchased();
}

