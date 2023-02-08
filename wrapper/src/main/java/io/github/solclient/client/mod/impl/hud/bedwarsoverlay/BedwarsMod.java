package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import io.github.solclient.client.event.impl.ScoreboardRenderEvent;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.SolClientMod;
import net.minecraft.client.network.PlayerListEntry;


import java.util.*;
import java.util.regex.Pattern;

public class BedwarsMod extends SolClientMod {

    private final static Pattern[] GAME_START = {
            Pattern.compile("^\\s*?Protect your bed and destroy the enemy beds\\.\\s*?$"),
            Pattern.compile("^\\s*?Bed Wars Lucky Blocks\\s*?$"),
            Pattern.compile("^\\s*?Bed Wars Swappage\\s*?$")
    };

    private final static BedwarsMod INSTANCE = new BedwarsMod();

    public static BedwarsMod getInstance() {
        return INSTANCE;
    }

    protected BedwarsGame currentGame = null;
    protected final GameLogDisplay gameLog;
    private int targetTick = -1;

    private BedwarsMod() {
        gameLog = new GameLogDisplay(this);
    }

    @EventHandler
    public void onMessage(ReceiveChatMessageEvent event) {
        // Remove formatting
        String rawMessage = event.originalMessage.replaceAll("§.", "");
        if (currentGame != null) {
            currentGame.onChatMessage(rawMessage, event);
        } else if (targetTick < 0 && BedwarsGame.matched(GAME_START, rawMessage).isPresent()) {
            // Give time for Hypixel to sync
            targetTick = mc.inGameHud.getTicks() + 10;
        }
    }

    public Optional<BedwarsGame> getGame() {
        return currentGame == null ? Optional.empty() : Optional.of(currentGame);
    }

    @EventHandler
    public void onTick(PreTickEvent event) {
        if (currentGame != null) {
            if (currentGame.isStarted()) {
                // Trigger setting the header
                mc.inGameHud.getPlayerListWidget().setHeader(null);
                currentGame.tick();
            } else {
                boolean ready = false;
                for (PlayerListEntry player : mc.player.networkHandler.getPlayerList()) {
                    String name = mc.inGameHud.getPlayerListWidget().getPlayerName(player).replaceAll("§.", "");
                    if (name.charAt(1) == ' ') {
                        ready = true;
                        break;
                    }
                }
                if (ready) {
                    currentGame.onStart();
                }
            }
        } else {
            if (targetTick > 0 && mc.inGameHud.getTicks() > targetTick) {
                currentGame = new BedwarsGame(this);
                targetTick = -1;
            }
        }
    }

    @Override
    public List<HudElement> getHudElements() {
        return Arrays.asList(gameLog);
    }

    @Override
    public String getId() {
        return "bedwars";
    }

    @Override
    public ModCategory getCategory() {
        return ModCategory.HUD;
    }

    public boolean inGame() {
        return currentGame != null && currentGame.isStarted();
    }

    @EventHandler
    public void onScoreboardRender(ScoreboardRenderEvent event) {
        if (inGame()) {
            currentGame.onScoreboardRender(event);
        }
    }

    public void gameEnd() {
        currentGame = null;
    }

}
