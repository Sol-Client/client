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


import io.github.solclient.client.mod.impl.hypixeladditions.HypixelAPICache;
import lombok.Data;
import lombok.Getter;
import net.hypixel.api.reply.PlayerReply;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Data
public class BedwarsPlayer {

    private final BedwarsTeam team;
    @Getter
    private PlayerListEntry profile;
    private boolean alive = true;
    private boolean disconnected = false;
    private boolean bed = true;
    private final int number;
    private BedwarsPlayerStats stats = null;
    private boolean triedStats = false;
    private int tickAlive = -1;

    public BedwarsPlayer(BedwarsTeam team, PlayerListEntry profile, int number) {
        this.team = team;
        this.profile = profile;
        this.number = number;
    }

    public String getColoredTeamNumber(String format) {
        return getTeam().getColorSection() + format + getTeam().getPrefix() + getNumber();
    }

    public String getColoredTeamNumber() {
        return getTeam().getColorSection() + getTeam().getPrefix() + getNumber();
    }

    public String getName() {
        return profile.getProfile().getName();
    }

    public String getColoredName() {
        return team.getColorSection() + getName();
    }

    public String getTabListDisplay() {
        if (alive) {
            if (bed) {
                return team.getColorSection() + "§l" + team.getPrefix() + number + " " + getColoredName();
            }
            return team.getColorSection() + "§l" + team.getPrefix() + number + team.getColorSection() + "§o "  + getName();
        }
        if (disconnected) {
            return team.getColorSection() + "§l§m" + team.getPrefix() + number + "§7 §o§n"  + getName();
        }
        return team.getColorSection() + "§l§m" + team.getPrefix() + number + "§7 §m"  + getName();
    }

    public void updateListEntry(PlayerListEntry entry) {
        this.profile = entry;
    }

    public boolean isFinalKilled() {
        return tickAlive < 0 && !bed && !alive || (!bed && isDisconnected());
    }

    public void tick(int currentTick) {
        if (stats == null && !triedStats) {
            triedStats = true;
            Optional<CompletableFuture<PlayerReply.Player>> future = HypixelAPICache.getInstance().getPlayerOrRequest(profile.getProfile().getId());
            if (!future.isPresent()) {
                stats = BedwarsPlayerStats.generateFake();
            } else {
                future.get().whenCompleteAsync((player, error) -> {
                    if (error != null) {
                        stats = BedwarsPlayerStats.generateFake();
                        return;
                    }
                    stats = BedwarsPlayerStats.fromAPI(player);
                });
            }
        }
        if (alive || tickAlive < 0) {
            return;
        }
        if (currentTick >= tickAlive) {
            alive = true;
            tickAlive = -1;
        }
    }

    public void died() {
        if (!alive) {
            if (!bed) {
                tickAlive = -1;
            }
            return;
        }
        if (stats != null) {
            if (!bed) {
                stats.addFinalDeath();
            } else {
                stats.addDeath();
            }
        }
        alive = false;
        if (!bed) {
            tickAlive = -1;
            return;
        }
        int currentTick = MinecraftClient.getInstance().inGameHud.getTicks();
        tickAlive = currentTick + 20 * 5; // 5 second respawn
    }

    public void disconnected() {
        if (stats != null) {
            if (!bed) {
                stats.addFinalDeath();
            } else {
                stats.addDeath();
            }
        }
        disconnected = true;
        tickAlive = -1;
        alive = false;
    }

    public void reconnected() {
        disconnected = false;
        int currentTick = MinecraftClient.getInstance().inGameHud.getTicks();
        tickAlive = currentTick + 20 * 10; // 10 second respawn
    }

    public void killed(boolean finalKill) {
        if (stats != null) {
            if (finalKill) {
                stats.addFinalKill();
            } else {
                stats.addKill();
            }
        }
    }
}
