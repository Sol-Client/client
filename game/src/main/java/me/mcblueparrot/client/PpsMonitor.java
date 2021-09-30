package me.mcblueparrot.client;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.events.MouseClickEvent;
import me.mcblueparrot.client.events.TickEvent;

public class PpsMonitor {

    public static final PpsMonitor LMB = new PpsMonitor(0);
    public static final PpsMonitor RMB = new PpsMonitor(1);

    public static void forceInit() {}

    private int button;
    private List<Long> presses = new ArrayList<Long>();
//    private KeyBinding keyBind;

    public PpsMonitor(int button) {
        this.button = button;
        Client.INSTANCE.bus.register(this);
    }

    @EventHandler
    public void onMouseClickEvent(MouseClickEvent event) {
        if(event.button == button) {
            click();
        }
    }

    public void click() {
        presses.add(System.currentTimeMillis());
    }

    @EventHandler
    public void tick(TickEvent event) {
        presses.removeIf(new Predicate<Long>() {

            @Override
            public boolean test(Long t) {
                return System.currentTimeMillis() - t > 1000;
            }

        });
    }

    public int getPps() {
        return presses.size();
    }

}