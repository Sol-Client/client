package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import lombok.Getter;


public enum BedwarsMode {
    SOLO(BedwarsTeam.values()),
    DOUBLES(BedwarsTeam.values()),
    THREES(BedwarsTeam.BLUE, BedwarsTeam.GREEN, BedwarsTeam.YELLOW, BedwarsTeam.RED),
    FOURS(BedwarsTeam.BLUE, BedwarsTeam.GREEN, BedwarsTeam.YELLOW, BedwarsTeam.RED),
    FOUR_V_FOUR(BedwarsTeam.BLUE, BedwarsTeam.RED)
    ;

    @Getter
    private final BedwarsTeam[] teams;

    BedwarsMode(BedwarsTeam... teams) {
        this.teams = teams;
    }

}
