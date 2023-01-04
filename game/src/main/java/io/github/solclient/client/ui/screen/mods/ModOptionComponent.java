package io.github.solclient.client.ui.screen.mods;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.text.DecimalFormat;

import com.replaymod.replaystudio.util.I18n;

import io.github.solclient.client.mod.ModOption;
import io.github.solclient.client.mod.annotation.Slider;
import io.github.solclient.client.ui.component.*;
import io.github.solclient.client.ui.component.controller.*;
import io.github.solclient.client.ui.component.impl.*;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.*;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.*;

public class ModOptionComponent extends BlockComponent {

	@Getter
	private ModOption option;
	private boolean listening;
	private int enumWidth;

	public ModOptionComponent(ModOption option) {
		super(Colour.BLACK_128, 8, 0);

		this.option = option;

		add(new LabelComponent(option.getName()),
				new AlignedBoundsController(Alignment.CENTRE, Alignment.CENTRE,
						(component, defaultBounds) -> new Rectangle(defaultBounds.getY(), defaultBounds.getY(),
								defaultBounds.getWidth(), defaultBounds.getHeight())));

		Controller<Rectangle> defaultBoundController = new AlignedBoundsController(Alignment.END, Alignment.CENTRE,
				(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - defaultBounds.getY(),
						defaultBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight()));

		if (option.getType() == boolean.class) {
			add(new TickboxComponent((boolean) option.getValue(), option::setValue, this), defaultBoundController);
		} else if (option.getType() == Colour.class) {
			add(new ColourBoxComponent((component, defaultColour) -> (Colour) option.getValue(), this),
					defaultBoundController);

			onClick((info, button) -> {
				if (button != 0) {
					return false;
				}

				Utils.playClickSound(true);
				screen.getRoot().setDialog(new ColourPickerDialog(option, (Colour) option.getValue(),
						(colour) -> option.setValue(colour)));
				return true;
			});
		} else if (option.getType() == KeyBinding.class) {
			KeyBinding binding = (KeyBinding) option.getValue();

			add(new LabelComponent((component, defaultText) -> GameSettings.getKeyDisplayString(binding.getKeyCode()),
					new AnimatedColourController((component, defaultColour) -> {
						if (listening) {
							return new Colour(255, 255, 85);
						} else if (isConflicting(binding)) {
							return new Colour(255, 85, 85);
						}

						return isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON;
					})), defaultBoundController);

			onClick((info, button) -> {
				if (button == 0) {
					Utils.playClickSound(true);

					listening = true;
					screen.getRoot().onClickAnwhere((ignoredInfo, pressedButton) -> {
						mc.gameSettings.setOptionKeyBinding(binding, pressedButton - 100);

						listening = false;

						mc.gameSettings.saveOptions();
						KeyBinding.resetKeyBindingArrayAndHash();

						screen.getRoot().onKeyPressed(null);
						screen.getRoot().onClickAnwhere(null);
						listening = false;
						return true;
					});

					screen.getRoot().onKeyPressed((ignoredInfo, key, character) -> {
						if (key == 1) {
							mc.gameSettings.setOptionKeyBinding(binding, 0);
						} else if (key != 0) {
							mc.gameSettings.setOptionKeyBinding(binding, key);
						} else if (character > 0) {
							mc.gameSettings.setOptionKeyBinding(binding, character + 256);
						}

						listening = false;

						mc.gameSettings.saveOptions();
						KeyBinding.resetKeyBindingArrayAndHash();
						screen.getRoot().onKeyPressed(null);
						screen.getRoot().onClickAnwhere(null);
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
						new AnimatedColourController((component,
								defaultColour) -> isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)),
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

				add(new ScaledIconComponent("sol_client_next", 8, 8, new AnimatedColourController((component,
						defaultColour) -> component.isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)),
						defaultBoundController);

				add(previous = new ScaledIconComponent("sol_client_previous", 8, 8,
						new AnimatedColourController(
								(component, defaultColour) -> component.isHovered() ? Colour.LIGHT_BUTTON_HOVER
										: Colour.LIGHT_BUTTON)),
						(component, defaultBounds) -> {
							Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
							return new Rectangle(defaultComponentBounds.getX() - enumWidth - 12,
									defaultComponentBounds.getY(), defaultComponentBounds.getWidth(),
									defaultComponentBounds.getHeight());
						});

				onClick((info, button) -> {
					if (button == 0) {
						Utils.playClickSound(true);

						int current = ((Enum<?>) option.getValue()).ordinal();

						boolean direction = false;

						if (GuiScreen.isShiftKeyDown()) {
							direction = !direction;
						}

						if (previous.isHovered()) {
							direction = !direction;
						}

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
				add(new LabelComponent((component, defaultText) -> I18n.format(sliderAnnotation.format(),
						new DecimalFormat("0.##").format(option.getValue()))), (component, defaultBounds) -> {
							Rectangle defaultComponentBounds = defaultBoundController.get(component, defaultBounds);
							return new Rectangle(
									(int) (getBounds().getWidth()
											- regularFont.getWidth(nvg, ((LabelComponent) component).getText()) - 117),
									defaultComponentBounds.getY(), defaultBounds.getWidth(), defaultBounds.getHeight());
						});
			}

			add(new SliderComponent(sliderAnnotation.min(), sliderAnnotation.max(), sliderAnnotation.step(),
					(float) option.getValue(), (value) -> option.setValue(value), this), (component, defaultBounds) -> {
						defaultBounds = defaultBoundController.get(component, defaultBounds);
						return new Rectangle(defaultBounds.getX() - 5, defaultBounds.getY(), defaultBounds.getWidth(),
								defaultBounds.getHeight());
					});
		} else if (option.isFile()) {
			String text = option.getEditText();

			add(new LabelComponent((component, defaultText) -> text, new AnimatedColourController(
					(component, defaultColour) -> isHovered() ? Colour.LIGHT_BUTTON_HOVER : Colour.LIGHT_BUTTON)),
					defaultBoundController);

			onClick((info, button) -> {
				if (button == 0) {
					Utils.playClickSound(true);
					try {
						Utils.openUrl(option.getFile().toURI().toURL().toString());
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
		}
	}

	private boolean isConflicting(KeyBinding binding) {
		if (binding.getKeyCode() != 0) {
			for (KeyBinding testKey : mc.gameSettings.keyBindings) {
				if (testKey != binding && testKey.getKeyCode() == binding.getKeyCode()) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	protected Rectangle getDefaultBounds() {
		return Rectangle.ofDimensions(300, 20);
	}

}
