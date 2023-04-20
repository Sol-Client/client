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
