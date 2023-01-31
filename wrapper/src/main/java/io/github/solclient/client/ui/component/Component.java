package io.github.solclient.client.ui.component;

import java.util.*;
import java.util.function.BiPredicate;

import org.lwjgl.nanovg.NanoVG;

import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.handler.*;
import io.github.solclient.client.ui.component.impl.ScrollListComponent;
import io.github.solclient.client.util.NanoVGManager;
import io.github.solclient.client.util.data.*;
import lombok.*;
import net.minecraft.client.MinecraftClient;

// 7 months later, I finally reintroduced the component API...
public abstract class Component extends NanoVGManager {

	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected ComponentScreen screen;
	@Getter
	@Setter
	protected Component parent;
	@Getter
	protected boolean hovered;
	protected ClickHandler onClick;
	protected Runnable onMouseEnter;
	protected Runnable onMouseExit;
	@Getter
	protected List<Component> subComponents = new ArrayList<>();
	private Map<Component, Controller<Rectangle>> subComponentControllers = new HashMap<>();
	@Getter
	private Component dialog;
	private AnimatedColourController overlayColour = new AnimatedColourController(
			(component, defaultColour) -> dialog == null ? Colour.TRANSPARENT : new Colour(0, 0, 0, 150));
	private ClickHandler onRelease;
	private KeyHandler onKeyPressed;
	private KeyHandler onKeyReleased;
	private ClickHandler onClickAnywhere;
	private ClickHandler onReleaseAnywhere;
	private Controller<Boolean> visibilityController;
	private Rectangle cachedBounds;

	public void add(Component component, Controller<Rectangle> position) {
		subComponents.add(component);
		register(component, position);
	}

	public void add(int index, Component component, Controller<Rectangle> position) {
		subComponents.add(index, component);
		register(component, position);
	}

	private void register(Component component, Controller<Rectangle> position) {
		subComponentControllers.put(component, position);

		if (screen != null) {
			component.setScreen(screen);
		}

		component.setParent(this);
	}

	public void remove(Component component) {
		subComponents.remove(component);
		subComponentControllers.remove(component);
		component.setParent(null);
	}

	public void remove(int index) {
		subComponentControllers.remove(subComponents.remove(index));
	}

	public void clear() {
		subComponents.clear();
	}

	public void setScreen(ComponentScreen screen) {
		this.screen = screen;

		for (Component component : subComponents) {
			component.setScreen(screen);
		}
	}

	public Rectangle getRelativeBounds() {
		return new Rectangle(0, 0, getBounds().getWidth(), getBounds().getHeight());
	}

	public Rectangle getBounds() {
		if (parent == null)
			throw new IllegalArgumentException("Parent of " + this + " is null");

		return cachedBounds = parent.getBounds(this);
	}

	public Rectangle getCachedBounds() {
		if (cachedBounds == null)
			return getBounds();

		return cachedBounds;
	}

	public Rectangle getBounds(Component component) {
		if (!subComponentControllers.containsKey(component))
			throw new IllegalArgumentException(component + " is not a child of " + this);

		Controller<Rectangle> controller = subComponentControllers.get(component);
		if (controller == null)
			return component.getDefaultBounds();

		return controller.get(component, component.getDefaultBounds());
	}

	private static ComponentRenderInfo transform(ComponentRenderInfo info, Rectangle bounds) {
		return new ComponentRenderInfo(info.mouseX() - bounds.getX(),
				info.mouseY() - bounds.getY(), info.tickDelta());
	}

	public void render(ComponentRenderInfo info) {
		ComponentRenderInfo actualInfo = info;

		if (this instanceof ScrollListComponent) {
			actualInfo = ((ScrollListComponent) this).reverseTranslation(info);
		}

		hovered = actualInfo.mouseX() > 0 && actualInfo.mouseY() > 0
				&& actualInfo.mouseX() < getBounds().getWidth()
				&& actualInfo.mouseY() < getBounds().getHeight();

		if (parent != null) {
			hovered = hovered && (parent.isHovered() || parent.dialog == this);
		}

		if (dialog != null) {
			hovered = false;
		}

		for (Component component : subComponents) {
			if (component == dialog && dialog != null) {
				drawDialogOverlay();
			}

			if (component.shouldSkip() || (shouldScissor() && shouldCull(component))) {
				continue;
			}

			Rectangle bounds = getBounds(component);

			NanoVG.nvgSave(nvg);
			NanoVG.nvgTranslate(nvg, bounds.getX(), bounds.getY());

			if (component.shouldScissor()) {
				NanoVG.nvgScissor(nvg, 0, 0, bounds.getWidth(), bounds.getHeight());
			}

			component.render(transform(info, bounds));

			NanoVG.nvgRestore(nvg);
		}

		if (dialog == null) {
			drawDialogOverlay();
		}
	}

	private boolean shouldSkip() {
		return visibilityController != null && !visibilityController.get(this, true);
	}

	protected boolean shouldCull(Component component) {
		if (component.getBounds().getEndY() < getBounds().getY()) {
			return true;
		} else if (component.getBounds().getY() > getBounds().getHeight()) {
			return true;
		}

		return false;
	}

	private void drawDialogOverlay() {
		NanoVG.nvgBeginPath(nvg);
		NanoVG.nvgFillColor(nvg, overlayColour.get(this, Colour.WHITE).nvg());
		NanoVG.nvgRect(nvg, 0, 0, screen.width, screen.height);
		NanoVG.nvgFill(nvg);
	}

	protected Rectangle getDefaultBounds() {
		return Rectangle.ZERO;
	}

	protected boolean shouldScissor() {
		return false;
	}

	/**
	 * @return <code>true</code> if event has been processed.
	 */
	public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
		if (onKeyPressed != null && onKeyPressed.onKey(info, keyCode, character))
			return true;

		for (Component component : subComponents) {
			if (component.shouldSkip())
				continue;

			if (component.keyPressed(transform(info, getBounds(component)), keyCode, character))
				return true;
		}

		return false;
	}

	/**
	 * @return <code>true</code> if event has been processed.
	 */
	public boolean keyReleased(ComponentRenderInfo info, int keyCode, char character) {
		if (onKeyReleased != null && onKeyReleased.onKey(info, keyCode, character))
			return true;

		for (Component component : subComponents) {
			if (component.shouldSkip())
				continue;

			if (component.keyReleased(transform(info, getBounds(component)), keyCode, character))
				return true;
		}

		return false;
	}

	public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
		if (dialog != null) {
			boolean insideDialog = dialog.getBounds().contains(info.mouseX(), info.mouseY());

			if (dialog.mouseClickedAnywhere(transform(info, dialog.getBounds()), button, insideDialog, processed))
				processed = true;
			else if (!insideDialog && button == 0)
				setDialog(null);

			return processed;
		}

		if (!processed && onClickAnywhere != null && onClickAnywhere.onClick(info, button))
			processed = true;

		try {
			for (Component component : subComponents) {
				if (component.shouldSkip())
					continue;

				Rectangle bounds = getBounds(component);

				if (component.mouseClickedAnywhere(transform(info, bounds), button,
						inside && bounds.contains(info.mouseX(), info.mouseY()), processed))
					return true;
			}
		} catch (ConcurrentModificationException error) {
			// ArGHHHHHHH
		}

		if (inside) {
			if (onClick != null && onClick.onClick(info, button))
				return true;

			if (mouseClicked(info, button))
				return true;
		}

		return processed;
	}

	/**
	 * @return <code>true</code> if the event has been processed.
	 */
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		return false;
	}

	public boolean mouseReleasedAnywhere(ComponentRenderInfo info, int button, boolean inside) {
		if (dialog != null && dialog.mouseReleasedAnywhere(transform(info, dialog.getBounds()), button,
				dialog.getBounds().contains(info.mouseX(), info.mouseY())))
			return true;

		if (onReleaseAnywhere != null && onReleaseAnywhere.onClick(info, button))
			return true;

		for (Component component : subComponents) {
			if (component.shouldSkip())
				continue;

			Rectangle bounds = getBounds(component);

			if (component.mouseReleasedAnywhere(transform(info, bounds), button,
					bounds.contains(info.mouseX(), info.mouseY())))
				return true;
		}

		if (inside && onRelease != null && onRelease.onClick(info, button))
			return true;

		if (inside && mouseReleased(info, button))
			return true;

		return false;
	}

	/**
	 * @return <code>true</code> if the event has been processed.
	 */
	public boolean mouseReleased(ComponentRenderInfo info, int button) {
		return false;
	}

	private boolean forEachHoveredSubComponent(ComponentRenderInfo info,
			BiPredicate<Component, ComponentRenderInfo> action) {
		for (Component component : subComponents) {
			Rectangle bounds = getBounds(component);

			if (!bounds.contains(info.mouseX(), info.mouseY()))
				continue;

			if (action.test(component, transform(info, bounds)))
				return true;
		}

		return false;
	}

	/**
	 * @return <code>true</code> if the event has been processed.
	 */
	public boolean mouseScroll(ComponentRenderInfo info, int delta) {
		if (dialog != null)
			return dialog.mouseScroll(transform(info, dialog.getBounds()), delta);

		if (forEachHoveredSubComponent(info,
				(component, transformedInfo) -> component.mouseScroll(transformedInfo, delta)))
			return true;

		return false;
	}

	public void tick() {
		for (Component component : subComponents)
			component.tick();
	}

	public ComponentScreen getScreen() {
		return screen;
	}

	public Component onClick(ClickHandler onClick) {
		this.onClick = onClick;
		return this;
	}

	public Component onClickAnwhere(ClickHandler onClickAnywhere) {
		this.onClickAnywhere = onClickAnywhere;
		return this;
	}

	public Component onRelease(ClickHandler onRelease) {
		this.onRelease = onRelease;
		return this;
	}

	public Component onReleaseAnywhere(ClickHandler onReleaseAnywhere) {
		this.onReleaseAnywhere = onReleaseAnywhere;
		return this;
	}

	public Component onKeyPressed(KeyHandler onKeyPressed) {
		this.onKeyPressed = onKeyPressed;
		return this;
	}

	public Component onKeyReleased(KeyHandler onKeyReleased) {
		this.onKeyReleased = onKeyReleased;
		return this;
	}

	public void setDialog(Component dialog) {
		if (this.dialog != null)
			remove(this.dialog);

		if (dialog != null)
			add(dialog, new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
					(component, defaultBounds) -> defaultBounds));

		this.dialog = dialog;
	}

	public Component visibilityController(Controller<Boolean> visibilityController) {
		this.visibilityController = visibilityController;
		return this;
	}

}
