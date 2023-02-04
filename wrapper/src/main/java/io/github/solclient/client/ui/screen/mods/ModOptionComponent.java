package io.github.solclient.client.ui.screen.mods;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.mod.option.*;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.MinecraftUtils;
import io.github.solclient.client.util.data.*;
import io.github.solclient.client.util.data.Modifier;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.*;
import net.minecraft.client.resource.language.I18n;

public class ModOptionComponent extends Component {

	@Getter
	private ModOption<?> option;
	private boolean listening;
	private int enumWidth;

	public ModOptionComponent(ModOption<?> option) {
		this.option = option;

		add(new LabelComponent(option.getName()), new AlignedBoundsController(Alignment.START, Alignment.CENTRE));

		Controller<Rectangle> defaultBoundController = new AlignedBoundsController(Alignment.END, Alignment.CENTRE);

		if (option.getType() == boolean.class) {
			ModOption<Boolean> booleanOption = option.unsafeCast();
			add(new ToggleComponent(booleanOption.getValue(), booleanOption::setValue), defaultBoundController);
		} else if (option.getType() == Colour.class) {
			ModOption<Colour> colourOption = option.unsafeCast();

			ColourBoxComponent colour = new ColourBoxComponent((component, defaultColour) -> colourOption.getValue());
			add(colour, defaultBoundController);

			add(new LabelComponent((component, defaultText) -> ((Colour) option.getValue()).toHexString()).scaled(0.8F),
					(component, defaultBounds) -> {
						Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
						return new Rectangle(
								(int) (getBounds().getWidth()
										- regularFont.getWidth(nvg, ((LabelComponent) component).getText()) - 12),
								defaultComponentBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
					});

			colour.onClick((info, button) -> {
				if (button != 0)
					return false;

				MinecraftUtils.playClickSound(true);
				screen.getRoot().setDialog(
						new ColourPickerDialog(colourOption, colourOption.getValue(), colourOption::setValue));
				return true;
			});
		} else if (option.getType() == KeyBinding.class) {
			KeyBinding binding = (KeyBinding) option.getValue();

			ButtonComponent editButton = new ButtonComponent(
					(component, defaultText) -> KeyBindingExtension.from(binding).getPrefix()
							+ GameOptions.getFormattedNameForKeyCode(binding.getCode()),
					theme.button(), (component, defaultColour) -> {
						if (listening)
							return new Colour(255, 255, 85);
						else if (MinecraftUtils.isConflicting(binding))
							return new Colour(255, 85, 85);

						return theme.fg;
					}).width(45).height(16);
			add(editButton, defaultBoundController);

			editButton.onClick((info, button) -> {
				if (button == 0) {
					MinecraftUtils.playClickSound(true);

					// local functions at home...
					Runnable postSet = () -> {
						listening = false;
						mc.options.save();
						KeyBinding.updateKeysByCode();
						screen.getRoot().onKeyPressed(null);
						screen.getRoot().onKeyReleased(null);
						screen.getRoot().onClickAnwhere(null);
					};

					listening = true;
					screen.getRoot().onClickAnwhere((ignoredInfo, pressedButton) -> {
						mc.options.setKeyBindingCode(binding, pressedButton - 100);
						KeyBindingExtension.from(binding).setMods(0);
						postSet.run();
						return true;
					});

					screen.getRoot().onKeyPressed((ignored, key, character) -> {
						if (Modifier.isModifier(key))
							return false;
						int mods = 0;

						if (key == 1)
							mc.options.setKeyBindingCode(binding, 0);
						else if (key != 0) {
							if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
								mods |= Modifier.CTRL;
							if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
								mods |= Modifier.ALT;
							if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
								mods |= Modifier.SHIFT;

							mc.options.setKeyBindingCode(binding, key);
						} else if (character > 0)
							mc.options.setKeyBindingCode(binding, character + 256);

						KeyBindingExtension.from(binding).setMods(mods);
						postSet.run();
						return true;
					});

					screen.getRoot().onKeyReleased((ignored, key, character) -> {
						if (!Modifier.isModifier(key))
							return false;

						mc.options.setKeyBindingCode(binding, key);
						KeyBindingExtension.from(binding).setMods(0);

						postSet.run();
						return true;
					});
					return true;
				}

				return false;
			});
		} else if (option.getType().isEnum()) {
			try {
				Method valuesField = option.getType().getMethod("values");
				valuesField.setAccessible(true);

				Enum<?>[] fields = (Enum<?>[]) valuesField.invoke(null);

				add(new LabelComponent((component, defaultText) -> option.getValue().toString(),
						new AnimatedColourController(
								(component, defaultColour) -> isHovered() ? theme.fgButtonHover : theme.fgButton)),
						(component, defaultBounds) -> {
							if (enumWidth == 0) {
								for (Enum<?> field : fields) {
									int newWidth = (int) regularFont.getWidth(nvg, field.toString());

									if (newWidth > enumWidth) {
										enumWidth = newWidth;
									}
								}
							}

							Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
							return new Rectangle(
									getBounds().getWidth() - enumWidth - 11 + (enumWidth / 2)
											- ((int) regularFont.getWidth(nvg, ((LabelComponent) component).getText())
													/ 2),
									defaultComponentBounds.getY(), defaultComponentBounds.getWidth(),
									defaultComponentBounds.getHeight());
						});

				Component previous;

				add(new IconComponent("next", 8, 8, new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? theme.fgButtonHover : theme.fgButton)),
						defaultBoundController);

				add(previous = new IconComponent("prev", 8, 8, new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? theme.fgButtonHover : theme.fgButton)),
						(component, defaultBounds) -> {
							Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
							return new Rectangle(defaultComponentBounds.getX() - enumWidth - 12,
									defaultComponentBounds.getY(), defaultComponentBounds.getWidth(),
									defaultComponentBounds.getHeight());
						});

				onClick((info, button) -> {
					if (button == 0) {
						MinecraftUtils.playClickSound(true);

						int current = ((Enum<?>) option.getValue()).ordinal();

						boolean direction = false;

						if (Screen.hasShiftDown())
							direction = !direction;
						if (previous.isHovered())
							direction = !direction;

						if (direction) {
							current--;

							if (current < 0) {
								current = fields.length - 1;
							}
						} else {
							current++;
							if (current > fields.length - 1) {
								current = 0;
							}
						}

						((ModOption<Object>) option).setValue(fields[current]);

						return true;
					}

					return false;
				});
			} catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException error) {
				throw new IllegalStateException(error);
			}
		} else if (option instanceof SliderOption) {
			SliderOption sliderOption = (SliderOption) option;

			if (sliderOption.shouldShowValue()) {
				add(new LabelComponent((component, defaultText) -> I18n.translate(sliderOption.getFormat(),
						new DecimalFormat("0.##").format(option.getValue()))).scaled(0.8F),
						(component, defaultBounds) -> {
							Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
							return new Rectangle(
									(int) (getBounds().getWidth()
											- regularFont.getWidth(nvg, ((LabelComponent) component).getText()) - 97),
									defaultComponentBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
						});
			}

			add(new SliderComponent(sliderOption.getMin(), sliderOption.getMax(), sliderOption.getStep(),
					(float) option.getValue(), (value) -> sliderOption.setValue(value)), (component, defaultBounds) -> {
						defaultBounds = defaultBoundController.get(component, defaultBounds);
						return new Rectangle(defaultBounds.getX() - 5, defaultBounds.getY(), defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});
		} else if (option instanceof FileOption) {
			FileOption fileOption = (FileOption) option;

			ButtonComponent editFile = new ButtonComponent(
					(component, defaultText) -> I18n.translate(fileOption.getEditText()), theme.button(), theme.fg())
					.width(50).height(16);
			add(editFile, defaultBoundController);

			editFile.onClick((info, button) -> {
				if (button == 0) {
					MinecraftUtils.playClickSound(true);
					try {
						MinecraftUtils.openUrl(fileOption.getPath().toUri().toURL().toString());
					} catch (MalformedURLException error) {
						throw new IllegalStateException(error);
					}
					return true;
				}

				return false;
			});
		} else if (option instanceof TextOption) {
			TextOption textOption = (TextOption) option;

			TextFieldComponent field = new TextFieldComponent(100, false).withPlaceholder(textOption.getPlaceholder())
					.onUpdate((string) -> {
						textOption.setValue(string);
						return true;
					});
			field.autoFlush();
			field.setText(textOption.getValue());
			add(field, defaultBoundController);
		} else if (option.getType() == PixelMatrix.class) {
			PixelMatrixComponent matrix = new PixelMatrixComponent((PixelMatrix) option.getValue());
			matrix.onClick((info, button) -> {
				if (button != 0)
					return false;

				MinecraftUtils.playClickSound(true);
				screen.getRoot().setDialog(new PixelMatrixDialog(option.unsafeCast()));
				return true;
			});

			add(matrix, defaultBoundController);
		}
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 20);
	}

}
