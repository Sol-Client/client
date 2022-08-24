package io.github.solclient.client.platform.mc.util;

import io.github.solclient.client.platform.Helper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Input {

	public final int SPACE = getKeyCode(32);
	public final int APOSTROPHE = getKeyCode(39);
	public final int COMMA = getKeyCode(44);
	public final int MINUS = getKeyCode(45);
	public final int PERIOD = getKeyCode(46);
	public final int SLASH = getKeyCode(47);
	public final int _0 = getKeyCode(48);
	public final int _1 = getKeyCode(49);
	public final int _2 = getKeyCode(50);
	public final int _3 = getKeyCode(51);
	public final int _4 = getKeyCode(52);
	public final int _5 = getKeyCode(53);
	public final int _6 = getKeyCode(54);
	public final int _7 = getKeyCode(55);
	public final int _8 = getKeyCode(56);
	public final int _9 = getKeyCode(57);
	public final int SEMICOLON = getKeyCode(59);
	public final int EQUAL = getKeyCode(61);
	public final int A = getKeyCode(65);
	public final int B = getKeyCode(66);
	public final int C = getKeyCode(67);
	public final int D = getKeyCode(68);
	public final int E = getKeyCode(69);
	public final int F = getKeyCode(70);
	public final int G = getKeyCode(71);
	public final int H = getKeyCode(72);
	public final int I = getKeyCode(73);
	public final int J = getKeyCode(74);
	public final int K = getKeyCode(75);
	public final int L = getKeyCode(76);
	public final int M = getKeyCode(77);
	public final int N = getKeyCode(78);
	public final int O = getKeyCode(79);
	public final int P = getKeyCode(80);
	public final int Q = getKeyCode(81);
	public final int R = getKeyCode(82);
	public final int S = getKeyCode(83);
	public final int T = getKeyCode(84);
	public final int U = getKeyCode(85);
	public final int V = getKeyCode(86);
	public final int W = getKeyCode(87);
	public final int X = getKeyCode(88);
	public final int Y = getKeyCode(89);
	public final int Z = getKeyCode(90);
	public final int LEFT_BRACKET = getKeyCode(91);
	public final int BACKSLASH = getKeyCode(92);
	public final int RIGHT_BRACKET = getKeyCode(93);
	public final int GRAVE_ACCENT = getKeyCode(96);
	public final int WORLD_1 = 161;
	public final int WORLD_2 = 162;
	public final int ESCAPE = getKeyCode(256);
	public final int ENTER = getKeyCode(257);
	public final int TAB = getKeyCode(258);
	public final int BACKSPACE = getKeyCode(259);
	public final int INSERT = getKeyCode(260);
	public final int DELETE = getKeyCode(261);
	public final int RIGHT = getKeyCode(262);
	public final int LEFT = getKeyCode(263);
	public final int DOWN = getKeyCode(264);
	public final int UP = getKeyCode(265);
	public final int PAGE_UP = getKeyCode(266);
	public final int PAGE_DOWN = getKeyCode(267);
	public final int HOME = getKeyCode(268);
	public final int END = getKeyCode(269);
	public final int CAPS_LOCK = getKeyCode(280);
	public final int SCROLL_LOCK = getKeyCode(281);
	public final int NUM_LOCK = getKeyCode(282);
	public final int PRINT_SCREEN = getKeyCode(283);
	public final int PAUSE = getKeyCode(284);
	public final int F1 = getKeyCode(290);
	public final int F2 = getKeyCode(291);
	public final int F3 = getKeyCode(292);
	public final int F4 = getKeyCode(293);
	public final int F5 = getKeyCode(294);
	public final int F6 = getKeyCode(295);
	public final int F7 = getKeyCode(296);
	public final int F8 = getKeyCode(297);
	public final int F9 = getKeyCode(298);
	public final int F10 = getKeyCode(299);
	public final int F11 = getKeyCode(300);
	public final int F12 = getKeyCode(301);
	public final int F13 = getKeyCode(302);
	public final int F14 = getKeyCode(303);
	public final int F15 = getKeyCode(304);
	public final int F16 = getKeyCode(305);
	public final int F17 = getKeyCode(306);
	public final int F18 = getKeyCode(307);
	public final int F19 = getKeyCode(308);
	public final int F20 = getKeyCode(309);
	public final int F21 = getKeyCode(310);
	public final int F22 = getKeyCode(311);
	public final int F23 = getKeyCode(312);
	public final int F24 = getKeyCode(313);
	public final int F25 = getKeyCode(314);
	public final int KP_0 = getKeyCode(320);
	public final int KP_1 = getKeyCode(321);
	public final int KP_2 = getKeyCode(322);
	public final int KP_3 = getKeyCode(323);
	public final int KP_4 = getKeyCode(324);
	public final int KP_5 = getKeyCode(325);
	public final int KP_6 = getKeyCode(326);
	public final int KP_7 = getKeyCode(327);
	public final int KP_8 = getKeyCode(328);
	public final int KP_9 = getKeyCode(329);
	public final int KP_DECIMAL = getKeyCode(330);
	public final int KP_DIVIDE = getKeyCode(331);
	public final int KP_MULTIPLY = getKeyCode(332);
	public final int KP_SUBTRACT = getKeyCode(333);
	public final int KP_ADD = getKeyCode(334);
	public final int KP_ENTER = getKeyCode(335);
	public final int KP_EQUAL = getKeyCode(336);
	public final int LEFT_SHIFT = getKeyCode(340);
	public final int LEFT_CONTROL = getKeyCode(341);
	public final int LEFT_ALT = getKeyCode(342);
	public final int LEFT_SUPER = getKeyCode(343);
	public final int LEFT_COMMAND = MinecraftUtil.isMac() ? LEFT_SUPER : LEFT_CONTROL;
	public final int RIGHT_SHIFT = getKeyCode(344);
	public final int RIGHT_CONTROL = getKeyCode(345);
	public final int RIGHT_ALT = getKeyCode(346);
	public final int RIGHT_SUPER = getKeyCode(347);
	public final int RIGHT_COMMAND = MinecraftUtil.isMac() ? RIGHT_SUPER : RIGHT_CONTROL;
	public final int MENU = getKeyCode(348);
	public final int UNKNOWN = getKeyCode(-1);
	public final int SHIFT_MODIFIER = 1;
	public final int CONTROL_MODIFIER = 2;
	public final int ALT_MODIFIER = 4;
	public final int SUPER_MODIFIER = 8;
	public final int CAPS_LOCK_MODIFIER = 16;
	public final int NUM_LOCK_MODIFIER = 32;
	public final int COMMAND_MODIFIER = MinecraftUtil.isMac() ? SUPER_MODIFIER : CONTROL_MODIFIER;

	/**
	 * 	 the native key code from the GLFW one. Defaults to returning the same
	 * code, but also provides uncertainty that prevents the compiler from getting
	 * too excited.
	 *
	 * @param glfw The GLFW key.
	 * @return The key native to the platform.
	 */
	private int getKeyCode(int glfw) {
		return glfw;
	}

	public boolean isKeyDown(int code) {
		throw new UnsupportedOperationException();
	}

	@Helper
	public boolean isControlDown() {
		return isKeyDown(LEFT_CONTROL) || isKeyDown(RIGHT_CONTROL);
	}

	@Helper
	public boolean isCommandDown() {
		return isKeyDown(LEFT_COMMAND) || isKeyDown(RIGHT_COMMAND);
	}

	@Helper
	public boolean isShiftDown() {
		return isKeyDown(LEFT_SHIFT) || isKeyDown(RIGHT_SHIFT);
	}

	@Helper
	public boolean isAltDown() {
		return isKeyDown(LEFT_ALT) || isKeyDown(RIGHT_ALT);
	}

	// Should do nothing on 1.18
	public void enableRepeatEvents(boolean value) {
	}

	public boolean isMouseButtonDown(int button) {
		throw new UnsupportedOperationException();
	}

	private boolean isCommandCombo(int key, int required) {
		return key == required && isCommandDown() && !isShiftDown() && !isAltDown();
	}

	public boolean isCopy(int key) {
		return isCommandCombo(key, C);
	}

	public boolean isPaste(int key) {
		return isCommandCombo(key, V);
	}

	public boolean isCut(int key) {
		return isCommandCombo(key, X);
	}

	public boolean isSelectAll(int key) {
		return isCommandCombo(key, A);
	}

}
