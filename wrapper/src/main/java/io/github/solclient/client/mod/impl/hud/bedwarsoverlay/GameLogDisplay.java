package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.BaseHudElement;
import io.github.solclient.client.mod.hud.HudPosition;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import org.jetbrains.annotations.Nullable;

public class GameLogDisplay extends BaseHudElement {

    private final BedwarsMod mod;
    private HudPosition position = new HudPosition(0, 0);

    public GameLogDisplay(BedwarsMod mod) {
        this.mod = mod;
    }

    @Override
    public HudPosition getHudPosition() {
        return position;
    }

    @Override
    public void setHudPosition(HudPosition position) {
        this.position = position;
    }

    @Override
    public float getHudScale() {
        return 1f;
    }

    @Override
    public boolean isVisible() {
        return mod.inGame();
    }

    @Override
    public Rectangle getBounds(Position position) {
        return position.rectangle(100, 200);
    }

    @Override
    public void render(Position position, boolean editMode) {

    }

    @Override
    public Mod getMod() {
        return mod;
    }

    @Override
    public boolean isShownInReplay() {
        return false;
    }

    public void died(BedwarsPlayer player, @Nullable BedwarsPlayer killer, boolean finaled) {

    }

}
