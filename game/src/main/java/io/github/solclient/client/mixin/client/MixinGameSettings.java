package io.github.solclient.client.mixin.client;

import java.io.*;
import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.util.extension.KeyBindingExtension;
import net.minecraft.client.settings.*;

@Mixin(GameSettings.class)
public class MixinGameSettings {

	private static boolean firstLoad = true;

	@Inject(method = "loadOptions", at = @At("HEAD"))
	public void setDefaults(CallbackInfo callback) {
		useVbo = true; // Use VBOs by default.
	}

	@Inject(method = "loadOptions", at = @At("TAIL"), cancellable = true)
	public void postLoadOptions(CallbackInfo callback) {
		if (firstLoad) {
			callback.cancel();
			firstLoad = false;
		}
	}

	// TODO this is a bad way of doing this

	@Redirect(method = "saveOptions", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;close()V"))
	public void injectCustomOptions(PrintWriter writer) {
		for (KeyBinding keyBinding : keyBindings) {
			int mods = ((KeyBindingExtension) keyBinding).getMods();
			if (mods == 0)
				continue;

			writer.print("key_mods_");
			writer.print(keyBinding.getKeyDescription());
			writer.print(':');
			writer.println(mods);
		}
		writer.close();
	}

	@Redirect(method = "loadOptions", at = @At(value = "INVOKE", target = "Ljava/io/BufferedReader;readLine()Ljava/lang/String;"))
	public String readCustomOptions(BufferedReader reader) throws IOException {
		String result = reader.readLine();

		if (result.startsWith("key_mods_") && result.indexOf(':') != -1) {
			String key = result.substring(9, result.indexOf(':'));
			String value = result.substring(result.indexOf(':') + 1);
			StreamSupport.stream(Arrays.spliterator(keyBindings), false)
					.filter((binding) -> binding.getKeyDescription().equals(key)).findFirst()
					.ifPresent((binding) -> ((KeyBindingExtension) binding).setMods(Integer.parseInt(value)));
		}

		return result;
	}

	@Inject(method = "isKeyDown", at = @At("RETURN"), cancellable = true)
	private static void requireMods(KeyBinding key, CallbackInfoReturnable<Boolean> callback) {
		if (callback.getReturnValueZ() && !((KeyBindingExtension) key).areModsPressed())
			callback.setReturnValue(false);
	}

	@Shadow
	public boolean useVbo;

	@Shadow
	public KeyBinding[] keyBindings;

}
