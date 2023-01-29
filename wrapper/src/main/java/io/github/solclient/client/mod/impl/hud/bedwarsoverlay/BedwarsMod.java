package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.ModCategory;

import java.util.regex.Pattern;

public class BedwarsMod extends Mod {

    private final static Pattern GAME_START = Pattern.compile("^\\s*?Protect your bed and destroy the enemy beds\\.\\s*?$");

    private BedwarsGame currentGame = null;
    private int targetTick = -1;

    @EventHandler
    public void onMessage(ReceiveChatMessageEvent event) {
        // Remove formatting
        String rawMessage = event.message.replaceAll("§.", "");
        if (currentGame != null) {
            currentGame.onChatMessage(rawMessage, event);
        } else if (targetTick < 0 && GAME_START.matcher(rawMessage).matches()) {
            // Give time for Hypixel to sync
            targetTick = mc.inGameHud.getTicks() + 10;
        }
    }

    @EventHandler
    public void onTick(PreTickEvent event) {
        if (currentGame != null) {
            currentGame.tick();
        } else {
            if (targetTick > 0 && mc.inGameHud.getTicks() > targetTick) {
                currentGame = new BedwarsGame();
                currentGame.onStart();
            }
        }
    }

    @Override
    public String getId() {
        return "bedwars";
    }

    @Override
    public ModCategory getCategory() {
        return ModCategory.HUD;
    }

}
