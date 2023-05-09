package io.github.solclient.client.mod.impl.chathotkeys;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.util.KeyBindingInterface;
import lombok.*;

@EqualsAndHashCode
@AllArgsConstructor
public class Hotkey implements KeyBindingInterface {

	@Expose
	public int key;
	@Getter
	@Setter
	@Expose
	public int mods;
	@Expose
	public String value;

	@Override
	public int getKeyCode() {
		return key;
	}

	@Override
	public void setKeyCode(int keyCode) {
		key = keyCode;
	}

	@Override
	public void increaseTimesPressed() {
	}

	@Override
	public void setPressed(boolean pressed) {
	}

}
