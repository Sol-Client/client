package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@AllArgsConstructor
public enum BedwarsDeathType {
    COMBAT("rekt", BedwarsMessages.COMBAT_KILL),
    VOID("yeeted into void", BedwarsMessages.VOID_KILL),
    PROJECTILE("shot", BedwarsMessages.PROJECTILE_KILL),
    FALL("fall", BedwarsMessages.FALL_KILL),
    GOLEM("golem moment", BedwarsMessages.GOLEM_KILL),
    SELF_VOID("voided", new Pattern[]{BedwarsMessages.SELF_VOID}),
    SELF_UNKNOWN("died", new Pattern[]{BedwarsMessages.SELF_UNKNOWN}),
    ;

    @Getter
    private final String inner;

    @Getter
    private final Pattern[] patterns;

    public static boolean getDeath(String rawMessage, BedwarsDeathMatch ifPresent) {
        for (BedwarsDeathType type : values()) {
            if (BedwarsMessages.matched(type.getPatterns(), rawMessage, m -> ifPresent.onMatch(type, m))) {
                return true;
            }
        }
        return false;
    }

    public interface BedwarsDeathMatch {

        void onMatch(BedwarsDeathType type, Matcher matcher);

    }
}
