package me.mcblueparrot.client.mod.impl.quickplay.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import lombok.RequiredArgsConstructor;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.mod.impl.quickplay.QuickPlayMod;
import me.mcblueparrot.client.mod.impl.quickplay.database.QuickPlayGame;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

@RequiredArgsConstructor
public class QuickPlayPalette extends GuiScreen {

	private final QuickPlayMod mod;
	private Font font = SolClientMod.getFont();
	private String query = "";
	private int selectedIndex;
	private boolean inAllGames;
	private QuickPlayGame currentGame;
	private int maxScrolling;
	private int scroll;
	private boolean mouseDown;
	private boolean wasMouseDown;
	private int lastMouseX = -1;
	private int lastMouseY = -1;
	private int recentGamesScroll;
	private int allGamesScroll;
	private int nextScroll = -1;

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		GlStateManager.enableBlend();

		Rectangle box = new Rectangle(0, 0, 200, 250);
		box = box.offset(width / 2 - (box.getWidth() / 2), height / 2 - (box.getHeight() / 2));

		Utils.drawRectangle(box, new Colour(20, 20, 20));

		font.renderString(query.isEmpty() ? "Search" : query, box.getX() + 10 + (query.isEmpty() ? 2 : 0),
				box.getY() + 10 + (font instanceof SlickFontRenderer ? 0 : 1), query.isEmpty() ? 0xFF666666 : -1);

		drawRect((int) (box.getX() + 10 + font.getWidth(query)), box.getY() + 10,
				(int) (box.getX() + 11 + font.getWidth(query)), box.getY() + 20, -1);

		drawHorizontalLine(box.getX(), box.getX() + box.getWidth() - 1, box.getY() + 30, 0xFF000000);

		Rectangle entriesBox = new Rectangle(box.getX(), box.getY() + 31, box.getWidth(), box.getHeight() - 31);
		Rectangle base = new Rectangle(entriesBox.getX(), entriesBox.getY(), entriesBox.getWidth(), 20);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		Utils.scissor(entriesBox);

		int x = box.getX();

		int y = 0;

		scroll = MathHelper.clamp_int(scroll, 0, maxScrolling);

		List<QuickPlayOption> options = getGames();

		if(selectedIndex > options.size() - 1) {
			selectedIndex = options.size() - 1;
		}

		if(selectedIndex < 0) {
			selectedIndex = 0;
		}

		for(int i = 0; i < options.size(); i++) {
			QuickPlayOption game = options.get(i);

			Rectangle gameBounds = base.offset(0, y - scroll);

			boolean containsMouse = gameBounds.contains(mouseX, mouseY)
					&& entriesBox.contains(mouseX, mouseY);

			if(selectedIndex == i) {
				gameBounds.fill(new Colour(60, 60, 60));

				if(containsMouse && mouseDown && !wasMouseDown) {
					game.onClick(this, mod);
				}
			}

			if((lastMouseX != mouseX || lastMouseY != mouseY) && lastMouseX != -1
					&& lastMouseY != -1 &&
					containsMouse) {
				selectedIndex = i;
			}

			if(game.getIcon() != null) {
				RenderHelper.enableGUIStandardItemLighting();
				mc.getRenderItem().renderItemIntoGUI(game.getIcon(), x + 3, gameBounds.getY() + 1);
				RenderHelper.disableStandardItemLighting();
			}

			font.renderString(game.getText(), x + 25,
					gameBounds.getY() + 4 + (font instanceof SlickFontRenderer ? 0 : 1), -1);

			y += gameBounds.getHeight();
		}


		maxScrolling = Math.max(0, y - entriesBox.getHeight());

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		lastMouseX = mouseX;
		lastMouseY = mouseY;

		wasMouseDown = mouseDown;

		if(nextScroll != -1) {
			scroll = nextScroll;
			nextScroll = -1;
		}
	}

	private List<QuickPlayOption> getGames() {
		List<QuickPlayOption> result;
		if(inAllGames) {
			if(currentGame != null) {
				result = currentGame.getModeOptions();
			}
			else {
				result = mod.getGameOptions();
			}
			result.add(0, new BackOption());
		}
		else if(query.isEmpty()) {
			result = mod.getRecentlyPlayed();
			result.add(new AllGamesOption());
		}
		else {
			result = mod.getGames().stream().flatMap((entry) -> entry.getModes().stream())
					.filter((mode) -> EnumChatFormatting
							.getTextWithoutFormattingCodes(mode.getText().toLowerCase(Locale.ROOT))
							.contains(query.toLowerCase(Locale.ROOT)))
					.sorted((o1, o2) -> {
						return Integer.compare(EnumChatFormatting.getTextWithoutFormattingCodes(o1.getText().toLowerCase())
								.startsWith(query.toLowerCase()) ? 0 : 1, EnumChatFormatting.getTextWithoutFormattingCodes(o2.getText().toLowerCase())
								.startsWith(query.toLowerCase()) ? 0 : 1);
					})
					.collect(Collectors.toList());
		}

		return result;
	}

	private void clampIndex() {
		selectedIndex = MathHelper.clamp_int(selectedIndex, 0, getGames().size() - 1);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(typedChar > 31 && typedChar != '§') {
			query += typedChar;
			inAllGames = false;
		}
		else if(typedChar == 8 && !query.isEmpty()) {
			if(GuiScreen.isCtrlKeyDown()) {
				query = "";
			}
			else {
				query = query.substring(0, query.length() - 1);
			}
		}

		if(keyCode == Keyboard.KEY_DOWN) {
			selectedIndex++;
			scroll += 20;
			clampIndex();
		}
		else if(keyCode == Keyboard.KEY_UP) {
			selectedIndex--;
			scroll -= 20;
			clampIndex();
		}
		else if(keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_RIGHT) {
			getGames().get(selectedIndex).onClick(this, mod);
		}
		else if(keyCode == Keyboard.KEY_LEFT) {
			back();
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if(mouseButton == 0) {
			mouseDown = true;
			lastMouseX = lastMouseY = 0;
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		if(state == 0) {
			mouseDown = false;
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int dWheel = Mouse.getEventDWheel();

		if(dWheel != 0) {
			if(dWheel > 0) {
				dWheel = -1;
			}
			else if(dWheel < 0) {
				dWheel = 1;
			}

			scroll += 20 * dWheel;
			lastMouseX = lastMouseY = 0;
		}
	}

	public void openAllGames() {
		recentGamesScroll = scroll;
		inAllGames = true;
		currentGame = null;
		scroll = 0;
		selectedIndex = 1;
	}

	public void back() {
		if(currentGame != null) {
			selectedIndex = mod.getGames().indexOf(currentGame) + 1;
			currentGame = null;

			nextScroll = allGamesScroll;
		}
		else if(inAllGames) {
			selectedIndex = mod.getRecentlyPlayed().size();
			inAllGames = false;

			nextScroll = recentGamesScroll;
		}
		else {
			mc.displayGuiScreen(null);
			return;
		}
	}

	public void selectGame(QuickPlayGame game) {
		allGamesScroll = scroll;
		selectedIndex = 1;
		scroll = 0;
		currentGame = game;
	}

}
