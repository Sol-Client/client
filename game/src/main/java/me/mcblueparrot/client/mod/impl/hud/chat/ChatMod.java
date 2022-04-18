package me.mcblueparrot.client.mod.impl.hud.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.event.EventHandler;
import me.mcblueparrot.client.event.impl.ChatRenderEvent;
import me.mcblueparrot.client.event.impl.PostTickEvent;
import me.mcblueparrot.client.event.impl.ReceiveChatMessageEvent;
import me.mcblueparrot.client.event.impl.ScrollEvent;
import me.mcblueparrot.client.mod.annotation.FileOption;
import me.mcblueparrot.client.mod.annotation.Option;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mod.hud.HudMod;
import me.mcblueparrot.client.mod.hud.SimpleHudMod;
import me.mcblueparrot.client.ui.element.ChatButton;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessGuiChat;
import me.mcblueparrot.client.util.access.AccessGuiNewChat;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ChatMod extends HudMod {

	public static boolean enabled;
	public static ChatMod instance;

	@Expose
	@Option
	public boolean preventClose = true;
	@Expose
	@Option
	private boolean smooth = true; // Smooth, man!

	private static final float ANIMATION_MULTIPLIER = 0.5F;
	private int lastAnimatedOffset;
	private int animatedOffset;

	@Expose
	@Option
	public boolean infiniteChat = true;

	@Option
	public KeyBinding peekKey = new KeyBinding(getTranslationKey() + ".peek", 0, Client.KEY_CATEGORY);
	private boolean wasPeeking;
	private boolean hasScrollbar;

	@Expose
	@Option
	public ChatVisibility visibility = ChatVisibility.SHOWN;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean background = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private Colour backgroundColour = new Colour(0, 0, 0, 127);
	@Expose
	@Option
	private Colour defaultTextColour = Colour.WHITE;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
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
	@Option
	@FileOption(file = "Chat Filter.txt", header = "# List words on each line for them to be blocked.\n"
			+ "# The chat mod and chat filter must be enabled for this to work.\n"
			+ "# This may not work well for all languages.\n"
			+ "# Any lines starting with \"#\" will be ignored.")
	private String filteredWordsContent;
	private List<String> filteredWords = new ArrayList<>();

	private int previousChatSize;
	private SymbolsButton symbolsButton;

	@Override
	public String getId() {
		return "chat";
	}

	@Override
	public void onRegister() {
		super.onRegister();

		instance = this;
		Client.INSTANCE.registerKeyBinding(peekKey);
	}

	@Override
	public void postStart() {
		super.postStart();

		symbolsButton = new SymbolsButton(this);

		if(enabled) {
			Client.INSTANCE.registerChatButton(symbolsButton);
		}
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;

		if(symbolsButton != null) {
			Client.INSTANCE.registerChatButton(symbolsButton);
		}

		if(mc.theWorld != null) {
			mc.ingameGUI.getChatGUI().refreshChat();
		}
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
		Client.INSTANCE.unregisterChatButton(symbolsButton);

		if(mc.theWorld != null) {
			mc.ingameGUI.getChatGUI().refreshChat();
		}
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@Override
	public void onFileUpdate(String fieldName) {
		super.onFileUpdate(fieldName);

		if(fieldName.equals("filteredWordsContent")) {
			filteredWords = new ArrayList<>(Arrays.asList(filteredWordsContent.split("\\r?\\n"))); // https://stackoverflow.com/a/454913
			filteredWords.removeIf((word) -> word.isEmpty() || word.startsWith("#"));
		}
	}

	@Override
	public boolean onOptionChange(String key, Object value) {
		if(key.equals("closedHeight") || key.equals("openHeight")
				|| key.equals("width") || key.equals("enabled")
				|| key.equals("scale")) {
			mc.ingameGUI.getChatGUI().refreshChat();
		}
		return super.onOptionChange(key, value);
	}

	@EventHandler
	public void onScroll(ScrollEvent event) {
		// Arrow key scrolling isn't implemented for various reasons, but nobody cares anyway.

		if(hasScrollbar && peekKey.isKeyDown() && event.amount != 0) {
			int amount = 1;

			if(event.amount < 0) {
				amount = -amount;
			}

			if(!GuiScreen.isShiftKeyDown()) {
				amount *= 7;
			}

			mc.ingameGUI.getChatGUI().scroll(amount);

			event.cancelled = true;
		}
	}

	@SuppressWarnings("unchecked")
	@EventHandler
	public void onTick(PostTickEvent event) {
		if(!peekKey.isKeyDown() && wasPeeking) {
			mc.ingameGUI.getChatGUI().resetScroll();
		}

		wasPeeking = peekKey.isKeyDown();

		if(smooth && !mc.isGamePaused()) {
			lastAnimatedOffset = animatedOffset;

			animatedOffset *= ANIMATION_MULTIPLIER;

			for(ChatAnimationData line : (Iterable<ChatAnimationData>) (Object) (((AccessGuiNewChat) mc.ingameGUI
					.getChatGUI()).getDrawnChatLines())) {
				line.setLastTransparency(line.getTransparency());
				line.setTransparency(line.getTransparency() * ANIMATION_MULTIPLIER);
			}
		}
	}

	@EventHandler
	public void onReceiveChatMessage(ReceiveChatMessageEvent event) {
		if(!chatFilter) {
			return;
		}

		// Primarily focused on English text, as all non-ascii characters are stripped.
		String message = strip(event.message);

		for(String word : filteredWords) {
			word = strip(word);

			if(message.equals(word) || message.startsWith(word + " ")
					|| message.endsWith(" " + word) || message.contains(" " + word + " ")) {
				event.cancelled = true;
				return;
			}
		}
	}

	private static String strip(String message) {
		return EnumChatFormatting.getTextWithoutFormattingCodes(message).toLowerCase().replaceAll("[^a-z ]", "");
	}

	@EventHandler
	public void onChatRender(ChatRenderEvent event) {
		event.cancelled = true;
		AccessGuiNewChat accessor = ((AccessGuiNewChat) event.chat);

		if(visibility != ChatVisibility.HIDDEN) {
			int linesCount = event.chat.getLineCount();
			boolean open = false;
			int j = 0;
			int drawnLinesCount = accessor.getDrawnChatLines().size();

			if(drawnLinesCount > 0) {
				if(event.chat.getChatOpen()) {
					open = true;
				}

				float f1 = getScale();
				int l = MathHelper.ceiling_float_int((float) event.chat.getChatWidth() / f1);
				GlStateManager.pushMatrix();
				GlStateManager.translate(2.0F, 20.0F, 0.0F);
				GlStateManager.scale(f1, f1, 1.0F);

				if(previousChatSize < accessor.getDrawnChatLines().size()) {
					animatedOffset = 9;
					lastAnimatedOffset = 9;
				}

				if(smooth && !(event.chat.getChatOpen() && accessor.getScrollPos() > 0)) {
					float calculatedOffset = lastAnimatedOffset + (animatedOffset - lastAnimatedOffset) * event.partialTicks;

					GlStateManager.translate(0, calculatedOffset, 0);
				}

				for(int i = 0; i + accessor.getScrollPos() < accessor.getDrawnChatLines().size() && i < linesCount; ++i) {
					ChatLine line = (ChatLine) accessor.getDrawnChatLines().get(i + accessor.getScrollPos());

					if(line != null) {
						int update = event.updateCounter - line.getUpdatedCounter();

						if(open || update < 200) {
							double percent = (double) update / 200.0D;
							percent = 1.0D - percent;
							percent = percent * 10.0D;
							percent = MathHelper.clamp_double(percent, 0.0D, 1.0D);
							percent = percent * percent;

							if(open) {
								percent = 1;
							}

							double percentFG = percent;

							if(smooth) {
								ChatAnimationData data = ((ChatAnimationData) line);

								if(data.getTransparency() != 0) {
									float calculatedTransparency = data.getLastTransparency() + (data.getTransparency() - data.getLastTransparency()) * event.partialTicks;
									percentFG *= (1 - calculatedTransparency);
								}
							}

							++j;

							if(percent > 0.05F) {
								int i2 = 0;
								int j2 = -i * 9;

								if(background) {
									Gui.drawRect(i2 - 2, j2 - 9, i2 + l + 4, j2,
											backgroundColour.withAlpha((int) (backgroundColour.getAlpha() * percent)).getValue());
								}

								String formattedText = line.getChatComponent().getFormattedText();
								GlStateManager.enableBlend();

								if(percentFG > 0.05F) {
									mc.fontRendererObj.drawString(colours ? formattedText :
											EnumChatFormatting.getTextWithoutFormattingCodes(formattedText), (float) i2, (float) (j2 - 8),
											defaultTextColour.withAlpha((int) (defaultTextColour.getAlpha() * percentFG)).getValue(), shadow);
								}

								GlStateManager.disableAlpha();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if(open) {
					int k2 = mc.fontRendererObj.FONT_HEIGHT;
					GlStateManager.translate(-3.0F, 0.0F, 0.0F);
					int l2 = drawnLinesCount * k2 + drawnLinesCount;
					int i3 = j * k2 + j;
					int j3 = accessor.getScrollPos() * i3 / drawnLinesCount;
					int k1 = i3 * i3 / l2;

					if(l2 != i3) {
						hasScrollbar = true;

						int k3 = j3 > 0 ? 170 : 96;
						int l3 = accessor.getIsScrolled() ? 13382451 : 3355562;
						Gui.drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
						Gui.drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
					}
					else {
						hasScrollbar = false;
					}
				}

				GlStateManager.popMatrix();
			}
			else {
				hasScrollbar = false;
			}
		}
		else {
			hasScrollbar = false;
		}

		previousChatSize = accessor.getDrawnChatLines().size();
	}

}
