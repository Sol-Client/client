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

package io.github.solclient.client.mod.impl.hud.bedwarsoverlay.stats;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;


public class LobbyStatsHud implements HudElement {

    public LobbyStatsHud() {

    }

    public void update() {

    }

    @Override
    public float getScale() {
        return 0;
    }

    @Override
    public Position getConfiguredPosition() {
        return null;
    }

    @Override
    public void setPosition(Position position) {

    }

    @Override
    public boolean isVisible() {
        return false;
    }

    @Override
    public Rectangle getBounds(Position position) {
        return null;
    }

    @Override
    public void render(Position position, boolean editMode) {

    }

    @Override
    public Mod getMod() {
        return null;
    }

    @Override
    public boolean isShownInReplay() {
        return false;
    }
}
