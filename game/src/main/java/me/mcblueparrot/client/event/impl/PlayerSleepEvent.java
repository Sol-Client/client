package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

@AllArgsConstructor
public class PlayerSleepEvent {

	public EntityPlayer entityPlayer;
	public BlockPos pos;

}
