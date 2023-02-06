package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Value;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameLogDisplay implements HudElement {

    private final BedwarsMod mod;
    private final MinecraftClient mc;
    private final List<Message> messages = new ArrayList<>();
    private final static List<Message> EDIT_MESSAGES = Arrays.asList(
        new Message(-1, "☠ §9B1§7 //§c R2"),
        new Message(-1, "☠ §bA1")
    );
    private Position position = new Position(100, 100);

    public GameLogDisplay(BedwarsMod mod) {
        this.mod = mod;
        this.mc = MinecraftClient.getInstance();
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public float getScale() {
        return 1f;
    }

    @Override
    public Position getConfiguredPosition() {
        return position;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public Rectangle getBounds(Position position) {
        return position.rectangle(100, 200);
    }

    @Override
    public void render(Position position, boolean editMode) {
        int tick = mc.inGameHud.getTicks();
        int y = 0;
        for (Message message : (editMode ? EDIT_MESSAGES : messages)) {
            y += 9;
            int color = -1;
            if (!editMode && message.tickCreated > 20 * 5 + tick) {
                if (message.tickCreated > 20 * 8 + tick) {
                    break;
                }
                color = new Colour(255, 255, 255, (int) ((1 - ((float) 20 * 5 - tick) / (20 * 3)) * 255)).getValue();
            }
            mc.textRenderer.draw(message.content, position.getX(), position.getY() - y, color);
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

    public void push(Text content) {
        push(content.asFormattedString());
    }

    public void push(String content) {
        int tick = mc.inGameHud.getTicks();
        messages.add(0, new Message(tick, content));
        while (messages.size() > 300) {
            messages.remove(300);
        }
    }

    public void died(BedwarsPlayer player, @Nullable BedwarsPlayer killer, boolean finaled) {
        if (killer == null) {
            push(new LiteralText("☠ " + player.getColoredTeamNumber()));
        } else {
            push(new LiteralText("☠ " + player.getColoredTeamNumber() + "§7 // " + killer.getColoredTeamNumber()));
        }
    }

    @Value
    public static class Message {
        int tickCreated;
        String content;

    }

}
