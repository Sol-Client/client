package io.github.solclient.client.mod.impl.hypixeladditions;

import io.github.solclient.client.util.ApacheHttpClient;
import io.github.solclient.client.util.MinecraftUtils;
import net.hypixel.api.HypixelAPI;
import net.hypixel.api.reply.PlayerReply;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class HypixelAPICache {

    private static final HypixelAPICache INSTANCE = new HypixelAPICache();

    private HypixelAPI api = null;
    private final Map<UUID, PlayerReply.Player> playerCache = new HashMap<>();
    private final Map<UUID, CompletableFuture<PlayerReply.Player>> responseCache = new HashMap<>();
    private final Map<UUID, Integer> levelCache = new HashMap<>();

    public static HypixelAPICache getInstance() {
        return INSTANCE;
    }

    private HypixelAPICache() {}

    public Optional<PlayerReply.Player> getPlayerFromCache(UUID uuid) {
        return Optional.ofNullable(playerCache.get(uuid));
    }


    public Optional<CompletableFuture<PlayerReply.Player>> getPlayerOrRequest(UUID uuid) {
        PlayerReply.Player cached = playerCache.get(uuid);
        if (cached != null) {
            return Optional.of(CompletableFuture.completedFuture(cached));
        }
        CompletableFuture<PlayerReply.Player> cachedResponse = responseCache.get(uuid);
        if (cachedResponse != null) {
            return Optional.of(cachedResponse);
        }
        if (api == null) {
            return Optional.empty();
        }
        CompletableFuture<PlayerReply.Player> reply = api.getPlayerByUuid(uuid).thenApplyAsync(playerReply -> {
            responseCache.remove(uuid);
            if (!playerReply.isSuccess()) {
                return null;
            }
            playerCache.put(uuid, playerReply.getPlayer());
            return playerReply.getPlayer();
        });
        responseCache.put(uuid, reply);
        return Optional.of(reply);
    }


    public String getLevelHead(UUID id) {

        if (levelCache.containsKey(id)) {
            // If it exists in cache then we are computing it/have the result
            Integer result = levelCache.get(id);
            if (result < 0) {
                return null;
            }
            return String.valueOf(result);
        }

        if (api == null) {
            return null;
        }

        // Put here first because we are waiting for computation to come back
        levelCache.put(id, -1);
        getPlayerOrRequest(id).ifPresent(c -> c.whenCompleteAsync((player, error) -> {
            if (player == null || error != null) {
                return;
            }

            if (player.exists()) {
                levelCache.put(id, (int) player.getNetworkLevel());
            } else {
                // At this stage, the player is either nicked, or an NPC, but all NPCs and fake
                // players I've tested do not get to this stage.
                levelCache.put(id, MinecraftUtils.randomInt(120, 280));
                // Based on looking at YouTubers' Hypixel levels. It won't
                // actually be the true level, and may not look quite right,
                // but it's more plausible than a Level 1 god bridger.
            }
        }));
        return null;
    }

    public void setAPIKey(String apiKey) {
        api = new HypixelAPI(new ApacheHttpClient(UUID.fromString(apiKey)));
    }

    public void clear() {
        responseCache.forEach((k, v) -> v.obtrudeValue(null));
        responseCache.clear();
        playerCache.clear();
        levelCache.clear();
    }
}
