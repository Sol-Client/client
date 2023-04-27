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


import net.minecraft.client.MinecraftClient;

import java.util.Locale;

public enum Justification {

    LEFT, CENTER, RIGHT;

    public String getDisplayKey() {
        return "sol_client.justification." + name().toLowerCase(Locale.ROOT);
    }

    public int getXOffset(String string, int width) {
        if (this == LEFT) {
            return 0;
        }
        return getXOffset(MinecraftClient.getInstance().textRenderer.getStringWidth(string), width);
    }

    public int getXOffset(int textWidth, int width) {
        if (this == LEFT) {
            return 0;
        }
        if (this == RIGHT) {
            return width - textWidth;
        }
        return (width - textWidth) / 2;
    }

}
