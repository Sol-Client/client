package me.mcblueparrot.client.events;

public class ScrollEvent {

    public int amount;
    public boolean cancelled;

    public ScrollEvent(int amount) {
        this.amount = amount;
    }

}
