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

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;
import com.replaymod.replay.ReplayModReplay;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.ModOptionStorage;
import io.github.solclient.client.mod.option.impl.SliderOption;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractHudElement implements HudElement {

    public static final String TRANSLATION_KEY = "sol_client.mod.hud";

    /** The exact x/y on the screen */
    protected Position truePosition = new Position(0, 0);

    /** Scaled x/y on screen. Matrices should already be scaled if this is used */
    protected Position dividedPosition;

    private Rectangle previousBounds = null;

    @Expose
    protected float x = 0;
    @Expose
    protected float y = 0;

    @Expose
    protected float scale = 100;

    @Expose
    protected AnchorPoint anchorPoint = AnchorPoint.TOP_LEFT;

    private int lastWidth = 0;
    private int lastHeight = 0;

    @Override
    public AnchorPoint getAnchor() {
        return isDynamic() ? anchorPoint : AnchorPoint.TOP_LEFT;
    }

    @Override
    public List<ModOption<?>> createOptions() {
        List<ModOption<?>> options = new ArrayList<>();
        options.add(new SliderOption(TRANSLATION_KEY + ".option.scale",
                        ModOptionStorage.of(Number.class, () -> scale, (value) -> {
                            if (scale != value.floatValue()) {
                                scale = value.floatValue();
                                updateBounds(true);
                            }
                        }),
                        Optional.of("sol_client.slider.percent"), 50, 150, 1));
        return options;
    }

    @Override
    public float getScale() {
        return scale / 100;
    }

    public static int floatToInt(float percent, int max, int offset) {
        return MathHelper.clamp(Math.round((max - offset) * percent), 0, max);
    }

    public static float intToFloat(int current, int max, int offset) {
        return MathHelper.clamp((float) (current) / (max - offset), 0, 1);
    }

    public int offsetTrueWidth(boolean editMode) {
        return getAnchor().offsetWidth(getMultipliedBounds(editMode).getWidth());
    }

    public int offsetTrueHeight(boolean editMode) {
        return getAnchor().offsetHeight(getMultipliedBounds(editMode).getHeight());
    }

    @Override
    public Position getDividedPosition() {
        return dividedPosition;
    }

    @Override
    public Position getConfiguredPosition() {
        return truePosition;
    }

    @Override
    public Position getPosition() {
        return truePosition;
    }

    @Override
    public void setAnchorPoint(AnchorPoint p) {
        this.anchorPoint = p;
    }

    @Override
    public void render(boolean editMode) {
        // Don't render HUD in replay or if marked as invisible.
        if (!isVisible() || !(editMode || isShownInReplay() || ReplayModReplay.instance.getReplayHandler() == null))
            return;

        Window window = new Window(MinecraftClient.getInstance());
        if ((int) window.getScaledWidth() != lastWidth || (int) window.getScaledHeight() != lastHeight) {
            lastWidth = (int) window.getScaledWidth();
            lastHeight = (int) window.getScaledHeight();
            updateBounds(editMode, lastWidth, lastHeight);
            previousBounds = getBounds(editMode);
        } else {
            Rectangle rect = getBounds(editMode);
            if (rect != null) {
                if (previousBounds == null) {
                    previousBounds = updateBounds(editMode);
                } else if (rect.getWidth() != previousBounds.getWidth() || rect.getHeight() != previousBounds.getHeight()) {
                    previousBounds = updateBounds(editMode);
                }
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.scale(getScale(), getScale(), getScale());
        render(getDividedPosition(), editMode);
        GlStateManager.popMatrix();
    }

    @Override
    public void setPosition(Position position, boolean editMode) {
        int x = position.getX() + offsetTrueWidth(editMode);
        int y = position.getY() + offsetTrueHeight(editMode);
        Window window = new Window(MinecraftClient.getInstance());
        this.x = intToFloat(x, (int) window.getScaledWidth(), 0);
        this.y = intToFloat(y, (int) window.getScaledHeight(), 0);
        updateBounds(editMode);
    }

    @Override
    public Rectangle updateBounds(boolean editMode, int scaledWidth, int scaledHeight) {
        Rectangle bounds = getBounds(editMode);
        if (scaledHeight == 0) {
            truePosition = new Position(0, 0);
            return truePosition.rectangle(bounds.getWidth(), bounds.getHeight());
        }
        int scaledX = floatToInt(x, scaledWidth, 0) - offsetTrueWidth(editMode);
        int scaledY = floatToInt(y, scaledHeight, 0) - offsetTrueHeight(editMode);
        if (scaledX < 0) {
            scaledX = 0;
        }
        if (scaledY < 0) {
            scaledY = 0;
        }
        int trueWidth = (int) (bounds.getWidth() * getScale());
        if (trueWidth < scaledWidth && scaledX + trueWidth > scaledWidth) {
            scaledX = scaledWidth - trueWidth;
        }
        int trueHeight = (int) (bounds.getHeight() * getScale());
        if (trueHeight < scaledHeight && scaledY + trueHeight > scaledHeight) {
            scaledY = scaledHeight - trueHeight;
        }
        truePosition = new Position(scaledX, scaledY);
        dividedPosition = new Position((int) (scaledX / getScale()), (int) (scaledY / getScale()));
        return truePosition.rectangle(bounds.getWidth(), bounds.getHeight());
    }

}
