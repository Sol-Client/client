/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
