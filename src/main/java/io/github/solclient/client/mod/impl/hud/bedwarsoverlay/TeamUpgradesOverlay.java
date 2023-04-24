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

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades.BedwarsTeamUpgrades;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades.TeamUpgrade;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades.TrapUpgrade;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.ModOptionStorage;
import io.github.solclient.client.mod.option.annotation.Option;
import io.github.solclient.client.mod.option.impl.FieldOptions;
import io.github.solclient.client.mod.option.impl.SliderOption;
import io.github.solclient.client.mod.option.impl.ToggleOption;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeamUpgradesOverlay implements HudElement {

    private final static String TRANSLATION_KEY = "bedwars.teamupgrades";

    private BedwarsTeamUpgrades upgrades = null;
    private final BedwarsMod mod;
    private final MinecraftClient mc;
    private final static String[] trapEdit = {"trap/minerfatigue", "trap/itsatrap"};

    @Expose
    private boolean enabled = false;

    @Expose
    private Position position = new Position(100, 100);

    @Expose
    private float scale = 100;

    public TeamUpgradesOverlay(BedwarsMod mod) {
        this.mod = mod;
        this.mc = MinecraftClient.getInstance();
    }

    public void onStart(BedwarsTeamUpgrades newUpgrades) {
        upgrades = newUpgrades;
    }

    public void onEnd() {
        upgrades = null;
    }

    @Override
    public float getScale() {
        return scale / 100;
    }

    @Override
    public Position getConfiguredPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public boolean isVisible() {
        return enabled;
    }

    @Override
    public Rectangle getBounds(Position position) {
        return position.rectangle(60, 40);
    }

    public List<ModOption<?>> createOptions() {
        List<ModOption<?>> options = new ArrayList<>();
        options.add(new ToggleOption(
                TRANSLATION_KEY + ".enabled",
                ModOptionStorage.of(boolean.class, () -> enabled, (value) -> {
                    if (enabled != value) {
                        enabled = value;
                    }
                })
        ));
        options.add(
                new SliderOption(TRANSLATION_KEY + ".option.scale",
                        ModOptionStorage.of(Number.class, () -> scale, (value) -> scale = value.floatValue()),
                        Optional.of("sol_client.slider.percent"), 50, 150, 1
                ));
        try {
            FieldOptions.visit(mod, this.getClass(), options::add);
        } catch (IllegalAccessException error) {
            throw new AssertionError(error);
        }
        return options;
    }

    @Override
    public void render(Position position, boolean editMode) {
        if (upgrades == null && !editMode) {
            return;
        }
        int x = position.getX() + 1;
        int y = position.getY() + 2;
        GlStateManager.color(1, 1, 1);
        boolean normalUpgrades = false;
        if (upgrades != null) {
            for (TeamUpgrade u : upgrades.upgrades) {
                if (!u.isPurchased()) {
                    continue;
                }
                if (u instanceof TrapUpgrade) {
                    continue;
                }
                String texture = u.getTexture()[0];
                mc.getTextureManager().bindTexture(new Identifier("sol_client", "textures/bedwars/" + texture + ".png"));
                DrawableHelper.drawTexture(x, y, 0, 0, 16, 16, 16, 16);
                x += 17;
                normalUpgrades = true;
            }
        }
        x = position.getX() + 1;
        if (normalUpgrades) {
            y += 17;
        }
        for (String texture : (editMode ? trapEdit : upgrades.trap.getTexture())) {
            mc.getTextureManager().bindTexture(new Identifier("sol_client", "textures/bedwars/" + texture + ".png"));
            DrawableHelper.drawTexture(x, y, 0, 0, 16, 16, 16, 16);
            x += 17;
        }
    }

    @Override
    public Mod getMod() {
        return mod;
    }

    @Override
    public boolean isShownInReplay() {
        return false;
    }
}
