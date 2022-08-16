package io.github.solclient.client.mod.impl.hud.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.Constants;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.game.PostTickEvent;
import io.github.solclient.client.event.impl.hud.PreHudElementRenderEvent;
import io.github.solclient.client.event.impl.input.ScrollWheelEvent;
import io.github.solclient.client.event.impl.network.chat.IncomingChatMessageEvent;
import io.github.solclient.client.mod.annotation.FileOption;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.mod.hud.HudMod;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.hud.chat.Chat;
import io.github.solclient.client.platform.mc.hud.chat.ChatMessage;
import io.github.solclient.client.platform.mc.option.KeyBinding;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.text.Text;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.VanillaHudElement;
import io.github.solclient.client.util.data.Colour;

public class ChatMod extends HudMod {

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
	public KeyBinding peekKey = KeyBinding.create(getTranslationKey() + ".peek", 0, Constants.KEY_CATEGORY);
	private boolean wasPeeking;
	private boolean hasScrollbar;

	@Expose
	@Option
	public ChatVisibility visibility = ChatVisibility.SHOWN;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY)
	private boolean background = true;
	@Expose
	@Option(translationKey = SimpleHudMod.TRANSLATION_KEY, applyToAllClass = Option.BACKGROUND_COLOUR_CLASS)
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
		mc.getOptions().addKey(peekKey);
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

		if(mc.hasLevel()) {
			mc.getIngameHud().getChat().refresh();
		}
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
		Client.INSTANCE.unregisterChatButton(symbolsButton);

		if(mc.hasLevel()) {
			mc.getIngameHud().getChat().refresh();
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
			mc.getIngameHud().getChat().refresh();
		}
		return super.onOptionChange(key, value);
	}

	@EventHandler
	public void onScroll(ScrollWheelEvent event) {
		// Arrow key scrolling isn't implemented for various reasons, but nobody cares anyway.

		if(hasScrollbar && peekKey.isHeld() && event.getAmount() != 0) {
			int amount = 1;

			if(event.getAmount() < 0) {
				amount = -amount;
			}

			if(!Input.isShiftHeld()) {
				amount *= 7;
			}

			mc.getIngameHud().getChat().scroll(amount);

			event.cancel();
		}
	}

	@EventHandler
	@SuppressWarnings("unchecked")
	public void onTick(PostTickEvent event) {
		if(!peekKey.isHeld() && wasPeeking) {
			mc.getIngameHud().getChat().resetScroll();
		}

		wasPeeking = peekKey.isHeld();

		if(smooth && !mc.isPaused()) {
			lastAnimatedOffset = animatedOffset;

			animatedOffset *= ANIMATION_MULTIPLIER;

			for(ChatAnimationData line : (Iterable<ChatAnimationData>) (Object) (mc.getIngameHud().getChat()
					.getVisibleMessages())) {
				line.setLastTransparency(line.getTransparency());
				line.setTransparency(line.getTransparency() * ANIMATION_MULTIPLIER);
			}
		}
	}

	@EventHandler
	public void onIncomingChatMessage(IncomingChatMessageEvent event) {
		if(!chatFilter) {
			return;
		}

		// Primarily focused on English text, as all non-ascii characters are stripped.
		String message = event.getMessage().getPlain();

		for(String word : filteredWords) {
			word = strip(word);

			if(message.equals(word) || message.startsWith(word + " ")
					|| message.endsWith(" " + word) || message.contains(" " + word + " ")) {
				event.cancel();
				return;
			}
		}
	}

	private String strip(String word) {
		return word.toLowerCase().codePoints().filter(Character::isLetter).mapToObj((i) -> (Character) (char) i)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
	}

	@EventHandler
	public void onChatRender(PreHudElementRenderEvent event) {
		if(event.getElement() != VanillaHudElement.CHAT) {
			return;
		}

		event.cancel();
		Chat chat = mc.getIngameHud().getChat();

		if(visibility != ChatVisibility.HIDDEN) {
			int linesCount = chat.getVisibleMessageCount();
			boolean open = false;
			int j = 0;
			int visibleLinesCount = chat.getVisibleMessages().size();

			if(visibleLinesCount > 0) {
				if(chat.isOpen()) {
					open = true;
				}

				float scale = getScale();
				int width = (int) Math.ceil(chat.getWidth() / scale);
				GlStateManager.pushMatrix();
				GlStateManager.translate(2, 20, 0);
				GL11.glScalef(scale, scale, 1.0F);

				if(previousChatSize < visibleLinesCount) {
					animatedOffset = 9;
					lastAnimatedOffset = 9;
				}

				if(smooth && !(chat.isOpen() && chat.getScroll() > 0)) {
					float calculatedOffset = lastAnimatedOffset + (animatedOffset - lastAnimatedOffset) * event.getTickDelta();

					GL11.glTranslatef(0, calculatedOffset, 0);
				}

				for(int i = 0; i + chat.getScroll() < chat.getVisibleMessages().size() && i < linesCount; ++i) {
					ChatMessage line = chat.getVisibleMessages().get(i + chat.getScroll());

					if(line != null) {
						int update = mc.getIngameHud().getTicks() - line.getUpdatedCounter();

						if(open || update < 200) {
							double percent = update / 200.0D;
							percent = 1.0D - percent;
							percent = percent * 10.0D;
							percent = Utils.clamp(percent, 0.0D, 1.0D);
							percent = percent * percent;

							if(open) {
								percent = 1;
							}

							double percentFG = percent;

							if(smooth) {
								ChatAnimationData data = ((ChatAnimationData) line);

								if(data.getTransparency() != 0) {
									float calculatedTransparency = data.getLastTransparency() + (data.getTransparency() - data.getLastTransparency()) * event.getTickDelta();
									percentFG *= (1 - calculatedTransparency);
								}
							}

							++j;

							if(percent > 0.05F) {
								int i2 = 0;
								int j2 = -i * 9;

								if(background) {
									DrawableHelper.fillRect(i2 - 2, j2 - 9, i2 + width + 4, j2,
											backgroundColour.withAlpha((int) (backgroundColour.getAlpha() * percent)).getValue());
								}

								Text formattedText = line.getMessage();
								GL11.glEnable(GL11.GL_BLEND);

								if(percentFG > 0.05F) {
									mc.getFont().render(colours ? formattedText :
											formattedText.withoutColour(), i2, j2 - 8,
											defaultTextColour.withAlpha((int) (defaultTextColour.getAlpha() * percentFG)).getValue(), shadow);
								}
							}
						}
					}
				}

				if(open) {
					int k2 = mc.getFont().getHeight();
					GL11.glTranslatef(-3, 0, 0);
					int l2 = visibleLinesCount * k2 + visibleLinesCount;
					int i3 = j * k2 + j;
					int j3 = chat.getScroll() * i3 / visibleLinesCount;
					int k1 = i3 * i3 / l2;

					if(l2 != i3) {
						hasScrollbar = true;

						int k3 = j3 > 0 ? 170 : 96;
						int l3 = chat.isScrolled() ? 13382451 : 3355562;
						DrawableHelper.fillRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
						DrawableHelper.fillRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
					}
					else {
						hasScrollbar = false;
					}
				}

				GL11.glPopMatrix();
			}
			else {
				hasScrollbar = false;
			}

			previousChatSize = visibleLinesCount;
		}
		else {
			hasScrollbar = false;
		}
	}

}
