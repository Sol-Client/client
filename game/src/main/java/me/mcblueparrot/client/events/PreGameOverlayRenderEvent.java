package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.annotation.semantic.ForgeCompat;

@RequiredArgsConstructor
public class PreGameOverlayRenderEvent {

    public final float partialTicks;
    public final GameOverlayElement type;
    public boolean cancelled;

    @Deprecated
    @ForgeCompat
    public void setCanceled(boolean cancelled) {
        this.cancelled = cancelled;
    }

}
