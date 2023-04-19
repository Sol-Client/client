/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.solclient.client.mod.impl.hud.chat;

import java.util.*;

import com.google.gson.annotations.Expose;
import com.mojang.blaze3d.platform.GlStateManager;

import io.github.solclient.client.*;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.*;
import io.github.solclient.client.mod.impl.*;
import io.github.solclient.client.mod.impl.api.chat.ChatApiMod;
import io.github.solclient.client.mod.impl.core.mixins.client.ChatHudAccessor;
import io.github.solclient.client.mod.option.annotation.*;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.util.GlobalConstants;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class ChatMod extends SolClientHudMod {

	private static final float ANIMATION_MULTIPLIER = 0.5F;

	public static boolean enabled;
	public static ChatMod instance;

	@Expose
	@Option
	public boolean preventClose = true;
	@Expose
	@Option
	private boolean smooth = true; // Smooth, man!

	private int lastAnimatedOffset;
	private int animatedOffset;

	@Expose
	@Option
	public boolean infiniteChat = true;

	@Option
	public KeyBinding peekKey = new KeyBinding(getTranslationKey("peek"), 0, GlobalConstants.KEY_CATEGORY);
	private boolean wasPeeking;
	private boolean hasScrollbar;

	@Expose
	@Option
	public ChatVisibility visibility = ChatVisibility.SHOWN;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean background = true;
	@Expose
	@ColourKey(ColourKey.BACKGROUND_COLOUR)
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private Colour backgroundColour = new Colour(0, 0, 0, 127);
	@Expose
	@Option
	private Colour defaultTextColour = Colour.WHITE;
	@Expose
	@Option(translationKey = SolClientSimpleHudMod.TRANSLATION_KEY)
	private boolean shadow = true;
	@Expose
	@Option
	public boolean colours = true;
	@Expose
	@Option
	@Slider(min = 40, max = 320, step = 1)
	public float width = 320;
	@Expose
	@Option
	@Slider(min = 20, max = 180, step = 1)
	public float closedHeight = 90;
	@Expose
	@Option
	@Slider(min = 20, max = 180, step = 1)
	public float openHeight = 180;
	@Expose
	@Option
	public boolean links = true;
	@Expose
	@Option
	public boolean promptLinks = true;

	@Expose
	@Option
	private boolean chatFilter = true;
	// @formatter:off
	@Option
	@TextFile(value = "chat-filter.txt", header =
			"# List words on each line for them to be blocked.\n" +
			"# The chat mod and chat filter must be enabled for this to work.\n" +
			"# Any lines starting with \"#\" will be ignored.")
	private String filteredWordsContent;
	// @formatter:on
	private List<String> filteredWords = new ArrayList<>();

	private int previousChatSize;
	private SymbolsButton symbolsButton;

	@Override
	public void init() {
		super.init();
		instance = this;
	}

	@Override
	public void lateInit() {
		super.lateInit();

		symbolsButton = new SymbolsButton(this);

		if (enabled)
			ChatApiMod.instance.registerButton(symbolsButton);
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;

		if (symbolsButton != null)
			ChatApiMod.instance.registerButton(symbolsButton);

		if (mc.world != null)
			mc.inGameHud.getChatHud().reset();
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
		ChatApiMod.instance.unregisterButton(symbolsButton);

		if (mc.world != null)
			mc.inGameHud.getChatHud().reset();
	}

	@Override
	public void onFileUpdate(String fieldName) {
		super.onFileUpdate(fieldName);

		if (fieldName.equals(getTranslationKey("option.filteredWordsContent"))) {
			filteredWords = new ArrayList<>(Arrays.asList(filteredWordsContent.split("\\r?\\n"))); // https://stackoverflow.com/a/454913
			filteredWords.removeIf((word) -> word.isEmpty() || word.startsWith("#"));
		}
	}

	@Override
	public boolean onOptionChange(String key, Object value) {
		if (key.equals("closedHeight") || key.equals("openHeight") || key.equals("width") || key.equals("enabled")
				|| key.equals("scale")) {
			mc.inGameHud.getChatHud().reset();
		}
		return super.onOptionChange(key, value);
	}

	@EventHandler
	public void onScroll(ScrollEvent event) {
		// Arrow key scrolling isn't implemented for various reasons, but nobody cares
		// anyway.

		if (hasScrollbar && peekKey.isPressed() && event.amount != 0) {
			int amount = 1;

			if (event.amount < 0) {
				amount = -amount;
			}

			if (!Screen.hasShiftDown())
				amount *= 7;

			mc.inGameHud.getChatHud().scroll(amount);

			event.cancelled = true;
		}
	}

	@EventHandler
	@SuppressWarnings("unchecked")
	public void onTick(PostTickEvent event) {
		if (!peekKey.isPressed() && wasPeeking)
			mc.inGameHud.getChatHud().resetScroll();

		wasPeeking = peekKey.isPressed();

		if (smooth && !mc.isPaused()) {
			lastAnimatedOffset = animatedOffset;

			animatedOffset *= ANIMATION_MULTIPLIER;

			for (ChatAnimationData line : (Iterable<ChatAnimationData>) (Object) (((ChatHudAccessor) mc.inGameHud
					.getChatHud()).getVisibleMessages())) {
				line.setLastTransparency(line.getTransparency());
				line.setTransparency(line.getTransparency() * ANIMATION_MULTIPLIER);
			}
		}
	}

	@EventHandler
	public void onReceiveChatMessage(ReceiveChatMessageEvent event) {
		if (!chatFilter) {
			return;
		}

		String message = strip(event.message);
		for (String word : filteredWords) {
			word = strip(word);

			if (message.equals(word) || message.startsWith(word + " ") || message.endsWith(" " + word)
					|| message.contains(" " + word + " ")) {
				event.cancelled = true;
				return;
			}
		}
	}

	private static String strip(String message) {
		return message.toLowerCase().codePoints().filter(point -> Character.isLetter(point) || Character.isWhitespace(point))
				.mapToObj((codePoint) -> (Character) (char) codePoint)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}

	@EventHandler
	public void onChatRender(ChatRenderEvent event) {
		event.cancelled = true;
		ChatHudAccessor accessor = ((ChatHudAccessor) event.chat);

		if (visibility != ChatVisibility.HIDDEN) {
			int linesCount = event.chat.getVisibleLineCount();
			boolean open = false;
			int j = 0;
			int drawnLinesCount = accessor.getVisibleMessages().size();

			if (drawnLinesCount > 0) {
				if (event.chat.isChatFocused())
					open = true;

				float f1 = getScale();
				int l = MathHelper.ceil(event.chat.getWidth() / f1);
				GlStateManager.pushMatrix();
				GlStateManager.translate(2, 20, 0);
				GlStateManager.scale(f1, f1, 1);

				if (previousChatSize < accessor.getVisibleMessages().size()) {
					animatedOffset = 9;
					lastAnimatedOffset = 9;
				}

				if (smooth && !(event.chat.isChatFocused() && accessor.getScrolledLines() > 0)) {
					float calculatedOffset = lastAnimatedOffset
							+ (animatedOffset - lastAnimatedOffset) * event.partialTicks;

					GlStateManager.translate(0, calculatedOffset, 0);
				}

				for (int i = 0; i + accessor.getScrolledLines() < accessor.getVisibleMessages().size()
						&& i < linesCount; ++i) {
					ChatHudLine line = accessor.getVisibleMessages().get(i + accessor.getScrolledLines());

					if (line != null) {
						int update = event.ticks - line.getCreationTick();

						if (open || update < 200) {
							double percent = update / 200.0D;
							percent = 1.0D - percent;
							percent = percent * 10.0D;
							percent = MathHelper.clamp(percent, 0.0D, 1.0D);
							percent = percent * percent;

							if (open) {
								percent = 1;
							}

							double percentFG = percent;

							if (smooth) {
								ChatAnimationData data = ((ChatAnimationData) line);

								if (data.getTransparency() != 0) {
									float calculatedTransparency = data.getLastTransparency()
											+ (data.getTransparency() - data.getLastTransparency())
													* event.partialTicks;
									percentFG *= (1 - calculatedTransparency);
								}
							}

							++j;

							if (percent > 0.05F) {
								int i2 = 0;
								int j2 = -i * 9;

								if (background)
									DrawableHelper.fill(i2 - 2, j2 - 9, i2 + l + 4, j2, backgroundColour
											.withAlpha((int) (backgroundColour.getAlpha() * percent)).getValue());

								String formattedText = line.getText().asFormattedString();
								GlStateManager.enableBlend();

								if (percentFG > 0.05F) {
									mc.textRenderer.draw(colours ? formattedText : Formatting.strip(formattedText), i2,
											j2 - 8,
											defaultTextColour
													.withAlpha((int) (defaultTextColour.getAlpha() * percentFG))
													.getValue(),
											shadow);
								}
							}
						}
					}
				}

				if (open) {
					int k2 = mc.textRenderer.fontHeight;
					GlStateManager.translate(-3.0F, 0.0F, 0.0F);
					int l2 = drawnLinesCount * k2 + drawnLinesCount;
					int i3 = j * k2 + j;
					int j3 = accessor.getScrolledLines() * i3 / drawnLinesCount;
					int k1 = i3 * i3 / l2;

					if (l2 != i3) {
						hasScrollbar = true;

						int k3 = j3 > 0 ? 170 : 96;
						int l3 = accessor.getHasUnreadNewMessages() ? 13382451 : 3355562;
						DrawableHelper.fill(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
						DrawableHelper.fill(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
					} else {
						hasScrollbar = false;
					}
				}

				GlStateManager.popMatrix();
			} else {
				hasScrollbar = false;
			}
		} else {
			hasScrollbar = false;
		}

		previousChatSize = accessor.getVisibleMessages().size();
	}

}
