package me.mcblueparrot.client.ui.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

import org.lwjgl.opengl.GL11;

import lombok.Getter;
import lombok.Setter;
import me.mcblueparrot.client.ui.component.controller.Controller;
import me.mcblueparrot.client.ui.component.handler.ClickHandler;
import me.mcblueparrot.client.ui.component.impl.ScrollListComponent;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

// 7 months later, I finally reintroduced the component API...
public abstract class Component {

	protected Minecraft mc = Minecraft.getMinecraft();
	protected Screen screen;
	protected Font font;
	@Getter
	@Setter
	protected Component parent;
	@Getter
	protected boolean hovered;
	protected ClickHandler onClick;
	protected Runnable onMouseEnter;
	protected Runnable onMouseExit;
	protected List<Component> subComponents = new ArrayList<>();
	private Map<Component, Controller<Rectangle>> subComponentControllers = new HashMap<>();

	public void add(Component component, Controller<Rectangle> position) {
		subComponents.add(component);
		subComponentControllers.put(component, position);
		component.setParent(this);

		if(screen != null) {
			component.screen = screen;
		}

		if(font != null) {
			component.font = font;
		}
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

	public void remove(Component component) {
		subComponents.remove(component);
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

		if(parent != null && parent.shouldScissor()) {
			hovered = hovered && parent.isHovered();
		}

		for(Component component : subComponents) {
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
		if(forEachHoveredSubComponent(info, (component, transformedInfo) -> component.keyPressed(transformedInfo, keyCode, character))) {
			return true;
		}

		return false;
	}

	/**
	 * @return <code>true</code> if the event has been processed.
	 */
	public boolean mouseClicked(ComponentRenderInfo info, int button) {
		if(forEachHoveredSubComponent(info, (component, transformedInfo) -> component.mouseClicked(transformedInfo, button))) {
			return true;
		}

		if(onClick != null && onClick.onClick(button)) {
			return true;
		}

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

	public Component onMouseEnter(Runnable onMouseEnter) {
		this.onMouseEnter = onMouseEnter;
		return this;
	}

	public Component onMouseExit(Runnable onMouseExit) {
		this.onMouseExit = onMouseExit;
		return this;
	}

}
