package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;


import lombok.Data;
import lombok.Value;
import net.minecraft.client.network.PlayerListEntry;

@Data
public class BedwarsPlayer {

    private final BedwarsTeam team;
    private final PlayerListEntry entry;
    private boolean alive;

}
