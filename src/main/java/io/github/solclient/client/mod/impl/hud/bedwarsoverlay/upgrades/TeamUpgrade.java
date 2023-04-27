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

