package io.github.solclient.client.event.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.util.Window;
import net.minecraft.scoreboard.ScoreboardObjective;

@RequiredArgsConstructor
public class ScoreboardRenderEvent {

	public final ScoreboardObjective objective;
	public final Window window;
	public boolean cancelled;

}
