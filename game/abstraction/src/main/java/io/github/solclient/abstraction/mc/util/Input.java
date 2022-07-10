package io.github.solclient.abstraction.mc.util;

import io.github.solclient.abstraction.Helper;
import io.github.solclient.abstraction.mc.RuntimeDetermined;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Input {

	public final int SPACE = 32;
	public final int APOSTROPHE = 39;
	public final int COMMA = 44;
	public final int MINUS = 45;
	public final int PERIOD = 46;
	public final int SLASH = 47;
	public final int N0 = 48;
	public final int N1 = 49;
	public final int N2 = 50;
	public final int N3 = 51;
	public final int N4 = 52;
	public final int N5 = 53;
	public final int N6 = 54;
	public final int N7 = 55;
	public final int N8 = 56;
	public final int N9 = 57;
	public final int SEMICOLON = 59;
	public final int EQUAL = 61;
	public final int A = 65;
	public final int B = 66;
	public final int C = 67;
	public final int D = 68;
	public final int E = 69;
	public final int F = 70;
	public final int G = 71;
	public final int H = 72;
	public final int I = 73;
	public final int J = 74;
	public final int K = 75;
	public final int L = 76;
	public final int M = 77;
	public final int N = 78;
	public final int O = 79;
	public final int P = 80;
	public final int Q = 81;
	public final int R = 82;
	public final int S = 83;
	public final int T = 84;
	public final int U = 85;
	public final int V = 86;
	public final int W = 87;
	public final int X = 88;
	public final int Y = 89;
	public final int Z = 90;
	public final int LEFT_BRACKET = 91;
	public final int BACKSLASH = 92;
	public final int RIGHT_BRACKET = 93;
	public final int GRAVE_ACCENT = 96;
	public final int WORLD_1 = 161;
	public final int WORLD_2 = 162;
	public final int ESCAPE = 256;
	public final int ENTER = 257;
	public final int TAB = 258;
	public final int BACKSPACE = 259;
	public final int INSERT = 260;
	public final int DELETE = 261;
	public final int RIGHT = 262;
	public final int LEFT = 263;
	public final int DOWN = 264;
	public final int UP = 265;
	public final int PAGE_UP = 266;
	public final int PAGE_DOWN = 267;
	public final int HOME = 268;
	public final int END = 269;
	public final int CAPS_LOCK = 280;
	public final int SCROLL_LOCK = 281;
	public final int NUM_LOCK = 282;
	public final int PRINT_SCREEN = 283;
	public final int PAUSE = 284;
	public final int F1 = 290;
	public final int F2 = 291;
	public final int F3 = 292;
	public final int F4 = 293;
	public final int F5 = 294;
	public final int F6 = 295;
	public final int F7 = 296;
	public final int F8 = 297;
	public final int F9 = 298;
	public final int F10 = 299;
	public final int F11 = 300;
	public final int F12 = 301;
	public final int F13 = 302;
	public final int F14 = 303;
	public final int F15 = 304;
	public final int F16 = 305;
	public final int F17 = 306;
	public final int F18 = 307;
	public final int F19 = 308;
	public final int F20 = 309;
	public final int F21 = 310;
	public final int F22 = 311;
	public final int F23 = 312;
	public final int F24 = 313;
	public final int F25 = 314;
	public final int KP_0 = 320;
	public final int KP_1 = 321;
	public final int KP_2 = 322;
	public final int KP_3 = 323;
	public final int KP_4 = 324;
	public final int KP_5 = 325;
	public final int KP_6 = 326;
	public final int KP_7 = 327;
	public final int KP_8 = 328;
	public final int KP_9 = 329;
	public final int KP_DECIMAL = 330;
	public final int KP_DIVIDE = 331;
	public final int KP_MULTIPLY = 332;
	public final int KP_SUBTRACT = 333;
	public final int KP_ADD = 334;
	public final int KP_ENTER = 335;
	public final int KP_EQUAL = 336;
	public final int LEFT_SHIFT = 340;
	public final int LEFT_CONTROL = 341;
	public final int LEFT_ALT = 342;
	public final int LEFT_SUPER = 343;
	public final int RIGHT_SHIFT = 344;
	public final int RIGHT_CONTROL = 345;
	public final int RIGHT_ALT = 346;
	public final int RIGHT_SUPER = 347;
	public final int MENU = 348;
	public final int LAST = MENU;
	public final int NONE = RuntimeDetermined.value();

	public boolean isKeyHeld(int code) {
		throw new UnsupportedOperationException();
	}

	@Helper
	public static boolean isCtrlHeld() {
		return isKeyHeld(LEFT_CONTROL);
	}

	@Helper
	public static boolean isShiftHeld() {
		return isKeyHeld(LEFT_SHIFT);
	}

	// Should do nothing on 1.18
	public static void enableRepeatEvents(boolean value) {
		throw new UnsupportedOperationException();
	}

	public static boolean isMouseButtonHeld(int code) {
		throw new UnsupportedOperationException();
	}

}
