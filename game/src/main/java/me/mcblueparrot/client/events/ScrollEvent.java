package me.mcblueparrot.client.events;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ScrollEvent {

    public final int amount;
    public boolean cancelled;

}
