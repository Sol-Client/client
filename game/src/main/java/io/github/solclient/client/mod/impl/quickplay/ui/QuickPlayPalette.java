package io.github.solclient.client.mod.impl.quickplay.ui;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.database.QuickPlayGame;
import io.github.solclient.client.platform.mc.DrawableHelper;
import io.github.solclient.client.platform.mc.render.GlStateManager;
import io.github.solclient.client.platform.mc.screen.ProxyScreen;
import io.github.solclient.client.platform.mc.text.Font;
import io.github.solclient.client.platform.mc.text.TextFormatting;
import io.github.solclient.client.platform.mc.util.Input;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;

// Dirty code alert
// TODO new UI
// TODO translation
public final class QuickPlayPalette extends ProxyScreen {

	private final QuickPlayMod mod;
	private Font font = SolClientConfig.instance.getUIFont();
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

	public QuickPlayPalette(QuickPlayMod mod) {
		super(null);
		this.mod = mod;
	}

	@Override
	public void initScreen() {
		super.initScreen();
		Input.enableRepeatEvents(true);
	}

	@Override
	protected void onClose() {
		Input.enableRepeatEvents(false);
	}

	@Override
	public void renderScreen(int mouseX, int mouseY, float tickDelta) {
		super.renderScreen(mouseX, mouseY, tickDelta);

		GlStateManager.enableBlend();

		Rectangle box = new Rectangle(0, 0, 200, 250);
		box = box.offset(width / 2 - (box.getWidth() / 2), getHeight() / 2 - (box.getHeight() / 2));

		box.fill(Colour.BACKGROUND);

		font.render(query.isEmpty() ? "Search" : query, box.getX() + 10 + (query.isEmpty() ? 2 : 0),
				box.getY() + 10 + (SolClientConfig.instance.fancyFont ? 0 : 1), query.isEmpty() ? 0xFF666666 : -1);

		DrawableHelper.fillRect((int) (box.getX() + 10 + font.getWidth(query)), box.getY() + 10,
				(int) (box.getX() + 11 + font.getWidth(query)), box.getY() + 20, -1);

		DrawableHelper.renderHorizontalLine(box.getX(), box.getX() + box.getWidth() - 1, box.getY() + 30, 0xFF000000);

		Rectangle entriesBox = new Rectangle(box.getX(), box.getY() + 31, box.getWidth(), box.getHeight() - 31);
		Rectangle base = new Rectangle(entriesBox.getX(), entriesBox.getY(), entriesBox.getWidth(), 20);

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		Utils.scissor(entriesBox);

		int x = box.getX();

		int y = 0;

		scroll = Utils.clamp(scroll, 0, maxScrolling);

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
				mc.getItemRenderer().render(game.getIcon(), x + 3, gameBounds.getY() + 1);
			}

			font.render(game.getText(), x + 25,
					gameBounds.getY() + 4 + (SolClientConfig.instance.fancyFont ? 0 : 1), -1);

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
					.filter((mode) -> TextFormatting
							.strip(mode.getText().toLowerCase(Locale.ROOT))
							.contains(query.toLowerCase(Locale.ROOT)))
					.sorted((o1, o2) -> {
						return Integer.compare(TextFormatting.strip(o1.getText().toLowerCase())
								.startsWith(query.toLowerCase()) ? 0 : 1, TextFormatting.strip(o2.getText().toLowerCase())
								.startsWith(query.toLowerCase()) ? 0 : 1);
					})
					.collect(Collectors.toList());
		}

		return result;
	}

	private void clampIndex() {
		selectedIndex = Utils.clamp(selectedIndex, 0, getGames().size() - 1);
	}

	@Override
	public void keyDown(int code, int scancode, int mods) {
		super.keyDown(code, scancode, mods);

		if(code == Input.BACKSPACE) {
			if(!query.isEmpty()) {
				if((mods & Input.COMMAND_MODIFIER) != 0) {
					query = "";
				}
				else {
					query = query.substring(0, query.length() - 1);
				}
			}
		}
		else if(code == Input.DOWN) {
			selectedIndex++;
			scroll += 20;
			clampIndex();
		}
		else if(code == Input.UP) {
			selectedIndex--;
			scroll -= 20;
			clampIndex();
		}
		else if(code == Input.ENTER || code == Input.RIGHT) {
			try {
				getGames().get(selectedIndex).onClick(this, mod);
			}
			catch(IndexOutOfBoundsException ignored) {
				// Prevent crash in the rare case that the index is desynchronised.
			}
		}
		else if(code == Input.LEFT) {
			back();
		}
	}

	@Override
	public void characterTyped(char character, int key) {
		super.characterTyped(character, key);

		if(character != '§') {
			query += character;
			inAllGames = false;
		}
	}

	@Override
	public void mouseDown(int x, int y, int mouseButton) {
		super.mouseDown(x, y, mouseButton);

		if(mouseButton == 0) {
			mouseDown = true;
			lastMouseX = lastMouseY = 0;
		}
	}

	@Override
	public void mouseUp(int mouseX, int mouseY, int state) {
		super.mouseUp(mouseX, mouseY, state);

		if(state == 0) {
			mouseDown = false;
		}
	}

	@Override
	public void scroll(int by) {
		if(by != 0) {
			if(by > 0) {
				by = -1;
			}
			else if(by < 0) {
				by = 1;
			}

			scroll += 20 * by;
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
			mc.closeScreen();
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
