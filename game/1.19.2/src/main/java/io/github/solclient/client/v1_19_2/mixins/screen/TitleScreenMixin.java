package io.github.solclient.client.v1_19_2.mixins.screen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.text.Text;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "initWidgetsNormal", at = @At("RETURN"))
	public void addModButton(CallbackInfo callback) {
		addDrawableChild(new ButtonWidget(width / 2 - 100, height / 4 + 120, 200, 20, Text.translatable("sol_client.mod.screen.title"), (button) -> {}));
	}

	@Redirect(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/TitleScreen;addDrawableChild(Lnet/minecraft/client/gui/Element;)Lnet/minecraft/client/gui/Element;"))
	public <T extends Element & Drawable & Selectable>
			T shift(TitleScreen instance, T element) {
		if(element instanceof ButtonWidget button && !(element instanceof PressableTextWidget)) {
			button.y += 20;
		}

		return addDrawableChild(element);
	}

}
