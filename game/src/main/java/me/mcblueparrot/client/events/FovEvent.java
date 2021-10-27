package me.mcblueparrot.client.events;

public class FovEvent {

    public float fov;
    public float partialTicks;

    public FovEvent(float fov, float partialTicks) {
        this.fov = fov;
        this.partialTicks = partialTicks;
    }

}
