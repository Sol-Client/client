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

import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PreTickEvent;
import io.github.solclient.client.event.impl.ReceiveChatMessageEvent;
import io.github.solclient.client.event.impl.ScoreboardRenderEvent;
import io.github.solclient.client.event.impl.WorldLoadEvent;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.option.ModOption;
import io.github.solclient.client.mod.option.annotation.AbstractTranslationKey;
import io.github.solclient.client.mod.option.annotation.Option;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;


import java.util.*;
import java.util.regex.Pattern;

@AbstractTranslationKey("sol_client.mod.bedwars")
public final class BedwarsMod extends StandardMod {

    private final static Pattern[] GAME_START = {
            Pattern.compile("^\\s*?Protect your bed and destroy the enemy beds\\.\\s*?$"),
            Pattern.compile("^\\s*?Bed Wars Lucky Blocks\\s*?$"),
            Pattern.compile("^\\s*?Bed Wars Swappage\\s*?$")
    };

    public static BedwarsMod instance;
    protected BedwarsGame currentGame = null;

    @Expose
    protected final TeamUpgradesOverlay upgradesOverlay;

    @Expose
    @Option
    protected boolean removeAnnoyingMessages = true;

    @Expose
    @Option
    protected boolean showChatTime = true;

    @Expose
    @Option
    protected boolean overrideMessages = true;
    private int targetTick = -1;

    public BedwarsMod() {
        upgradesOverlay = new TeamUpgradesOverlay(this);
    }

    @Override
    protected List<ModOption<?>> createOptions() {
        List<ModOption<?>> options = super.createOptions();
        options.addAll(upgradesOverlay.createOptions());
        return options;
    }

    @Override
    public String getDetail() {
        return I18n.translate("sol_client.mod.screen.by", "DarkKronicle") + I18n.translate("sol_client.mod.screen.textures_by", "Sybillian");
    }

    @Override
    public void init() {
    	super.init();
    	instance = this;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        if (currentGame != null) {
            gameEnd();
        }
    }

    @EventHandler
    public void onMessage(ReceiveChatMessageEvent event) {
        // Remove formatting
        String rawMessage = event.originalMessage.replaceAll("§.", "");
        if (currentGame != null) {
            currentGame.onChatMessage(rawMessage, event);
            String time = "§7" + currentGame.getFormattedTime() + " ";
            if (!event.cancelled && showChatTime) {
                // Add time to every message received in game
                if (event.newMessage != null) {
                    event.newMessage = new LiteralText(time).append(event.newMessage);
                } else {
                    event.newMessage = new LiteralText(time).append(event.formattedMessage);
                }
            }
        } else if (targetTick < 0 && BedwarsMessages.matched(GAME_START, rawMessage).isPresent()) {
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
                if (checkReady()) {
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

    private boolean checkReady() {
        for (PlayerListEntry player : mc.player.networkHandler.getPlayerList()) {
            String name = mc.inGameHud.getPlayerListWidget().getPlayerName(player).replaceAll("§.", "");
            if (name.charAt(1) == ' ') {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<HudElement> getHudElements() {
        return Arrays.asList(upgradesOverlay);
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
        upgradesOverlay.onEnd();
        currentGame = null;
    }

    @Override
    public void registerOtherTypeAdapters(GsonBuilder builder) {
        builder.registerTypeAdapter(TeamUpgradesOverlay.class, (InstanceCreator<TeamUpgradesOverlay>) (type) -> upgradesOverlay);
    }
}
