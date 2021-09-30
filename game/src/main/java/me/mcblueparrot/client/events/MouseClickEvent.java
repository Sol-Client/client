package me.mcblueparrot.client.events;

public class MouseClickEvent {

    public int button;
    public boolean cancelled;

    public MouseClickEvent(int button) {
        this.button = button;
    }

}
