package io.github.solclient.client.v1_8_9.mixins.platform.mc.util;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.mc.util.Input;
import lombok.experimental.UtilityClass;

@UtilityClass
@Mixin(Input.class)
public class InputImpl {

	@Overwrite(remap = false)
	private int getKeyCode(int glfw) {
		// old-style switch for Java 8 compat.
		switch(glfw) {
			case 32:	return Keyboard.KEY_SPACE;
			case 39:	return Keyboard.KEY_APOSTROPHE;
			case 44:	return Keyboard.KEY_COMMA;
			case 45:	return Keyboard.KEY_MINUS;
			case 46:	return Keyboard.KEY_PERIOD;
			case 47:	return Keyboard.KEY_SLASH;
			case 48:	return Keyboard.KEY_0;
			case 49:	return Keyboard.KEY_1;
			case 50:	return Keyboard.KEY_2;
			case 51:	return Keyboard.KEY_3;
			case 52:	return Keyboard.KEY_4;
			case 53:	return Keyboard.KEY_5;
			case 54:	return Keyboard.KEY_6;
			case 55:	return Keyboard.KEY_7;
			case 56:	return Keyboard.KEY_8;
			case 57:	return Keyboard.KEY_9;
			case 59:	return Keyboard.KEY_SEMICOLON;
			case 61:	return Keyboard.KEY_EQUALS;
			case 65:	return Keyboard.KEY_A;
			case 66:	return Keyboard.KEY_B;
			case 67:	return Keyboard.KEY_C;
			case 68:	return Keyboard.KEY_D;
			case 69:	return Keyboard.KEY_E;
			case 70:	return Keyboard.KEY_F;
			case 71:	return Keyboard.KEY_G;
			case 72:	return Keyboard.KEY_H;
			case 73:	return Keyboard.KEY_I;
			case 74:	return Keyboard.KEY_J;
			case 75:	return Keyboard.KEY_K;
			case 76:	return Keyboard.KEY_L;
			case 77:	return Keyboard.KEY_M;
			case 78:	return Keyboard.KEY_N;
			case 79:	return Keyboard.KEY_O;
			case 80:	return Keyboard.KEY_P;
			case 81:	return Keyboard.KEY_Q;
			case 82:	return Keyboard.KEY_R;
			case 83:	return Keyboard.KEY_S;
			case 84:	return Keyboard.KEY_T;
			case 85:	return Keyboard.KEY_U;
			case 86:	return Keyboard.KEY_V;
			case 87:	return Keyboard.KEY_W;
			case 88:	return Keyboard.KEY_X;
			case 89:	return Keyboard.KEY_Y;
			case 90:	return Keyboard.KEY_Z;
			case 91:	return Keyboard.KEY_LBRACKET;
			case 92:	return Keyboard.KEY_BACKSLASH;
			case 93:	return Keyboard.KEY_RBRACKET;
			case 96:	return Keyboard.KEY_GRAVE;
			case 256:	return Keyboard.KEY_ESCAPE;
			case 257:	return Keyboard.KEY_RETURN;
			case 258:	return Keyboard.KEY_TAB;
			case 259:	return Keyboard.KEY_BACK;
			case 260:	return Keyboard.KEY_INSERT;
			case 261:	return Keyboard.KEY_DELETE;
			case 262:	return Keyboard.KEY_RIGHT;
			case 263:	return Keyboard.KEY_LEFT;
			case 264:	return Keyboard.KEY_DOWN;
			case 265:	return Keyboard.KEY_UP;
			case 266:	return Keyboard.KEY_PRIOR;
			case 267:	return Keyboard.KEY_NEXT;
			case 268:	return Keyboard.KEY_HOME;
			case 269:	return Keyboard.KEY_END;
			case 280:	return Keyboard.KEY_CAPITAL;
			case 281:	return Keyboard.KEY_SCROLL;
			case 282:	return Keyboard.KEY_NUMLOCK;
			case 284:	return Keyboard.KEY_PAUSE;
			case 290:	return Keyboard.KEY_F1;
			case 291:	return Keyboard.KEY_F2;
			case 292:	return Keyboard.KEY_F3;
			case 293:	return Keyboard.KEY_F4;
			case 294:	return Keyboard.KEY_F5;
			case 295:	return Keyboard.KEY_F6;
			case 296:	return Keyboard.KEY_F7;
			case 297:	return Keyboard.KEY_F8;
			case 298:	return Keyboard.KEY_F9;
			case 299:	return Keyboard.KEY_F10;
			case 300:	return Keyboard.KEY_F11;
			case 301:	return Keyboard.KEY_F12;
			case 302:	return Keyboard.KEY_F13;
			case 303:	return Keyboard.KEY_F14;
			case 304:	return Keyboard.KEY_F15;
			case 305:	return Keyboard.KEY_F16;
			case 306:	return Keyboard.KEY_F17;
			case 307:	return Keyboard.KEY_F18;
			case 308:	return Keyboard.KEY_F19;
			case 320:	return Keyboard.KEY_NUMPAD0;
			case 321:	return Keyboard.KEY_NUMPAD1;
			case 322:	return Keyboard.KEY_NUMPAD2;
			case 323:	return Keyboard.KEY_NUMPAD3;
			case 324:	return Keyboard.KEY_NUMPAD4;
			case 325:	return Keyboard.KEY_NUMPAD5;
			case 326:	return Keyboard.KEY_NUMPAD6;
			case 327:	return Keyboard.KEY_NUMPAD7;
			case 328:	return Keyboard.KEY_NUMPAD8;
			case 329:	return Keyboard.KEY_NUMPAD9;
			case 330:	return Keyboard.KEY_DECIMAL;
			case 331:	return Keyboard.KEY_DIVIDE;
			case 332:	return Keyboard.KEY_MULTIPLY;
			case 333:	return Keyboard.KEY_SUBTRACT;
			case 334:	return Keyboard.KEY_ADD;
			case 335:	return Keyboard.KEY_NUMPADENTER;
			case 336:	return Keyboard.KEY_NUMPADEQUALS;
			case 340:	return Keyboard.KEY_LSHIFT;
			case 341:	return Keyboard.KEY_LCONTROL;
			case 342:	return Keyboard.KEY_LMENU;
			case 343:	return Keyboard.KEY_LMETA;
			case 344:	return Keyboard.KEY_RSHIFT;
			case 345:	return Keyboard.KEY_RCONTROL;
			case 346:	return Keyboard.KEY_RMENU;
			case 347:	return Keyboard.KEY_RMETA;
		}

		return Keyboard.KEY_NONE;
	}

	@Overwrite(remap = false)
	private boolean isKeyHeld(int code) {
		return Keyboard.isKeyDown(code);
	}

	@Overwrite(remap = false)
	private void enableRepeatEvents(boolean value) {
		Keyboard.enableRepeatEvents(value);
	}

	@Overwrite(remap = false)
	public boolean isMouseButtonDown(int button) {
		return Mouse.isButtonDown(button);
	}

}
