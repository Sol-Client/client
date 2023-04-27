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

package io.github.solclient.client.mod.hud;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

/**
 * Ported here by DarkKronicle from KronHUD by DarkKronicle
 * <a href="https://github.com/DarkKronicle/KronHUD">Github</a>
 *
 * @license GPL-3.0
 */
@AllArgsConstructor
public enum AnchorPoint {

    TOP_LEFT(-1, 1),
    TOP_MIDDLE(0, 1),
    TOP_RIGHT(1, 1),
    MIDDLE_LEFT(-1, 0),
    MIDDLE_MIDDLE(0, 0),
    MIDDLE_RIGHT(1, 0),
    BOTTOM_LEFT(-1, -1),
    BOTTOM_MIDDLE(0, -1),
    BOTTOM_RIGHT(1, -1);

    @Getter
    private final int xComponent;

    @Getter
    private final int yComponent;

    public int getX(int anchorX, int width) {
        switch (xComponent) {
            case 0:
                return anchorX - (width / 2);
            case 1:
                return anchorX - width;
            default:
                return anchorX;
        }
    }

    public int getY(int anchorY, int height) {
        switch (yComponent) {
            case 0:
                return anchorY - (height / 2);
            case 1:
                return anchorY - height;
            default:
                return anchorY;
        }
    }

    public int offsetWidth(int width) {
        switch (xComponent) {
            case 0:
                return width / 2;
            case 1:
                return width;
            default:
                return 0;
        }
    }

    public int offsetHeight(int height) {
        switch (yComponent) {
            case 0:
                return (height / 2);
            case 1:
                return 0;
            default:
                return height;
        }
    }
}
