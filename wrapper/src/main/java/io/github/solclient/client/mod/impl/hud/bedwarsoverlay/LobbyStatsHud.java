package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

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
