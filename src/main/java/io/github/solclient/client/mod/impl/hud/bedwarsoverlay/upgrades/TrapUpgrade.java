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

package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades;

import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.BedwarsMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrapUpgrade extends TeamUpgrade {

    private final static Pattern[] REGEX = {
            Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap\\s*$"),
            Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap\\s*$"),
            Pattern.compile("^\\b[A-Za-z0-9_§]{3,16}\\b purchased (.+) Trap\\s*$"),
            Pattern.compile("Trap was set (off)!"),
    };

    private final List<TrapType> traps = new ArrayList<>(3);

    public TrapUpgrade() {
        super("trap", REGEX);
    }

    @Override
    protected void onMatch(TeamUpgrade upgrade, Matcher matcher) {
        if (matcher.group(1).equals("off")) {
            // Trap went off
            traps.remove(0);
            return;
        }
        traps.add(TrapType.getFuzzy(matcher.group(1)));
    }

    public boolean canPurchase() {
        return traps.size() < 3;
    }

    @Override
    public int getPrice(BedwarsMode mode) {
        switch (traps.size()) {
            case 0:
                return 1;
            case 1:
                return 2;
            case 2:
                return 4;
        };
        return 0;
    }

    @Override
    public boolean isPurchased() {
        return traps.size() > 0;
    }

    @Override
    public String[] getTexture() {
        if (traps.size() == 0) {
            return new String[]{"trap/empty"};
        }
        String[] trapTextures = new String[traps.size()];
        for (int i = 0; i < traps.size(); i++) {
            TrapType type = traps.get(i);
            trapTextures[i] = "trap/" + type.getTextureName();
        }
        return trapTextures;
    }

    @Override
    public boolean isMultiUpgrade() {
        return true;
    }

    @AllArgsConstructor
    public enum TrapType {
        ITS_A_TRAP("itsatrap"),
        COUNTER_OFFENSIVE("counteroffensive"),
        ALARM("alarm"),
        MINER_FATIGUE("minerfatigue")
        ;

        @Getter
        private final String textureName;

        public static TrapType getFuzzy(String s) {
            s = s.toLowerCase(Locale.ROOT);
            if (s.contains("miner")) {
                return MINER_FATIGUE;
            }
            if (s.contains("alarm")) {
                return ALARM;
            }
            if (s.contains("counter")) {
                return COUNTER_OFFENSIVE;
            }
            return ITS_A_TRAP;
        }
    }
}
