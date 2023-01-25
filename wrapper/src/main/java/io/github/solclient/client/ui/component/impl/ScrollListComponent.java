package io.github.solclient.client.ui.component.impl;

import org.lwjgl.input.Keyboard;
import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.mod.impl.SolClientConfig;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.util.data.*;
import net.minecraft.util.math.MathHelper;

public abstract class ScrollListComponent extends Component {

	private double targetY;
	private double animatedY;
	private double lastAnimatedY;
	private double calculatedY;
	private int maxScrolling;
	private double scrollPercent;
	private Component scrollbar;
	private int scrolling = -1;
	private int lastMouseY;
	private double grabStartY = -1;
	private int grabMouseY;

	@Override
	public void setParent(Component parent) {
		super.setParent(parent);

		parent.add(scrollbar = new BlockComponent(Colour.LIGHT_BUTTON, 2.5F, 0) {

			@Override
			public void render(ComponentRenderInfo info) {
				scrollPercent = (double) ScrollListComponent.this.getBounds().getHeight() / (double) getContentHeight();

				NanoVG.nvgTranslate(nvg, 0, (float) (calculatedY * scrollPercent));

				if (maxScrolling != 0) {
					super.render(info);
				}
			}

		}, (component, defaultBounds) -> {
			return new Rectangle(getBounds().getX() + getBounds().getWidth() - 8, getBounds().getY(), 5,
					(int) (getBounds().getHeight() * scrollPercent));
		});
	}

	public void add(Component component) {
		add(getSubComponents().size(), component);
	}

	public void add(int index, Component component) {
		add(index, component,
				new AlignedBoundsController(Alignment.CENTRE, Alignment.START, (sizingComponent, defaultBounds) -> {
					Component previous = null;
					Rectangle lastBounds = null;

					int prevIndex = subComponents.indexOf(component) - 1;
					if (prevIndex > -1) {
						previous = subComponents.get(prevIndex);
						lastBounds = previous.getCachedBounds();
					}

					return new Rectangle(defaultBounds.getX(),
							previous == null ? 0 : lastBounds.getY() + lastBounds.getHeight() + getSpacing(),
							defaultBounds.getWidth(), defaultBounds.getHeight());
				}));
	}

	private ComponentRenderInfo translate(ComponentRenderInfo info) {
		return new ComponentRenderInfo(info.getRelativeMouseX(), (int) (info.getRelativeMouseY() + calculatedY),
				info.getPartialTicks());
	}

	public ComponentRenderInfo reverseTranslation(ComponentRenderInfo info) {
		return new ComponentRenderInfo(info.getRelativeMouseX(), (int) (info.getRelativeMouseY() - calculatedY),
				info.getPartialTicks());
	}

	@Override
	public void render(ComponentRenderInfo info) {
		if (!SolClientConfig.instance.smoothScrolling) {
			calculatedY = targetY;
		} else {
			calculatedY = (lastAnimatedY + (animatedY - lastAnimatedY) * info.getPartialTicks());
		}

		NanoVG.nvgTranslate(nvg, 0, (float) -calculatedY);
		maxScrolling = getContentHeight() - getBounds().getHeight();

		if (maxScrolling < 0) {
			maxScrolling = 0;
		}

		if (lastMouseY != info.getRelativeMouseY()) {
			if (scrolling > 0) {
				int targetCompY = info.getRelativeMouseY() - scrolling;
				jumpTo((int) (targetCompY / (getBounds().getHeight() / (double) getContentHeight())));
				clamp();
			} else if (grabStartY != -1) {
				jumpTo(grabStartY - (info.getRelativeMouseY() - grabMouseY));
				clamp();
			}
		}

		lastMouseY = info.getRelativeMouseY();

		super.render(translate(info));
	}

	private int mouseInScrollbar(ComponentRenderInfo info) {
		Rectangle scrollBounds = scrollbar.getBounds().offset(-getBounds().getX(),
				-getBounds().getY() + (int) (calculatedY * scrollPercent));

		if (new Rectangle(scrollBounds.getX(), 0, scrollBounds.getWidth(), getBounds().getHeight())
				.contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
			if (!scrollBounds.contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
				return scrollbar.getBounds().getHeight() / 2;
			}

			return info.getRelativeMouseY() - scrollBounds.getY();
		}

		return -1;
	}

	@Override
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if (button == 0) {
			scrolling = mouseInScrollbar(reverseTranslation(info));
			lastMouseY = -1;
			if (scrolling != -1) {
				return true;
			}
		}

		boolean superResult = super.mouseClicked(info, button);

		if (button == 0 && !superResult && grabStartY == -1) {
			grabStartY = targetY;
			grabMouseY = reverseTranslation(info).getRelativeMouseY();
		}

		return true;
	}

	@Override
	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (button == 0) {
			if (scrolling != -1) {
				scrolling = -1;
				return true;
			}
			if (grabStartY != -1) {
				grabStartY = -1;
				return true;
			}
		}

		return super.mouseReleasedAnywhere(info, button, inside);
	}

	@Override
	protected boolean shouldCull(Component component) {
		if ((component.getBounds().getEndY() - calculatedY) < 0) {
			return true;
		} else if (component.getBounds().getY() - calculatedY > getBounds().getHeight()) {
			return true;
		}

		return false;
	}

	public void snapTo(int scroll) {
		targetY = animatedY = lastAnimatedY = calculatedY = maxScrolling = scroll;
	}

	public void jumpTo(double scroll) {
		targetY = scroll;
	}

	public int getScroll() {
		return (int) targetY;
	}

	private int getContentHeight() {
		if (subComponents.isEmpty())
			return 0;

		return getBounds(subComponents.get(subComponents.size() - 1)).getEndY();
	}

	@Override
	public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
		return super.mouseClickedAnywhere(translate(info), button, inside, processed);
	}

	protected int getScrollStep() {
		if (subComponents.isEmpty()) {
			return 0;
		}

		return (subComponents.get(0).getBounds().getHeight() + getSpacing());
	}

	@Override
	public boolean mouseScroll(ComponentRenderInfo info, int delta) {
		if (super.mouseScroll(info, delta)) {
			return true;
		}

		delta = delta / Math.abs(delta);

		if (subComponents.size() != 0) {
			targetY -= delta * getScrollStep();
		}

		return true;
	}

	@Override
	public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
		if (keyCode == Keyboard.KEY_HOME) {
			targetY = 0;
			return true;
		} else if (keyCode == Keyboard.KEY_END) {
			targetY = Double.POSITIVE_INFINITY;
			return true;
		} else if (keyCode == Keyboard.KEY_DOWN) {
			targetY += getScrollStep();
			return true;
		} else if (keyCode == Keyboard.KEY_UP) {
			targetY -= getScrollStep();
			return true;
		}

		return super.keyPressed(info, keyCode, character);
	}

	@Override
	public void tick() {
		super.tick();
		clamp();

		lastAnimatedY = animatedY;
		double multiplier = 0.6F;
		animatedY += (targetY - animatedY) * multiplier;
	}

	private void clamp() {
		targetY = MathHelper.clamp(targetY, 0, maxScrolling);
	}

	@Override
	protected boolean shouldScissor() {
		return true;
	}

	public abstract int getSpacing();

}
