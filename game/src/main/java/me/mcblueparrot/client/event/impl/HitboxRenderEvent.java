package me.mcblueparrot.client.event.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.Entity;

@RequiredArgsConstructor
public class HitboxRenderEvent {

	public final Entity entity;
	public final double x;
	public final double y;
	public final double z;
	public final float entityYaw;
	public final float partialTicks;
	public boolean cancelled;

}
