package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;

public class ScoreboardRenderEvent {

    public boolean cancelled;
    public ScoreObjective objective;
    public ScaledResolution scaledRes;

    public ScoreboardRenderEvent(ScoreObjective objective, ScaledResolution scaledRes) {
        this.objective = objective;
        this.scaledRes = scaledRes;
    }

}
