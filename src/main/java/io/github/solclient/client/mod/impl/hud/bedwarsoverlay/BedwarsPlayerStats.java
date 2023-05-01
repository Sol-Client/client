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
    private int gameFinalKills;
    @Getter
    private int gameFinalDeaths;
    @Getter
    private int gameBedsBroken;
    @Getter
    private int gameDeaths;
    @Getter
    private int gameKills;
    @Getter
    private final int losses;
    @Getter
    private final int wins;
    @Getter
    private final int winstreak;
    @Getter
    private final int stars;


    public static BedwarsPlayerStats generateFake() {
        return new BedwarsPlayerStats(
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                1
        );
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
        JsonObject achievements = getObjectSafe(player.getRaw().getAsJsonObject(), "achievements");
        int stars = 1;
        if (achievements != null) {
            stars = getAsIntElse(achievements, "bedwars_level", 1);
        }
        return  new BedwarsPlayerStats(finalKills, finalDeaths, bedsBroken, deaths, kills, 0, 0, 0, 0, 0, losses, wins, winstreak, stars);
    }

    public static int getAsIntElse(JsonObject obj, String key, int other) {
        if (obj.has(key)) {
            try {
                return obj.get(key).getAsInt();
            } catch (NumberFormatException | UnsupportedOperationException | IllegalStateException e) {
                // Not actually an int
            }
        }
        return other;
    }

    public static JsonObject getObjectSafe(JsonObject object, String key) {
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
        gameDeaths++;
    }

    public void addFinalDeath() {
        finalDeaths++;
        gameFinalDeaths++;
    }

    public void addKill() {
        kills++;
        gameKills++;
    }

    public void addFinalKill() {
        finalKills++;
        gameFinalKills++;
    }

    public void addBed() {
        bedsBroken++;
        gameBedsBroken++;
    }

    public float getFKDR() {
        return (float) finalKills / finalDeaths;
    }

    public float getBBLR() {
        return (float) bedsBroken / losses;
    }

}
