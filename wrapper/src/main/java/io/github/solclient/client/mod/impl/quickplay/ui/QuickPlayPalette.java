package io.github.solclient.client.mod.impl.quickplay.ui;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import io.github.solclient.client.mod.impl.quickplay.database.QuickPlayGame;
import io.github.solclient.client.ui.ScreenAnimation;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.data.*;
import lombok.*;
import net.minecraft.util.math.MathHelper;

public class QuickPlayPalette extends ComponentScreen {

	private final ScreenAnimation animation = new ScreenAnimation();

	public QuickPlayPalette(QuickPlayMod mod) {
		super(new Component() {
			{
				add(new QuickPlayPaletteComponent(mod),
						new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE));
			}
		});

		background = false;
	}

	@Override
	public void init() {
		super.init();
		Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void removed() {
		super.removed();
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	protected void wrap(Runnable task) {
		animation.wrap(task);
	}

	public static class QuickPlayPaletteComponent extends BlockComponent {

		@Getter
		private final QuickPlayMod mod;
		private final TextFieldComponent search;
		@Getter
		@Setter
		private boolean allGames;
		@Getter
		@Setter
		private QuickPlayGame game;
		private final QuickPlayScroll scroll;
		@Setter
		@Getter
		private QuickPlayOptionComponent selected;

		public QuickPlayPaletteComponent(QuickPlayMod mod) {
			super(theme.bg, 12, 0);
			this.mod = mod;
			scroll = new QuickPlayScroll(this);
			add(scroll, (component, defaultBounds) -> new Rectangle(0, 30, getBounds().getWidth(),
					getBounds().getHeight() - 30));
			search = new TextFieldComponent(250, false).withPlaceholder("sol_client.mod.screen.search").autoFlush()
					.onUpdate((ignored) -> {
						scroll.load();
						return true;
					}).withIcon("search").withoutUnderline();
			add(search, (component, defaultBounds) -> defaultBounds.offset(10, 10).grow(-20, 0));
			scroll.load();
		}

		@Override
		protected Rectangle getDefaultBounds() {
			return Rectangle.ofDimensions(250, 250);
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if (keyCode == Keyboard.KEY_F && hasControlDown() && !hasShiftDown() && !hasAltDown()) {
				search.setFocused(true);
				return true;
			}

			if (character > 31 && !search.isFocused()) {
				search.setFocused(true);
				search.setText("");
			}

			if (keyCode == Keyboard.KEY_UP || keyCode == Keyboard.KEY_DOWN && !scroll.getSubComponents().isEmpty()) {
				int direction = keyCode == Keyboard.KEY_UP ? -1 : 1;
				int index = scroll.getSubComponents().indexOf(selected);
				index += direction;
				index = MathHelper.clamp(index, 0, scroll.getSubComponents().size() - 1);

				if (scroll.getSubComponents().get(index) instanceof QuickPlayOptionComponent) {
					selected = (QuickPlayOptionComponent) scroll.getSubComponents().get(index);

					while (selected.getBounds().getEndY() - scroll.getScroll() > scroll.getBounds().getHeight())
						scroll.jumpTo(scroll.getScroll() + selected.getBounds().getHeight());

					while (selected.getBounds().getY() - scroll.getScroll() < 0)
						scroll.jumpTo(scroll.getScroll() - selected.getBounds().getHeight());

					return true;
				}
			}

			if ((keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_RIGHT) && selected != null) {
				selected.mouseClicked(null, 0);
				return true;
			}

			return super.keyPressed(info, keyCode, character);
		}

		public String getFilter() {
			return search.getText();
		}

		public void back() {
			if (!allGames)
				return;

			if (game != null)
				game = null;
			else
				allGames = false;

			scroll.load();
		}

		public void openAllGames() {
			selectGame(null);
		}

		public void selectGame(QuickPlayGame game) {
			allGames = true;
			this.game = game;
			scroll.load();
		}

	}

}
