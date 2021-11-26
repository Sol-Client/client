package me.mcblueparrot.client.mod.impl.hud;

import org.lwjgl.input.Keyboard;

import com.google.gson.annotations.Expose;

import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.events.ChatRenderEvent;
import me.mcblueparrot.client.events.EventHandler;
import me.mcblueparrot.client.mod.annotation.ConfigOption;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mod.hud.Hud;
import me.mcblueparrot.client.ui.ChatButton;
import me.mcblueparrot.client.util.Colour;
import me.mcblueparrot.client.util.Rectangle;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.access.AccessGuiChat;
import me.mcblueparrot.client.util.access.AccessGuiNewChat;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class ChatHud extends Hud {

	public static boolean enabled;
	public static ChatHud instance;

	private static String symbols = "☺☹♡♥◀▶▲▼←→↑↓«»©™‽☕✓✕⚐⚑⚠☆★✮✫☃☄";
	private static char[][] table;


	private static char[][] getSymbolTable() {
		if(table == null) {
			table = new char[6][6];
			int y = 0;
			int x = 0;
			for(char character : symbols.toCharArray()) {
				table[y][x] = character;
				x++;
				if(x > 5) {
					x = 0;
					y++;
				}
			}
			return table;
		}

		return table;
	}

	@Expose
	@ConfigOption("Visibility")
	public ChatVisibility visibility = ChatVisibility.SHOWN;
	@Expose
	@ConfigOption("Background")
	private boolean background = true;
	@Expose
	@ConfigOption("Background Colour")
	private Colour backgroundColour = new Colour(0, 0, 0, 127);
	@Expose
	@ConfigOption("Text Colour")
	private Colour textColour = Colour.WHITE;
	@Expose
	@ConfigOption("Text Shadow")
	private boolean shadow = true;
	@Expose
	@ConfigOption("Colours")
	public boolean colours = true;
	@Expose
	@ConfigOption("Width")
	@Slider(min = 40, max = 320, step = 1)
	public float width = 320;
	@Expose
	@ConfigOption("Height (Closed)")
	@Slider(min = 20, max = 180, step = 1)
	public float closedHeight = 90;
	@Expose
	@ConfigOption("Height (Open)")
	@Slider(min = 20, max = 180, step = 1)
	public float openHeight = 180;
	@Expose
	@ConfigOption("Web Links")
	public boolean links = true;
	@Expose
	@ConfigOption("Prompt Web Links")
	public boolean promptLinks = true;
	@Expose
	@ConfigOption("Prevent Force Closing")
	public boolean preventClose = true;

	public final SymbolsButton symbolsButton = new SymbolsButton();

	public ChatHud() {
		super("Chat", "chat", "Improves and allows you to customise the chat.");
		instance = this;
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

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
		Client.INSTANCE.registerChatButton(symbolsButton);
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
		Client.INSTANCE.unregisterChatButton(symbolsButton);
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

	@EventHandler
	public void onChatRender(ChatRenderEvent event) {
		event.cancelled = true;
		if(visibility != ChatVisibility.HIDDEN) {
			AccessGuiNewChat accessor = ((AccessGuiNewChat) event.chat);
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

				for(int i = 0; i + accessor.getScrollPos() < accessor.getDrawnChatLines().size() && i < linesCount; ++i) {
					ChatLine chatline = (ChatLine)accessor.getDrawnChatLines().get(i + accessor.getScrollPos());

					if(chatline != null) {
						int update = event.updateCounter - chatline.getUpdatedCounter();

						if(update < 200 || open) {
							double percent = (double) update / 200.0D;
							percent = 1.0D - percent;
							percent = percent * 10.0D;
							percent = MathHelper.clamp_double(percent, 0.0D, 1.0D);
							percent = percent * percent;

							if(open) {
								percent = 1;
							}

							++j;

							if(percent > 0.05F) {
								int i2 = 0;
								int j2 = -i * 9;
								if(background) {
									Gui.drawRect(i2 - 2, j2 - 9, i2 + l + 4, j2,
											backgroundColour.withAlpha((int) (backgroundColour.getAlpha() * percent)).getValue());
								}
								String formattedText = chatline.getChatComponent().getFormattedText();
								GlStateManager.enableBlend();
								this.mc.fontRendererObj.drawString(colours ? formattedText :
										EnumChatFormatting.getTextWithoutFormattingCodes(formattedText), (float)i2, (float)(j2 - 8),
										textColour.withAlpha((int) (textColour.getAlpha() * percent)).getValue(), shadow);
								GlStateManager.disableAlpha();
								GlStateManager.disableBlend();
							}
						}
					}
				}

				if(open) {
					int k2 = this.mc.fontRendererObj.FONT_HEIGHT;
					GlStateManager.translate(-3.0F, 0.0F, 0.0F);
					int l2 = drawnLinesCount * k2 + drawnLinesCount;
					int i3 = j * k2 + j;
					int j3 = accessor.getScrollPos() * i3 / drawnLinesCount;
					int k1 = i3 * i3 / l2;

					if(l2 != i3) {
						int k3 = j3 > 0 ? 170 : 96;
						int l3 = accessor.getIsScrolled() ? 13382451 : 3355562;
						Gui.drawRect(0, -j3, 2, -j3 - k1, l3 + (k3 << 24));
						Gui.drawRect(2, -j3, 1, -j3 - k1, 13421772 + (k3 << 24));
					}
				}

				GlStateManager.popMatrix();
			}
		}
	}

	public enum ChatVisibility {
		SHOWN("Shown"),
		COMMANDS("Commands Only"),
		HIDDEN("Hidden");

		private String name;

		ChatVisibility(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

	}

	public class SymbolsButton implements ChatButton {

		@Override
		public int getPriority() {
			return getIndex();
		}

		@Override
		public int getPopupWidth() {
			return 77;
		}

		@Override
		public int getPopupHeight() {
			return getSymbolTable().length * 13 - 1;
		}

		@Override
		public int getWidth() {
			return 12;
		}

		@Override
		public String getText() {
			return "✮";
		}

		@Override
		public void render(int x, int y, boolean mouseDown, boolean wasMouseDown, boolean wasMouseClicked, int mouseX, int mouseY) {
			int originalX = x;
			for(char[] characters : getSymbolTable()) {
				x = originalX;
				for(char character : characters) {
					Rectangle characterBounds = new Rectangle(x, y, 12, 12);
					boolean selected = character != 0 && characterBounds.contains(mouseX, mouseY);
					Utils.drawRectangle(characterBounds, selected ? Colour.WHITE_128 : Colour.BLACK_128);
					if(character != 0) {
						font.drawString(character + "",
								x + (13 / 2) - (font.getCharWidth(character) / 2),
								characterBounds.getY() + (characterBounds.getHeight() / 2)- (font.FONT_HEIGHT / 2),
								characterBounds.contains(mouseX, mouseY) ? 0 : -1);
					}

					if(selected && wasMouseClicked) {
						Utils.playClickSound();
						((AccessGuiChat) Utils.getChatGui()).type(character, Keyboard.KEY_0);
					}
					x += 13;
				}
				y += 13;
			}
		}
	}

}
