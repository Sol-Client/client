package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.hypixel.api.reply.PlayerReply;
import org.jetbrains.annotations.Nullable;


@AllArgsConstructor
public class BedwarsPlayerStats {

    @Getter
    private int finalKills;
    @Getter
    private int finalDeaths;
    @Getter
    private int bedsBroken;
    @Getter
    private int deaths;
    @Getter
    private int kills;
    @Getter
    private final int losses;
    @Getter
    private final int wins;
    @Getter
    private final int winstreak;


    public static BedwarsPlayerStats generateFake() {
        return new BedwarsPlayerStats(0, 0, 0, 0, 0, 0, 0, 0);
    }

    @Nullable
    public static BedwarsPlayerStats fromAPI(PlayerReply.Player player) {
        JsonElement rawStats = player.getProperty("stats");
        if (rawStats == null || !rawStats.isJsonObject()) {
            return null;
        }
        JsonObject stats = rawStats.getAsJsonObject();
        JsonObject bedwars = getObjectSafe(stats, "Bedwars");
        if (bedwars == null) {
            return null;
        }
        int finalKills = getAsIntElse(bedwars, "final_kills_bedwars", 0);
        int finalDeaths = getAsIntElse(bedwars, "final_deaths_bedwars", 0);
        int bedsBroken = getAsIntElse(bedwars, "beds_broken_bedwars", 0);
        int deaths = getAsIntElse(bedwars, "deaths_bedwars", 0);
        int kills = getAsIntElse(bedwars, "kills_bedwars", 0);
        int losses = getAsIntElse(bedwars, "losses_bedwars", 0);
        int wins = getAsIntElse(bedwars, "wins_bedwars", 0);
        int winstreak = getAsIntElse(bedwars, "winstreak", 0);
        return  new BedwarsPlayerStats(finalKills, finalDeaths, bedsBroken, deaths, kills, losses, wins, winstreak);
    }

    private static int getAsIntElse(JsonObject obj, String key, int other) {
        if (obj.has(key)) {
            try {
                return obj.get(key).getAsInt();
            } catch (NumberFormatException | UnsupportedOperationException | IllegalStateException e) {
                // Not actually an int
            }
        }
        return other;
    }

    private static JsonObject getObjectSafe(JsonObject object, String key) {
        if (!object.has(key)) {
            return null;
        }
        JsonElement el = object.get(key);
        if (!el.isJsonObject()) {
            return null;
        }
        return el.getAsJsonObject();
    }

    public void addDeath() {
        deaths++;
    }

    public void addFinalDeath() {
        finalDeaths++;
    }

    public void addKill() {
        kills++;
    }

    public void addFinalKill() {
        finalKills++;
    }

    public void addBed() {
        bedsBroken++;
    }
}
