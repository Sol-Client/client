package io.github.solclient.client.mod.impl.hud.bedwarsoverlay;


import lombok.Data;
import net.minecraft.client.network.PlayerListEntry;

@Data
public class BedwarsPlayer {

    private final BedwarsTeam team;
    private final PlayerListEntry entry;
    private boolean alive;
    private boolean bed;

    public String getName() {
        return entry.getProfile().getName();
    }

    public String coloredName() {
        return team.getColorSection() + getName();
    }

}
