package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

@AllArgsConstructor
public class PlayerSleepEvent {

	public final PlayerEntity player;
	public final BlockPos pos;

}
