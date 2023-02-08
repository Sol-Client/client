package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import com.mojang.blaze3d.platform.GlStateManager;
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
    private int maxNameLength = 1;

    public GameLogDisplay(BedwarsMod mod) {
        this.mod = mod;
        this.mc = MinecraftClient.getInstance();
    }

    public void gameStart(int maxNameLength) {
        this.messages.clear();
        this.maxNameLength = maxNameLength;
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

    public int getSecondsBeforeFade() {
        return 10;
    }

    public int getFadeSeconds() {
        return 3;
    }

    @Override
    public void render(Position position, boolean editMode) {
        GlStateManager.enableBlend();
        GlStateManager.blendFuncSeparate(770, 771, 1, 0);
        int tick = mc.inGameHud.getTicks();
        int y = -200;
        boolean chatFocused = mc.inGameHud.getChatHud().isChatFocused();
        for (Message message : (editMode ? EDIT_MESSAGES : messages)) {
            y += 9;
            int color = -1;
            int tickAlive = (tick - message.tickCreated);
            if (!chatFocused && !editMode && tickAlive > 20 * getSecondsBeforeFade()) {
                if (tickAlive > 20 * getSecondsBeforeFade() + getFadeSeconds()) {
                    // Stop rendering
                    break;
                }
                int tickFade = tickAlive - 20 * getSecondsBeforeFade();
                float alpha = 1 - (tickFade / (20f * getFadeSeconds()));
                color = new Colour(255, 255, 255, (int) (alpha * 255)).getValue();
            }
            mc.textRenderer.draw(message.content, position.getX(), position.getY() - y, color);
        }
        GlStateManager.disableBlend();
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

    public String died(BedwarsPlayer player, @Nullable BedwarsPlayer killer, boolean finaled) {
        String time = "§7" + mod.getGame().get().getFormattedTime() + " ";
        if (killer == null) {
            return time + getPlayerFormatted(player) + " §7/death/";
        } else {
            if (finaled && killer.getStats() != null) {
                return time + getPlayerFormatted(player) + " §6§n/FINAL/§r " + getPlayerFormatted(killer) + " §b#" + killer.getStats().getFinalKills();
            } else {
                return time + getPlayerFormatted(player) + " §7/death/ " + getPlayerFormatted(killer);
            }
        }
    }

    private String getPlayerFormatted(BedwarsPlayer player) {
        return player.getColoredTeamNumber() + " " + player.getProfile().getProfile().getName();
    }

    private String format(String name) {
        if (name.length() >= maxNameLength) {
            return name;
        }
        int dif = maxNameLength - name.length();
        StringBuilder nameBuilder = new StringBuilder(name);
        for (int i = 0; i < dif; i++) {
            nameBuilder.append(" ");
        }
        return nameBuilder.toString();
    }

    @Value
    public static class Message {
        int tickCreated;
        String content;

    }

}
