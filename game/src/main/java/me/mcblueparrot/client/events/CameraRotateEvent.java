package me.mcblueparrot.client.events;

public class CameraRotateEvent {

    public float yaw;
    public float pitch;

    public CameraRotateEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
