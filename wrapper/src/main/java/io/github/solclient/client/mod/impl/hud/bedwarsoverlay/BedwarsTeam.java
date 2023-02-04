package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;


@AllArgsConstructor
public enum BedwarsTeam {
    RED('c', 'R'),
    BLUE('9', 'B'),
    GREEN('a', 'G'),
    YELLOW('e', 'Y'),
    AQUA('b', 'A'),
    WHITE('f', 'W'),
    PINK('d', 'P'),
    GRAY('8', 'S'),
    ;

    @Getter
    private final char code;

    @Getter
    private final char prefix;

    public String getColorSection() {
        return "§" + code;
    }

    public static Optional<BedwarsTeam> fromPrefix(char prefix) {
        for (BedwarsTeam t : values()) {
            if (t.getPrefix() == prefix) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

    public static Optional<BedwarsTeam> fromName(String name) {
        for (BedwarsTeam t : values()) {
            if (name.equalsIgnoreCase(t.name())) {
                return Optional.of(t);
            }
        }
        return Optional.empty();
    }

}
