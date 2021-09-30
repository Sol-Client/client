package me.mcblueparrot.client.events;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.MovingObjectPosition;

@RequiredArgsConstructor
public class BlockHighlightRenderEvent {

    public final MovingObjectPosition movingObjectPosition;
    public final float partialTicks;
    public boolean cancelled;

}
