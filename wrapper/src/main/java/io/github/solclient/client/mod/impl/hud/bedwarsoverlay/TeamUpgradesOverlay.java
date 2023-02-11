package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.hud.bedwarsoverlay.upgrades.BedwarsTeamUpgrades;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.util.Identifier;

public class TeamUpgradesOverlay implements HudElement {

    private BedwarsTeamUpgrades upgrades = null;
    private final BedwarsMod mod;
    private final MinecraftClient mc;
    private final static String[] trapEdit = {"trap/minerfatigue", "trap/itsatrap"};

    private Position position = new Position(100, 100);

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
        return 1;
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
        return true;
    }

    @Override
    public Rectangle getBounds(Position position) {
        return position.rectangle(60, 20);
    }

    @Override
    public void render(Position position, boolean editMode) {
        if (upgrades == null && !editMode) {
            return;
        }
        int x = position.getX() + 1;
        int y = position.getY() + 2;
        GlStateManager.color(1, 1, 1);
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
