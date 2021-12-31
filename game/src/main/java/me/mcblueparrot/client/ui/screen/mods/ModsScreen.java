package me.mcblueparrot.client.ui.screen.mods;

import java.awt.Desktop;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.Sys;

import lombok.SneakyThrows;
import me.mcblueparrot.client.Client;
import me.mcblueparrot.client.mod.CachedConfigOption;
import me.mcblueparrot.client.mod.ConfigOnlyMod;
import me.mcblueparrot.client.mod.Mod;
import me.mcblueparrot.client.mod.ModCategory;
import me.mcblueparrot.client.mod.PrimaryIntegerSettingMod;
import me.mcblueparrot.client.mod.annotation.Slider;
import me.mcblueparrot.client.mod.impl.SolClientMod;
import me.mcblueparrot.client.ui.element.Button;
import me.mcblueparrot.client.ui.element.TextField;
import me.mcblueparrot.client.ui.element.Tickbox;
import me.mcblueparrot.client.util.Utils;
import me.mcblueparrot.client.util.data.Colour;
import me.mcblueparrot.client.util.data.Rectangle;
import me.mcblueparrot.client.util.font.Font;
import me.mcblueparrot.client.util.font.SlickFontRenderer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

// TODO better GUI code
public class ModsScreen extends GuiScreen {

	private int amountScrolled = 0;
	private int previousAmountScrolled;
	private int maxScrolling;
	private GuiScreen previous;
	private boolean wasMouseDown;
	private boolean mouseDown;
	private boolean wasRightClickDown;
	private boolean rightClickDown;
	private boolean openedWithMod;
	private Mod selectedMod;
	private CachedConfigOption selectedColour;
	private Font font = SolClientMod.getFont();
	private TextField searchField = new TextField("Search", 25, 6, 100, true, () -> amountScrolled = 0);

	public ModsScreen(GuiScreen previous, Mod mod) {
		this.previous = previous;
		this.openedWithMod = true;
		this.selectedMod = mod;
	}

	public ModsScreen(GuiScreen previous) {
		this.previous = previous;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);

		if(mouseButton == 0) {
			mouseDown = true;
		}

		if(mouseButton == 1) {
			rightClickDown = true;
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		if(state == 0) {
			mouseDown = false;
		}

		if(state == 1) {
			rightClickDown = false;
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

			amountScrolled += (float) (dWheel * (selectedMod != null ? 26 : 35));
			amountScrolled = MathHelper.clamp_int(amountScrolled, 0, maxScrolling);
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		if(keyCode == Client.INSTANCE.modsKey.getKeyCode() && previous == null) {
			mc.displayGuiScreen(null);
		}
		else if(keyCode == Keyboard.KEY_RETURN) {
			if(selectedMod == null) {
				if(!searchField.getText().isEmpty()) {
					List<Mod> mods = ModCategory.ALL.getMods(searchField.getText());

					Utils.playClickSound();

					if(!mods.isEmpty()) {
						Mod mod = mods.get(0);

						if(!mod.isBlocked()) {
							if(mod.getOptions().size() == 1) {
								mod.toggle();
							}
							else {
								selectedMod = mod;
							}
						}
					}
				}
			}
			else {
				Utils.playClickSound();
				selectedMod.toggle();
			}
		}
		else if (selectedMod != null && selectedMod instanceof PrimaryIntegerSettingMod && (keyCode == Keyboard.KEY_MINUS
				|| keyCode == Keyboard.KEY_EQUALS)) {
			PrimaryIntegerSettingMod mod = (PrimaryIntegerSettingMod) selectedMod;

			if(keyCode == Keyboard.KEY_MINUS) {
				mod.decrement();
			}
			else {
				mod.increment();
			}
		}
		else {
			searchField.keyPressed(keyCode, typedChar);
		}
	}

	@SneakyThrows
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(mc.theWorld == null) {
			drawRect(0, 0, width, height, new Colour(20, 20, 20).getValue());
		}
		else {
			drawWorldBackground(0);
		}

		boolean slickFont = font instanceof SlickFontRenderer;
		int sweetSpot = slickFont ? 5 : 6;

		GlStateManager.enableBlend();
		GlStateManager.color(1, 1, 1);
		mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_" +
				"search_" + Utils.getTextureScale() + ".png"));
		drawModalRectWithCustomSizedTexture(6, 5,
				0, 0, 16, 16, 16, 16);

		searchField.setFont(font);
		searchField.render(mouseX, mouseY);

		String title = "Mods";

		if(selectedMod != null) {
			title = selectedMod.getName();
		}

		font.renderString(title, (width / 2) - (font.getWidth(title) / 2), 15,
				-1);

		int y = 30;

		Mod newSelected = selectedMod;
		CachedConfigOption newSelectedColour = selectedColour;

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		Rectangle region = new Rectangle(0, 30, width, height - 60);
		Utils.scissor(region);

		boolean mouseInList = region.contains(mouseX, mouseY);

		y += 5;
		if(selectedMod == null) {
			for(ModCategory category : ModCategory.values()) {
				if(searchField.getText().isEmpty()) {
					if(category == ModCategory.ALL) {
						continue;
					}
				}
				else {
					if(category != ModCategory.ALL) {
						continue;
					}
				}

				String categoryTitle = category.toString();
				if(categoryTitle != null) {
					font.renderString(categoryTitle, width / 2 - font.getWidth(categoryTitle) / 2, y - amountScrolled, -1);
					y += 15;
				}

				for(Mod mod : category.getMods(searchField.getText())) {
					Rectangle rectangle = new Rectangle(width / 2 - 150, y - amountScrolled, 300, 30);
					boolean containsMouse = rectangle.contains(mouseX, mouseY) && region.contains(mouseX, mouseY);

					Colour fill = new Colour(0, 0, 0, 150);
					Colour outline;
					String description = mod.getDescription();
					if(mod.isBlocked()) {
						if(containsMouse) {
							outline = new Colour(255, 80, 80);
						}
						else {
							outline = new Colour(255, 0, 0);
						}
						description += " Blocked by current server.";
					}
					else if(mod.isEnabled()) {
						if(containsMouse) {
							outline = SolClientMod.instance.uiHover;
						}
						else {
							outline = SolClientMod.instance.uiColour;
						}
						if(mod.isLocked()) {
							description += mod.getLockMessage();
						}
					}
					else {
						if(containsMouse) {
							outline = new Colour(60, 60, 60);
						}
						else {
							outline = new Colour(50, 50, 50);
						}
					}
					Utils.drawRectangle(rectangle, fill);
					if(containsMouse) {
						GlStateManager.enableBlend();
						GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
						GL11.glColor3ub((byte) 200, (byte) 200, (byte) 200);

						mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_" +
								"settings_" + Utils.getTextureScale() + ".png"));
						boolean hasSettings = mod.getOptions().size() > 1 && !mod.isBlocked();
						Rectangle modSettingsBounds = new Rectangle(rectangle.getX() + rectangle.getWidth() - 20,
								rectangle.getY() + 7,
								16,
								16);
						boolean mouseInSettings = mod.isLocked() || modSettingsBounds.contains(mouseX, mouseY);

						if(hasSettings) {
							if(mouseInSettings) {
								GlStateManager.color(1F, 1F, 1F);
							}

							drawModalRectWithCustomSizedTexture(modSettingsBounds.getX(), modSettingsBounds.getY(),
									0, 0, 16, 16, 16, 16);
						}

						if(mouseInList) {
							if(mouseDown && !wasMouseDown) {
								Utils.playClickSound();
								if(mouseInSettings && hasSettings) {
									newSelected = mod;
								}
								else if(mod.isBlocked()) {
									URI blockedModPage;
									if((blockedModPage = Client.INSTANCE.detectedServer.getBlockedModPage()) != null) {
                    Utils.sendLauncherMessage("openUrl", blockedModPage.toString());
									}
								}
								else {
									mod.toggle();
								}
							}
							else if(hasSettings && rightClickDown && !wasRightClickDown) {
								Utils.playClickSound();
								newSelected = mod;
							}
						}
					}
					rectangle.stroke(outline);
					font.renderString(mod.getName(), rectangle.getX() + 6, rectangle.getY() + 4 + (slickFont ? 0 : 1), -1);
					font.renderString(description, rectangle.getX() + 6, rectangle.getY() + 16, 8421504);

					y += rectangle.getHeight() + 5;
				}
			}
			y += 31;
		}
		else {
			Rectangle colourSelectBox = null;
			for(CachedConfigOption option : selectedMod.getOptions()) {
				if(selectedMod instanceof ConfigOnlyMod && option.field.getName().equals("enabled")) {
					continue;
				}

				Rectangle rectangle = new Rectangle(width / 2 - 150, y - amountScrolled, 300, 21);
				Utils.drawRectangle(rectangle, new Colour(0, 0, 0, 150));
				font.renderString(option.name, rectangle.getX() + sweetSpot, rectangle.getY() + sweetSpot, -1);

				if(option.getType() == boolean.class) {
					Tickbox box = new Tickbox(rectangle.getX() + rectangle.getWidth() - 18, rectangle.getY() + 3,
							(boolean) option.getValue());
					box.render(mouseX, mouseY, rectangle.contains(mouseX, mouseY));

					if(rectangle.contains(mouseX, mouseY) && mouseDown && !wasMouseDown && mouseInList) {
						option.setValue(!(boolean) option.getValue());
						Utils.playClickSound();
					}
				}
				else if(option.getType().isEnum()) {
					String valueName = option.getValue().toString();

					Method method = option.getType().getMethod("values");
					method.setAccessible(true); // Why?
					Enum<?>[] values = (Enum<?>[]) method.invoke(null);

					Colour valueColour = new Colour(200, 200, 200);
					if(rectangle.contains(mouseX, mouseY)) {
						valueColour = Colour.WHITE;
					}

					int maxWidth = 0;

					for(Enum<?> value : values) {
						if(font.getWidth(value.toString()) > maxWidth) {
							maxWidth = (int) font.getWidth(value.toString());
						}
					}

					Rectangle textarea = new Rectangle(rectangle.getX() + rectangle.getWidth() - maxWidth - 10 - 12, rectangle.getY(), maxWidth + 10, rectangle.getHeight());

					font.renderString(valueName, textarea.getX() + (textarea.getWidth() / 2) - (font.getWidth(valueName) / 2),
							rectangle.getY() + sweetSpot,
							valueColour.getValue());

					Rectangle previousBounds = new Rectangle(
							textarea.getX() - 8,
							rectangle.getY() + 6, 8, 8);

					GlStateManager.enableBlend();

					boolean previous = GuiScreen.isShiftKeyDown() || previousBounds.contains(mouseX, mouseY);

					GlStateManager.color(1, 1, 1);

					if(!previousBounds.contains(mouseX, mouseY)) {
						GL11.glColor3ub((byte) 200, (byte) 200, (byte) 200);
					}

					mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_" +
							"previous_" + Utils.getTextureScale() + ".png"));
					drawModalRectWithCustomSizedTexture(previousBounds.getX(), previousBounds.getY(), 0, 0, 8, 8, 8, 8);

					Rectangle nextBounds = new Rectangle(
							rectangle.getX() + rectangle.getWidth() - 12,
							rectangle.getY() + 6, 8, 8);

					nextBounds.stroke(new Colour(0, 0, 0, 0)); // Good old OpenGL

					GlStateManager.enableBlend();
					GlStateManager.color(1, 1, 1);

					if(!nextBounds.contains(mouseX, mouseY)) {
						GL11.glColor3ub((byte) 200, (byte) 200, (byte) 200);
					}

					mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/sol_client_" +
							"next_" + Utils.getTextureScale() + ".png"));
					drawModalRectWithCustomSizedTexture(nextBounds.getX(), nextBounds.getY(), 0, 0, 8, 8, 8, 8);

					if(rectangle.contains(mouseX, mouseY) && mouseDown && !wasMouseDown && mouseInList) {
						int ordinal = ((Enum<?>) option.getValue()).ordinal();
						try {
							if(previous) {
								if(--ordinal < 0) {
									ordinal = values.length - 1;
								}
							}
							else if(++ordinal > values.length - 1) {
								ordinal = 0;
							}

							option.setValue(values[ordinal]);
						}
						catch(IllegalArgumentException | SecurityException error) {
							throw new IllegalStateException(error);
						}
						Utils.playClickSound();
					}
				}
				else if(option.getType() == Colour.class) {
					Colour colourValue = (Colour) option.getValue();

					Rectangle colourBox = new Rectangle(rectangle.getX() + rectangle.getWidth() - 18, rectangle.getY() + 3,
							15,
							15);
					colourBox.fill(colourValue);
					colourBox.stroke(rectangle.contains(mouseX, mouseY) ? new Colour(120, 120, 120) : new Colour(100,
							100,
							100));

					if(rectangle.contains(mouseX, mouseY) && mouseDown && !wasMouseDown && mouseInList) {
						if(newSelectedColour != option) {
							newSelectedColour = option;
						}
						else {
							newSelectedColour = null;
						}
						Utils.playClickSound();
					}

					if(selectedColour == option) {
						colourSelectBox = new Rectangle(rectangle.getX(), rectangle.getY() + rectangle.getHeight() + 1, 300,
								option.common ? 120 : 100);
						if(!colourSelectBox.contains(mouseX, mouseY) && !rectangle.contains(mouseX, mouseY) && mouseDown && !wasMouseDown && mouseInList) {
							newSelectedColour = null;
						}
						else {
							y += colourSelectBox.getHeight() + 1;
						}
					}
				}
				else if(option.getType() == float.class
						&& option.field.isAnnotationPresent(Slider.class)) {
					Slider slider = option.field.getAnnotation(Slider.class);
					float min = slider.min();
					float max = slider.max();
					float step = slider.step();

					Colour sliderColour = new Colour(200, 200, 200);
					if(rectangle.contains(mouseX, mouseY)) {
						sliderColour = Colour.WHITE;
					}

					Rectangle sliderBox = new Rectangle(rectangle.getX() + rectangle.getWidth() - 109,
							rectangle.getY() + 9, 104, 2);
					Utils.drawRectangle(sliderBox, sliderColour);

					float percentage = ((float) option.getValue() - min) / (max - min);
					int px = (int) (sliderBox.getX() + (percentage * 100));

					Rectangle sliderScrubber = new Rectangle(px, sliderBox.getY() - 4, 4, 10);
					Utils.drawRectangle(sliderScrubber, sliderColour);
					String valueText = new DecimalFormat("0.##").format(option.getValue());

					if(!slider.suffix().isEmpty()) {
						valueText += slider.suffix();
					}

					if(slider.showValue()) {
						font.renderString(valueText, sliderBox.getX() - font.getWidth(valueText) - 4, sliderScrubber.getY() + (slickFont ? 0 : 1),
								sliderColour.getValue());
					}

					if(rectangle.contains(mouseX, mouseY)) {
						if(mouseDown && mouseInList) {
							if(!wasMouseDown) {
								Utils.playClickSound();
							}
							if(mouseX < sliderBox.getX()) {
								option.setValue(min);
							}
							else if(mouseX > sliderBox.getX() + sliderBox.getWidth()) {
								option.setValue(max);
							}
							else {
								for(float value = min; value < max + step; value += step) {
									Rectangle bounds =
											new Rectangle(
													(int) (sliderBox.getX() + ((value - min) / (max - min) * 100)),
													rectangle.getY(), 1000, rectangle.getHeight());

									if(bounds.contains(mouseX, mouseY)) {
										option.setValue(value);
									}
								}
							}
						}
					}
				}

				y += 26;
			}
			y += 31;
			if(colourSelectBox != null) {
				Colour selectedColour = ((Colour) this.selectedColour.getValue());
				Utils.drawRectangle(colourSelectBox, new Colour(30, 30, 30));
				for(int componentIndex = 0; componentIndex < 4; componentIndex++) {
					int componentValue = selectedColour.getComponents()[componentIndex];
					Rectangle componentBox = new Rectangle(colourSelectBox.getX() + 34,
							colourSelectBox.getY() + 19 + (20 * componentIndex), 255, 10);

					if (new Rectangle(colourSelectBox.getX(), componentBox.getY(), colourSelectBox.getWidth(),
							componentBox.getHeight()).contains(mouseX, mouseY) && mouseDown && mouseInList) {
						int clickedPosition = MathHelper.clamp_int(mouseX - componentBox.getX(), 0, 255);
						int r = componentIndex == 0 ? clickedPosition : selectedColour.getRed();
						int g = componentIndex == 1 ? clickedPosition : selectedColour.getGreen();
						int b = componentIndex == 2 ? clickedPosition : selectedColour.getBlue();
						int a = componentIndex == 3 ? clickedPosition : selectedColour.getAlpha();

						if(!wasMouseDown) {
							Utils.playClickSound();
						}

						this.selectedColour.setValue(new Colour(r, g, b, a));
					}
					String name = "Red";
					switch(componentIndex) {
						case 1:
							name = "Green";
							break;
						case 2:
							name = "Blue";
							break;
						case 3:
							name = "Alpha";
					}
					font.renderString(name, componentBox.getX() - font.getWidth(name) - 5 + (slickFont ? 0 : 3), componentBox.getY() - (slickFont ? 1 : 0), -1);
					if(componentIndex == 3) {
						Utils.drawRectangle(componentBox, Colour.BLACK);
					}
					for(int colour = 0; colour < 256; colour += 1) {
						Colour renderColour = null;
						switch(componentIndex) {
							case 0:
								renderColour = new Colour(colour, 0, 0);
								break;
							case 1:
								renderColour = new Colour(0, colour, 0);
								break;
							case 2:
								renderColour = new Colour(0, 0, colour);
								break;
							case 3:
								renderColour = new Colour(0, 255, 255, colour);
						}
						if(colour == componentValue) {
							renderColour = Colour.WHITE;
							font.renderString(Integer.toString(colour),
									componentBox.getX() + colour - (font.getWidth(Integer.toString(colour)) / 2),
									componentBox.getY() + 9 + (slickFont ? 0 : 2), 0x777777);
						}
						Utils.drawRectangle(
								new Rectangle(componentBox.getX() + colour,
										componentBox.getY(), 1, 10),
								renderColour);
					}
				}

				font.renderString("Select Colour (RGBA)",
						colourSelectBox.getX() + (colourSelectBox.getWidth() / 2) - (font.getWidth("Select Colour " +
								"(RGBA)") / 2),
						colourSelectBox.getY() + 5, -1);

				if(this.selectedColour.common) {
					Button applyToAllButton = new Button(font, "Apply to All",
							new Rectangle(colourSelectBox.getX() + (colourSelectBox.getWidth() / 2) - 50,
									colourSelectBox.getY() + colourSelectBox.getHeight() - 20, 100, 15),
							new Colour(0, 150, 255), new Colour(30, 180, 255));
	                applyToAllButton.render(mouseX, mouseY);

	                if(applyToAllButton.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
	                	Utils.playClickSound();

	                	for(Mod mod : Client.INSTANCE.getMods()) {
	                		for(CachedConfigOption option : mod.getOptions()) {
	                			if(option.name.equals(this.selectedColour.name)) {
	                				option.setValue(selectedColour);
	                			}
	                		}
	                	}
	                }
				}
			}
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		drawHorizontalLine(0, width, 29, 0xFF000000);
		drawHorizontalLine(0, width, height - 31, 0xFF000000);
		Button done = new Button(font, "Done", new Rectangle(openedWithMod ? width / 2 - 50 : width / 2 - 103, height - 25, 100, 20), new Colour(0, 255, 0),
				new Colour(150, 255, 150));
		done.render(mouseX, mouseY);

		if(done.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
			Utils.playClickSound();
			if(openedWithMod) {
				mc.displayGuiScreen(previous);
				return;
			}
			if(selectedMod == null) {
				mc.displayGuiScreen(previous);
			}
			else {
				newSelected = null;
				newSelectedColour = null;
			}
		}

		if(!openedWithMod) {
			Button edit = new Button(font, "HUD Editor", new Rectangle(width / 2 + 3, height - 25, 100, 20), new Colour(255, 150, 0),
					new Colour(255, 190, 40));
			edit.render(mouseX, mouseY);
			if(edit.contains(mouseX, mouseY) && mouseDown && !wasMouseDown) {
				Utils.playClickSound();
				mc.displayGuiScreen(new MoveHudsScreen(this, previous instanceof GuiMainMenu ? previous : null));
			}
		}

		wasMouseDown = mouseDown;
		wasRightClickDown = rightClickDown;

		maxScrolling = (y) - (height);
		if(maxScrolling < 0) {
			maxScrolling = 0;
		}
		amountScrolled = MathHelper.clamp_int(amountScrolled, 0, maxScrolling);

		if(newSelected != selectedMod) {
			selectedMod = newSelected;
			mouseDown = false;
			wasMouseDown = false;
			if(newSelected == null) {
				amountScrolled = previousAmountScrolled;
			}
			else {
				previousAmountScrolled = amountScrolled;
				amountScrolled = 0;
			}
		}

		selectedColour = newSelectedColour;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Client.INSTANCE.save();
	}

	public void updateFont() {
		font = SolClientMod.getFont();
	}

	public void switchMod(Mod mod) {
		mouseDown = false;
		wasMouseDown = false;
		selectedMod = mod;
	}

	//    @Override
//    public void updateScreen() {
//        super.updateScreen();
//        if(previous instanceof GuiMainMenu) {
//            previous.updateScreen();
//        }
//    }

}
