package io.github.solclient.client;

import java.util.ArrayList;
import java.util.List;

import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.input.MouseDownEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CpsCounter {

	public static final CpsCounter LMB = new CpsCounter(0);
	public static final CpsCounter RMB = new CpsCounter(1);

	private final int button;
	private List<Long> presses = new ArrayList<Long>();

	public static void register() {
		Client.INSTANCE.getBus().register(LMB);
		Client.INSTANCE.getBus().register(RMB);
	}

	@EventHandler
	public void onMouseClickEvent(MouseDownEvent event) {
		if(event.getButton() == button) {
			click();
		}
	}

	@EventHandler
	public void tick(PostTickEvent event) {
		presses.removeIf(t -> System.currentTimeMillis() - t > 1000);
	}

	public void click() {
		presses.add(System.currentTimeMillis());
	}

	public int getCps() {
		return presses.size();
	}

}
