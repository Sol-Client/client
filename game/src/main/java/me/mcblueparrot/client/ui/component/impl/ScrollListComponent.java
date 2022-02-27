package me.mcblueparrot.client.ui.component.impl;

import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.component.Component;
import me.mcblueparrot.client.ui.component.ComponentRenderInfo;
import me.mcblueparrot.client.ui.component.controller.AlignedBoundsController;
import me.mcblueparrot.client.util.data.Alignment;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public abstract class ScrollListComponent extends Component {

	private double targetY;
	private double animatedY;
	private double lastAnimatedY;
	private double calculatedY;
	private int maxScrolling;
	private double scrollPercent;

	@Override
	public void setParent(Component parent) {
		super.setParent(parent);

		parent.add(new BlockComponent(Colour.LIGHT_BUTTON) {

			@Override
			public void render(ComponentRenderInfo info) {
				scrollPercent = (double) ScrollListComponent.this.getBounds().getHeight() / (double) getContentHeight();

				GlStateManager.translate(0, calculatedY * scrollPercent, 0);

				if(maxScrolling != 0) {
					super.render(info);
				}
			}

		}, (component, defaultBounds) -> {
			return new Rectangle(getBounds().getX() + getBounds().getWidth() - 5, getBounds().getY(), 3,
					(int) (getBounds().getHeight() * scrollPercent));
		});
	}

	public void add(Component component) {
		add(component, new AlignedBoundsController(Alignment.CENTRE, Alignment.START, (sizingComponent, defaultBounds) -> {
			Component lastAdded = null;
			Rectangle lastBounds = null;

			int index = subComponents.indexOf(component) - 1;

			if(index > -1) {
				lastAdded = subComponents.get(index);

				lastBounds = lastAdded.getBounds();
			}

			return new Rectangle(defaultBounds.getX(), lastAdded == null ? 0
					: lastBounds.getY() + lastBounds.getHeight() + getSpacing(),
					defaultBounds.getWidth(), defaultBounds.getHeight());
		}));
	}

	private ComponentRenderInfo translate(ComponentRenderInfo info) {
		return new ComponentRenderInfo(info.getRelativeMouseX(), (int) (info.getRelativeMouseY() + calculatedY), info.getPartialTicks());
	}

	public ComponentRenderInfo reverseTranslation(ComponentRenderInfo info) {
		return new ComponentRenderInfo(info.getRelativeMouseX(), (int) (info.getRelativeMouseY() - calculatedY), info.getPartialTicks());
	}

	@Override
	public void render(ComponentRenderInfo info) {
		if(!SolClientMod.instance.smoothScrolling) {
			calculatedY = targetY;
		}
		else {
			calculatedY = (lastAnimatedY + (animatedY - lastAnimatedY) * info.getPartialTicks());
		}

		GlStateManager.translate(0, -calculatedY, 0);
		maxScrolling = getContentHeight() - getBounds().getHeight();

		if(maxScrolling < 0) {
			maxScrolling = 0;
		}

		super.render(translate(info));
	}

	public void snapTo(int scroll) {
		targetY = animatedY = lastAnimatedY = calculatedY = maxScrolling = scroll;
	}

	public void jumpTo(int scroll) {
		targetY = scroll;
	}

	public int getScroll() {
		return (int) targetY;
	}

	private int getContentHeight() {
		return subComponents.size() == 0 ? 0 : subComponents.get(subComponents.size() - 1).getBounds().getEndY();
	}

	@Override
	public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		return super.mouseClickedAnywhere(translate(info), button, inside);
	}

	@Override
	public boolean mouseScroll(ComponentRenderInfo info, int delta) {
		if(super.mouseScroll(info, delta)) {
			return true;
		}

		delta = delta / Math.abs(delta);

		if(subComponents.size() != 0) {
			targetY -= delta * (subComponents.get(0).getBounds().getHeight() + getSpacing());
		}

		return true;
	}

	@Override
	public void tick() {
		super.tick();

		targetY = MathHelper.clamp_double(targetY, 0, maxScrolling);

		lastAnimatedY = animatedY;

		double multiplier = 0.6F;
		animatedY += (targetY - animatedY) * multiplier;
	}

	@Override
	protected boolean shouldScissor() {
		return true;
	}

	public abstract int getSpacing();

}
