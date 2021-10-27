package me.mcblueparrot.client.events;

public class PlayerHeadRotateEvent {

    public float yaw;
    public float pitch;
    public boolean cancelled;

    public PlayerHeadRotateEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
