package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum BedwarsDeathType {
    COMBAT("rekt"),
    VOID("yeeted into void"),
    PROJECTILE("shot"),
    FALL("fall"),
    GOLEM("golem moment"),
    SELF_VOID("voided"),
    SELF_UNKNOWN("died"),
    ;

    @Getter
    private final String inner;

}
