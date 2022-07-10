package io.github.solclient.client.ui.component;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.lwjgl.opengl.GL11;

import io.github.solclient.client.platform.mc.MinecraftClient;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.controller.Controller;
import io.github.solclient.client.ui.component.handler.ClickHandler;
import io.github.solclient.client.ui.component.handler.KeyHandler;
import io.github.solclient.client.ui.component.impl.ScrollListComponent;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import io.github.solclient.client.util.font.Font;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

// 7 months later, I finally reintroduced the component API...
public abstract class Component {

	protected MinecraftClient mc = MinecraftClient.getInstance();
	protected Screen screen;
	@Getter
	protected Font font;
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
	private ClickHandler onClickAnywhere;
	private ClickHandler onReleaseAnywhere;
	private Controller<Boolean> visibilityController;

	public void add(Component component, Controller<Rectangle> position) {
		subComponents.add(component);
		subComponentControllers.put(component, position);

		if(screen != null) {
			component.setScreen(screen);
		}

		if(font != null) {
			component.setFont(font);
		}

		component.setParent(this);
	}

	public void remove(Component component) {
		subComponents.remove(component);
	}

	public void clear() {
		subComponents.clear();
	}

	public void setFont(Font font) {
		this.font = font;

		for(Component component : subComponents) {
			component.setFont(font);
		}
	}

	public void setScreen(Screen screen) {
		this.screen = screen;

		for(Component component : subComponents) {
			component.setScreen(screen);
		}
	}

	public Rectangle getRelativeBounds() {
		return new Rectangle(0, 0, getBounds().getWidth(), getBounds().getHeight());
	}

	public Rectangle getBounds() {
		return parent.getBounds(this);
	}

	public Rectangle getBounds(Component component) {
		return subComponentControllers.get(component).get(component, component.getDefaultBounds());
	}

	private static ComponentRenderInfo transform(ComponentRenderInfo info, Rectangle bounds) {
		return new ComponentRenderInfo(info.getRelativeMouseX() - bounds.getX(),
				info.getRelativeMouseY() - bounds.getY(), info.getPartialTicks());
	}

	public void render(ComponentRenderInfo info) {
		ComponentRenderInfo actualInfo = info;

		if(this instanceof ScrollListComponent) {
			actualInfo = ((ScrollListComponent) this).reverseTranslation(info);
		}

		hovered = actualInfo.getRelativeMouseX() > 0 && actualInfo.getRelativeMouseY() > 0
				&& actualInfo.getRelativeMouseX() < getBounds().getWidth()
				&& actualInfo.getRelativeMouseY() < getBounds().getHeight();

		if(parent != null) {
			hovered = hovered && (parent.isHovered() || parent.dialog == this);
		}

		if(dialog != null) {
			hovered = false;
		}

		for(Component component : subComponents) {
			if(component == dialog && dialog != null) {
				drawDialogOverlay();
			}

			if(component.shouldSkip() || (shouldScissor() && shouldCull(component))) {
				continue;
			}

			Rectangle bounds = getBounds(component);

			GlStateManager.pushMatrix();
			GlStateManager.translate(bounds.getX(), bounds.getY(), 0);

			if(component.shouldScissor()) {
				GL11.glEnable(GL11.GL_SCISSOR_TEST);
				Utils.scissor(bounds);
			}

			component.render(transform(info, bounds));

			GlStateManager.popMatrix();

			if(component.shouldScissor()) {
				GL11.glDisable(GL11.GL_SCISSOR_TEST);
			}
		}

		if(dialog == null) {
			drawDialogOverlay();
		}
	}

	private boolean shouldSkip() {
		return visibilityController != null && !visibilityController.get(this, true);
	}

	protected boolean shouldCull(Component component) {
		if(component.getBounds().getEndY() < getBounds().getY()) {
			return true;
		}
		else if(component.getBounds().getY() > getBounds().getHeight()) {
			return true;
		}

		return false;
	}

	private void drawDialogOverlay() {
		GlStateManager.color(1, 1, 1);
		Gui.drawRect(0, 0, screen.width, screen.height, overlayColour.get(this, Colour.WHITE).getValue());
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
		if(onKeyPressed != null && onKeyPressed.keyPressed(info, keyCode, character)) {
			return true;
		}

		for(Component component : subComponents) {
			if(component.shouldSkip()) {
				continue;
			}

			if(component.keyPressed(transform(info, getBounds(component)), keyCode, character)) {
				return true;
			}
		}

		return false;
	}

	public boolean mouseClickedAnywhere(ComponentRenderInfo info, int button, boolean inside, boolean processed) {
		if(dialog != null) {
			boolean insideDialog = dialog.getBounds().contains(info.getRelativeMouseX(), info.getRelativeMouseY());

			if(dialog.mouseClickedAnywhere(transform(info, dialog.getBounds()), button, insideDialog, processed)) {
				processed = true;
			}
			else if(!insideDialog && button == 0) {
				setDialog(null);
			}

			return processed;
		}

		if(!processed && onClickAnywhere != null && onClickAnywhere.onClick(info, button)) {
			processed = true;
		}

		try {
			for(Component component : subComponents) {
				if(component.shouldSkip()) {
					continue;
				}

				Rectangle bounds = getBounds(component);

				if(component.mouseClickedAnywhere(transform(info, bounds), button, inside && bounds.contains(info.getRelativeMouseX(), info.getRelativeMouseY()), processed)) {
					processed = true;
				}
			}
		}
		catch(ConcurrentModificationException error) {
		}

		if(inside && onClick != null && onClick.onClick(info, button)) {
			processed = true;
		}

		if(inside && !processed && mouseClicked(info, button)) {
			processed = true;
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
		if(dialog != null) {
			if(dialog.mouseReleasedAnywhere(transform(info, dialog.getBounds()), button, dialog.getBounds().contains(info.getRelativeMouseX(), info.getRelativeMouseY()))) {
				return true;
			}
		}

		if(onReleaseAnywhere != null && onReleaseAnywhere.onClick(info, button)) {
			return true;
		}

		for(Component component : subComponents) {
			if(component.shouldSkip()) {
				continue;
			}

			Rectangle bounds = getBounds(component);

			if(component.mouseReleasedAnywhere(transform(info, bounds), button, bounds.contains(info.getRelativeMouseX(), info.getRelativeMouseY()))) {
				return true;
			}
		}

		if(inside && onRelease != null && onRelease.onClick(info, button)) {
			return true;
		}

		if(inside && mouseReleased(info, button)) {
			return true;
		}

		return false;
	}

	/**
	 * @return <code>true</code> if the event has been processed.
	 */
	public boolean mouseReleased(ComponentRenderInfo info, int button) {
		return false;
	}

	private boolean forEachHoveredSubComponent(ComponentRenderInfo info, BiPredicate<Component, ComponentRenderInfo> action) {
		for(Component component : subComponents) {
			Rectangle bounds = getBounds(component);

			if(!bounds.contains(info.getRelativeMouseX(), info.getRelativeMouseY())) {
				continue;
			}

			if(action.test(component, transform(info, bounds))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return <code>true</code> if the event has been processed.
	 */
	public boolean mouseScroll(ComponentRenderInfo info, int delta) {
		if(dialog != null) {
			return dialog.mouseScroll(transform(info, dialog.getBounds()), delta);
		}

		if(forEachHoveredSubComponent(info, (component, transformedInfo) -> component.mouseScroll(transformedInfo, delta))) {
			return true;
		}

		return false;
	}

	public void tick() {
		for(Component component : subComponents) {
			component.tick();
		}
	}

	public Screen getScreen() {
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

	public Component onKeyPressed(KeyHandler onKeyTyped) {
		this.onKeyPressed = onKeyTyped;
		return this;
	}

	public void setDialog(Component dialog) {
		if(this.dialog != null) {
			remove(this.dialog);
		}

		this.dialog = dialog;

		if(dialog != null) {
			add(dialog, new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE, (component, defaultBounds) -> defaultBounds));
		}
	}

	public Component visibilityController(Controller<Boolean> visibilityController) {
		this.visibilityController = visibilityController;
		return this;
	}

}
