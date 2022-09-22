package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.lang.I18n;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.platform.mc.util.MinecraftUtil;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

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
		if(text.length() > 32) {
			text = text.substring(0, 32);
		}

		boolean different = !this.text.equals(text);

		this.text = text;

		cursor = clamp(cursor);
		selectionEnd = clamp(selectionEnd);

		if(different) {
			ticks = 0;
			if(flush) {
				flush();
			}
		}
	}

	private int clamp(int value) {
		return Utils.clamp(value, 0, text.length());
	}

	@Override
	public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
		if(!inside) {
			if(focused) {
				flush();
			}

			focused = false;
		}

		return super.mouseClickedAnywhere(info, button, inside, processed);
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if(!focused) {
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

		if(centred) {
			textOffset = (getBounds().getWidth() / 2) - (font.getTextWidth(text) / 2);
		}

		if(hasIcon) {
			textOffset += 10;
		}

		if(selectionEnd != cursor) {
			int start = Math.min(selectionEnd, cursor);
			int end = Math.max(selectionEnd, cursor);

			float selectionWidth = font.getTextWidth(text.substring(start, end));
			float offset = font.getTextWidth(text.substring(0, start));

			DrawableHelper.fillRect(textOffset + offset, 0, textOffset + offset + selectionWidth, 10,
					Colour.BLUE.getValue());
		}

		boolean hasPlaceholder = placeholder != null && text.isEmpty() && !focused;

		font.render(hasPlaceholder ? I18n.translate(placeholder) : text, textOffset,
				SolClientConfig.INSTANCE.fancyFont ? 0 : 1, hasPlaceholder ? 0x888888 : -1);

		if(focused && ticks / 12 % 2 == 0) {
			float relativeCursorPosition = font.getTextWidth(text.substring(0, cursor));
			DrawableHelper.fillRect(textOffset + relativeCursorPosition, 0, textOffset + relativeCursorPosition + 1, 10,
					-1);
		}

		new Rectangle(0, getBounds().getHeight() - 1, getBounds().getWidth(), 1).fill(colour.get(this, null));
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return new Rectangle(0, 0, width, 12);
	}

	@Override
	public boolean keyPressed(ComponentRenderInfo info, int code, int scancode, int mods) {
		if(!focused) {
			return false;
		}

		if(Input.isSelectAll(code)) {
			setCursorPositionEnd();
			setSelectionPosition(0);
			return true;
		}

		if(Input.isCopy(code)) {
			MinecraftUtil.copy(getSelectedText());
			return true;
		}

		if(Input.isPaste(code)) {
			if(enabled) {
				writeText(MinecraftUtil.getClipboardContent());
			}

			return true;
		}

		if(Input.isCut(code)) {
			MinecraftUtil.copy(getSelectedText());

			if(enabled) {
				writeText("");
			}

			return true;
		}

		if(code == Input.BACKSPACE) {
			if((mods & Input.COMMAND_MODIFIER) != 0) {
				if(enabled) {
					deleteWords(-1);
				}
			}
			else if(enabled) {
				deleteFromCursor(-1);
			}
			return true;
		}
		else if(code == Input.HOME) {
			if((mods & Input.SHIFT_MODIFIER) != 0) {
				setSelectionPosition(0);
			}
			else {
				setCursorPosition(0);
			}
			return true;
		}
		else if(code == Input.LEFT) {
			if((mods & Input.SHIFT_MODIFIER) != 0) {
				if((mods & Input.COMMAND_MODIFIER) != 0) {
					setSelectionPosition(getWordFromPosition(-1, selectionEnd));
				}
				else {
					setSelectionPosition(selectionEnd - 1);
				}
			}
			else if((mods & Input.COMMAND_MODIFIER) != 0) {
				setCursorPosition(getWordFromCursor(-1));
			}
			else {
				moveCursorBy(-1);
			}
			return true;
		}
		else if(code == Input.RIGHT) {
			if((mods & Input.SHIFT_MODIFIER) != 0) {
				if((mods & Input.COMMAND_MODIFIER) != 0) {
					setSelectionPosition(getWordFromPosition(1, selectionEnd));
				}
				else {
					setSelectionPosition(selectionEnd + 1);
				}
			}
			else if((mods & Input.COMMAND_MODIFIER) != 0) {
				setCursorPosition(getWordFromCursor(1));
			}
			else {
				moveCursorBy(1);
			}
			return true;
		}
		else if(code == Input.END) {
			if((mods & Input.SHIFT_MODIFIER) != 0) {
				setSelectionPosition(text.length());
			}
			else {
				setCursorPositionEnd();
			}
			return true;
		}
		else if(code == Input.DELETE) {
			if((mods & Input.COMMAND_MODIFIER) != 0) {
				if(enabled) {
					deleteWords(1);
				}
			}
			else if(enabled) {
				deleteFromCursor(1);
			}
			return true;
		}
		else if(code == Input.ENTER) {
			flush();
			return true;
		}

		return false;
	}

	@Override
	public boolean characterTyped(ComponentRenderInfo info, char character) {
		if(MinecraftUtil.isAllowedInTextBox(character)) {
			if(enabled) {
				writeText(Character.toString(character));
			}

			return true;
		}

		return false;
	}

	private int getWordFromCursor(int word) {
		return getWordFromPosition(word, cursor);
	}

	private int getWordFromPosition(int word, int from) {
		boolean negative = word < 0;
		int positive = Math.abs(word);

		for(int i = 0; i < positive; ++i) {
			if(!negative) {
				int length = text.length();

				from = text.indexOf(32, from);

				if(from == -1) {
					from = length;
				}
				else {
					while(from < length && text.charAt(from) == 32) {
						++from;
					}
				}
			}
			else {
				while(from > 0 && text.charAt(from - 1) == 32) {
					--from;
				}

				while(from > 0 && text.charAt(from - 1) != 32) {
					--from;
				}
			}
		}

		return from;
	}

	private void deleteFromCursor(int move) {
		if(text.length() != 0) {
			if(selectionEnd != cursor) {
				writeText("");
			}
			else {
				boolean negativeMovement = move < 0;
				int start = negativeMovement ? cursor + move : cursor;
				int end = negativeMovement ? cursor : cursor + move;
				String result = "";

				if(start >= 0) {
					result = text.substring(0, start);
				}

				if(end < text.length()) {
					result = result + text.substring(end);
				}

				if(negativeMovement) {
					moveCursorBy(move);
				}

				setTextInternal(result);
			}
		}
	}

	private void deleteWords(int words) {
		if(text.length() != 0) {
			if(selectionEnd != cursor) {
				writeText("");
			}
			else {
				deleteFromCursor(getWordFromCursor(words) - cursor);
			}
		}
	}

	private void writeText(String text) {
		String result = "";
		String filtered = MinecraftUtil.filterTextBoxInput(text);
		int start = Math.min(cursor, selectionEnd);
		int end = Math.max(cursor, selectionEnd);
		int k = 32 - text.length() - (start - end);
		int l;

		if(this.text.length() > 0) {
			result = result + this.text.substring(0, start);
		}

		if(k < filtered.length()) {
			result = result + filtered.substring(0, k);
			l = k;
		}
		else {
			result = result + filtered;
			l = filtered.length();
		}

		if(this.text.length() > 0 && end < this.text.length()) {
			result = result + this.text.substring(end);
		}

		setTextInternal(result);
		moveCursorBy(start - selectionEnd + l);
	}

	private void moveCursorBy(int by) {
		setCursorPosition(selectionEnd + by);
	}

	private String getSelectedText() {
		int start = Math.min(cursor, selectionEnd);
		int end = Math.max(cursor, selectionEnd);
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
		int length = text.length();

		if(position > length) {
			position = length;
		}

		if(position < 0) {
			position = 0;
		}

		selectionEnd = position;
	}

	public TextFieldComponent onUpdate(Predicate<String> onUpdate) {
		this.onUpdate = onUpdate;
		return this;
	}

	public void flush() {
		if(text.equals(lastText)) {
			return;
		}

		if(onUpdate == null) {
			return;
		}

		if(!onUpdate.test(text)) {
			setTextInternal(lastText);
			setCursorPositionEnd();
		}
		else {
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
