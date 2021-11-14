package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;

@RequiredArgsConstructor
public class ScoreboardRenderEvent {

    public final ScoreObjective objective;
    public final ScaledResolution scaledRes;
    public boolean cancelled;

}
