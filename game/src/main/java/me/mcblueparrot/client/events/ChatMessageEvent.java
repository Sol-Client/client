package me.mcblueparrot.client.events;

public class ChatMessageEvent {

    public boolean actionBar;
    public boolean cancelled;
    public String message;

    public ChatMessageEvent(boolean actionBar, String message) {
        this.actionBar = actionBar;
        this.message = message;
    }

}
