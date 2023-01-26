package io.github.solclient.client.ui.screen.mods;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.BitSet;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.extension.KeyBindingExtension;
import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.mod.annotation.Slider;
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
	private ModOption option;
	private boolean listening;
	private int enumWidth;

	public ModOptionComponent(ModOption option) {
		this.option = option;

		add(new LabelComponent(option.getName()),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		Controller<Rectangle> defaultBoundController = new AlignedBoundsController(Alignment.END, Alignment.CENTRE,
				(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - defaultBounds.getY(),
						defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight()));

		if (option.getType() == boolean.class) {
			add(new ToggleComponent((boolean) option.getValue(), option::setValue), defaultBoundController);
		} else if (option.getType() == Colour.class) {
			ColourBoxComponent colour = new ColourBoxComponent(
					(component, defaultColour) -> (Colour) option.getValue());
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
				if (button != 0) {
					return false;
				}

				MinecraftUtils.playClickSound(true);
				screen.getRoot()
						.setDialog(new ColourPickerDialog(option, (Colour) option.getValue(), option::setValue));
				return true;
			});
		} else if (option.getType() == KeyBinding.class) {
			KeyBinding binding = (KeyBinding) option.getValue();

			add(new LabelComponent(
					(component, defaultText) -> KeyBindingExtension.from(binding).getPrefix()
							+ GameOptions.getFormattedNameForKeyCode(binding.getCode()),
					new AnimatedColourController((component, defaultColour) -> {
						if (listening)
							return new Colour(255, 255, 85);
						else if (MinecraftUtils.isConflicting(binding))
							return new Colour(255, 85, 85);

						return isHovered() ? theme.activeHover : theme.active;
					})), defaultBoundController);

			onClick((info, button) -> {
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
								(component, defaultColour) -> isHovered() ? theme.activeHover : theme.active)),
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
									getBounds().getWidth() - enumWidth - 16 + (enumWidth / 2)
											- ((int) regularFont.getWidth(nvg, ((LabelComponent) component).getText())
													/ 2),
									defaultComponentBounds.getY(), defaultComponentBounds.getWidth(),
									defaultComponentBounds.getHeight());
						});

				Component previous;

				add(new IconComponent("sol_client_next", 8, 8, new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? theme.activeHover : theme.active)),
						defaultBoundController);

				add(previous = new IconComponent("sol_client_previous", 8, 8, new AnimatedColourController(
						(component, defaultColour) -> component.isHovered() ? theme.activeHover : theme.active)),
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

						option.setValue(fields[current]);

						return true;
					}

					return false;
				});
			} catch (NoSuchMethodException | IllegalArgumentException | IllegalAccessException
					| InvocationTargetException error) {
				throw new IllegalStateException(error);
			}
		} else if (option.getType() == float.class && option.getField().isAnnotationPresent(Slider.class)) {
			Slider sliderAnnotation = option.getField().getAnnotation(Slider.class);

			if (sliderAnnotation.showValue()) {
				add(new LabelComponent((component, defaultText) -> I18n.translate(sliderAnnotation.format(),
						new DecimalFormat("0.##").format(option.getValue()))).scaled(0.8F),
						(component, defaultBounds) -> {
							Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
							return new Rectangle(
									(int) (getBounds().getWidth()
											- regularFont.getWidth(nvg, ((LabelComponent) component).getText()) - 97),
									defaultComponentBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
						});
			}

			add(new SliderComponent(sliderAnnotation.min(), sliderAnnotation.max(), sliderAnnotation.step(),
					(float) option.getValue(), (value) -> option.setValue(value)), (component, defaultBounds) -> {
						defaultBounds = defaultBoundController.get(component, defaultBounds);
						return new Rectangle(defaultBounds.getX() - 5, defaultBounds.getY(), defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});
		} else if (option.isFile()) {
			String text = option.getEditText();

			add(new LabelComponent((component, defaultText) -> text,
					new AnimatedColourController(
							(component, defaultColour) -> isHovered() ? theme.activeHover : theme.active)),
					defaultBoundController);

			onClick((info, button) -> {
				if (button == 0) {
					MinecraftUtils.playClickSound(true);
					try {
						MinecraftUtils.openUrl(option.getFile().toURI().toURL().toString());
					} catch (MalformedURLException error) {
						throw new IllegalStateException(error);
					}
					return true;
				}

				return false;
			});
		} else if (option.getType().equals(String.class)) {
			TextFieldComponent field = new TextFieldComponent(100, false).withPlaceholder(option.getPlaceholder())
					.onUpdate((string) -> {
						option.setValue(string);
						return true;
					});
			field.autoFlush();
			field.setText((String) option.getValue());
			add(field, defaultBoundController);
		} else if (option.getType() == PixelMatrix.class) {
			onClick((info, button) -> {
				if (button != 0)
					return false;

				MinecraftUtils.playClickSound(true);
				screen.getRoot().setDialog(new PixelMatrixDialog(option));
				return true;
			});

			add(new PixelMatrixComponent((PixelMatrix) option.getValue()), defaultBoundController);
		}
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(230, 20);
	}

}
