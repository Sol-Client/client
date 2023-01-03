package io.github.solclient.client.ui.component.impl;

import java.util.Objects;

import org.lwjgl.nanovg.NanoVG;

import com.google.common.base.Predicate;

import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import lombok.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.*;

public class TextFieldComponent extends Component {

	private int width;
	private String lastText = "";
	@Getter
	private String text = "";
	private String placeholder;
	private int cursor;
	@Setter
	@Getter
	private boolean focused;
	private int selectionEnd;
	private boolean enabled = true;
	private final Controller<Colour> colour = new AnimatedColourController(
			(component, defaultColour) -> focused ? Colour.WHITE : Colour.LIGHT_BUTTON);
	private boolean centred;
	private Predicate<String> onUpdate;
	private boolean flush;
	private int ticks;
	private boolean hasIcon;

	public TextFieldComponent(int width, boolean centred) {
		this.width = width;
		this.centred = centred;
	}

	public void setText(String text) {
		lastText = text;
		setTextInternal(text);
	}

	private void setTextInternal(String text) {
		if (text.length() > 32) {
			text = text.substring(0, 32);
		}

		boolean different = this.text != text;

		this.text = text;

		cursor = clamp(cursor);
		selectionEnd = clamp(selectionEnd);

		if (different) {
			ticks = 0;
			if (flush) {
				flush();
			}
		}
	}

	private int clamp(int value) {
		return MathHelper.clamp_int(value, 0, text.length());
	}

	@Override
	public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
		if (!inside) {
			if (focused) {
				flush();
			}

			focused = false;
		}

		return super.mouseClickedAnywhere(info, button, inside, processed);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (!focused) {
			focused = true;
			ticks = 0;
			return true;
		}

		return false;
	}

	@Override
	public void render(ComponentRenderInfo info) {
		super.render(info);

		int textOffset = 2;

		if (centred) {
			textOffset = (int) ((getBounds().getWidth() / 2) - (regularFont.getWidth(nvg, text) / 2));
		}

		if (hasIcon) {
			textOffset += 10;
		}

		if (selectionEnd != cursor) {
			int start = selectionEnd > cursor ? cursor : selectionEnd;
			int end = selectionEnd > cursor ? selectionEnd : cursor;

			float selectionWidth = regularFont.getWidth(nvg, text.substring(start, end));
			float offset = regularFont.getWidth(nvg, text.substring(0, start));

			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgFillColor(nvg, Colour.BLUE.nvg());
			NanoVG.nvgRect(nvg, textOffset + offset, 0, selectionWidth, 10);
			NanoVG.nvgFill(nvg);
		}

		boolean hasPlaceholder = placeholder != null && text.isEmpty() && !focused;

		NanoVG.nvgFillColor(nvg, (hasPlaceholder ? new Colour(0xFF888888) : Colour.WHITE).nvg());
		regularFont.renderString(nvg, hasPlaceholder ? I18n.format(placeholder) : text, textOffset, 0);

		if (focused && ticks / 12 % 2 == 0) {
			float relativeCursorPosition = regularFont.getWidth(nvg, text.substring(0, cursor));
			NanoVG.nvgBeginPath(nvg);
			NanoVG.nvgFillColor(nvg, Colour.WHITE.nvg());
			NanoVG.nvgRect(nvg, textOffset + relativeCursorPosition, 0, 1, 10);
			NanoVG.nvgFill(nvg);
		}

		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, colour.get(this, null).nvg());
		NanoVG.nvgRect(nvg, 0, getBounds().getHeight() - 1, getBounds().getWidth(), 1);
		NanoVG.nvgFill(nvg);
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, width, 12);
	}

	@Override
	public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
		if (!focused) {
			return false;
		}

		if (GuiScreen.isKeyComboCtrlA(keyCode)) {
			setCursorPositionEnd();
			setSelectionPosition(0);
			return true;
		}

		if (GuiScreen.isKeyComboCtrlC(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());
			return true;
		}

		if (GuiScreen.isKeyComboCtrlV(keyCode)) {
			if (enabled) {
				writeText(GuiScreen.getClipboardString());
			}

			return true;
		}

		if (GuiScreen.isKeyComboCtrlX(keyCode)) {
			GuiScreen.setClipboardString(getSelectedText());

			if (enabled) {
				writeText("");
			}

			return true;
		}

		switch (keyCode) {
		case 14:
			if (GuiScreen.isCtrlKeyDown()) {
				if (enabled) {
					deleteWords(-1);
				}
			} else if (enabled) {
				deleteFromCursor(-1);
			}

			return true;

		case 199:
			if (GuiScreen.isShiftKeyDown()) {
				setSelectionPosition(0);
			} else {
				setCursorPosition(0);
			}

			return true;

		case 203:
			if (GuiScreen.isShiftKeyDown()) {
				if (GuiScreen.isCtrlKeyDown()) {
					setSelectionPosition(getWordFromPosition(-1, selectionEnd));
				} else {
					setSelectionPosition(selectionEnd - 1);
				}
			} else if (GuiScreen.isCtrlKeyDown()) {
				setCursorPosition(getWordFromCursor(-1));
			} else {
				moveCursorBy(-1);
			}

			return true;

		case 205:
			if (GuiScreen.isShiftKeyDown()) {
				if (GuiScreen.isCtrlKeyDown()) {
					this.setSelectionPosition(getWordFromPosition(1, selectionEnd));
				} else {
					this.setSelectionPosition(selectionEnd + 1);
				}
			} else if (GuiScreen.isCtrlKeyDown()) {
				setCursorPosition(getWordFromCursor(1));
			} else {
				moveCursorBy(1);
			}

			return true;

		case 207:
			if (GuiScreen.isShiftKeyDown()) {
				setSelectionPosition(text.length());
			} else {
				setCursorPositionEnd();
			}

			return true;

		case 211:
			if (GuiScreen.isCtrlKeyDown()) {
				if (enabled) {
					deleteWords(1);
				}
			} else if (enabled) {
				deleteFromCursor(1);
			}

			return true;

		case 28:
			flush();
			return true;

		default:
			if (ChatAllowedCharacters.isAllowedCharacter(character)) {
				if (enabled) {
					writeText(Character.toString(character));
				}

				return true;
			} else {
				return false;
			}
		}
	}

	private int getWordFromCursor(int word) {
		return getWordFromPosition(word, cursor);
	}

	private int getWordFromPosition(int word, int from) {
		boolean negative = word < 0;
		int positive = Math.abs(word);

		for (int i = 0; i < positive; ++i) {
			if (!negative) {
				int length = text.length();

				from = text.indexOf(32, from);

				if (from == -1) {
					from = length;
				} else {
					while (from < length && this.text.charAt(from) == 32) {
						++from;
					}
				}
			} else {
				while (from > 0 && this.text.charAt(from - 1) == 32) {
					--from;
				}

				while (from > 0 && this.text.charAt(from - 1) != 32) {
					--from;
				}
			}
		}

		return from;
	}

	private void deleteFromCursor(int move) {
		if (text.length() != 0) {
			if (selectionEnd != cursor) {
				this.writeText("");
			} else {
				boolean negativeMovement = move < 0;
				int start = negativeMovement ? cursor + move : cursor;
				int end = negativeMovement ? cursor : cursor + move;
				String result = "";

				if (start >= 0) {
					result = text.substring(0, start);
				}

				if (end < this.text.length()) {
					result = result + text.substring(end);
				}

				if (negativeMovement) {
					moveCursorBy(move);
				}

				setTextInternal(result);
			}
		}
	}

	private void deleteWords(int words) {
		if (text.length() != 0) {
			if (selectionEnd != cursor) {
				writeText("");
			} else {
				deleteFromCursor(getWordFromCursor(words) - cursor);
			}
		}
	}

	private void writeText(String text) {
		String result = "";
		String filtered = ChatAllowedCharacters.filterAllowedCharacters(text);
		int start = cursor < selectionEnd ? cursor : selectionEnd;
		int end = cursor < selectionEnd ? selectionEnd : cursor;
		int k = 32 - text.length() - (start - end);
		int l = 0;

		if (this.text.length() > 0) {
			result = result + this.text.substring(0, start);
		}

		if (k < filtered.length()) {
			result = result + filtered.substring(0, k);
			l = k;
		} else {
			result = result + filtered;
			l = filtered.length();
		}

		if (this.text.length() > 0 && end < this.text.length()) {
			result = result + this.text.substring(end);
		}

		setTextInternal(result);
		moveCursorBy(start - this.selectionEnd + l);
	}

	private void moveCursorBy(int by) {
		setCursorPosition(this.selectionEnd + by);
	}

	private String getSelectedText() {
		int start = cursor < selectionEnd ? cursor : selectionEnd;
		int end = cursor < selectionEnd ? selectionEnd : cursor;
		return text.substring(start, end);
	}

	public void setCursorPositionEnd() {
		setCursorPosition(text.length());
	}

	private void setCursorPosition(int position) {
		cursor = position;
		cursor = clamp(cursor);
		setSelectionPosition(cursor);
	}

	private void setSelectionPosition(int position) {
		int length = this.text.length();

		if (position > length) {
			position = length;
		}

		if (position < 0) {
			position = 0;
		}

		selectionEnd = position;
	}

	public TextFieldComponent onUpdate(Predicate<String> onUpdate) {
		this.onUpdate = onUpdate;
		return this;
	}

	public void flush() {
		if (Objects.equals(text, lastText)) {
			return;
		}

		if (onUpdate == null) {
			return;
		}

		if (!onUpdate.apply(text)) {
			setTextInternal(lastText);
			setCursorPositionEnd();
		} else {
			lastText = text;
		}
	}

	public TextFieldComponent autoFlush() {
		flush = true;
		return this;
	}

	public TextFieldComponent placeholder(String placeholder) {
		this.placeholder = placeholder;
		return this;
	}

	public TextFieldComponent withIcon(String name) {
		hasIcon = true;
		add(new ScaledIconComponent(name, 16, 16),
				new AlignedBoundsController(Alignment.START, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));
		return this;
	}

	@Override
	public void tick() {
		super.tick();
		ticks++;
	}

}
