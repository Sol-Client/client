package io.github.solclient.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
public class PlayerSleepEvent {

	public final EntityPlayer entityPlayer;
	public final BlockPos pos;

}
