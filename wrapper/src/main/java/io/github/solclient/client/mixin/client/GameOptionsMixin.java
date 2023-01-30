package io.github.solclient.client.mixin.client;

import java.io.*;
import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.extension.KeyBindingExtension;
import net.minecraft.client.option.*;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

	private static boolean firstLoad = true;

	@Inject(method = "load", at = @At("HEAD"))
	public void setDefaults(CallbackInfo callback) {
		vbo = true; // Use VBOs by default.
	}

	@Inject(method = "load", at = @At("TAIL"), cancellable = true)
	public void postLoadOptions(CallbackInfo callback) {
		if (firstLoad) {
			callback.cancel();
			firstLoad = false;
		}
	}

	// TODO this is a bad way of doing this

	@Redirect(method = "save", at = @At(value = "INVOKE", target = "Ljava/io/PrintWriter;close()V"))
	public void injectCustomOptions(PrintWriter writer) {
		for (KeyBinding keyBinding : allKeys) {
			int mods = KeyBindingExtension.from(keyBinding).getMods();
			if (mods == 0)
				continue;

			writer.print("key_mods_");
			writer.print(keyBinding.getTranslationKey());
			writer.print(':');
			writer.println(mods);
		}
		writer.close();
	}

	@Redirect(method = "load", at = @At(value = "INVOKE", target = "Ljava/io/BufferedReader;readLine()Ljava/lang/String;"))
	public String readCustomOptions(BufferedReader reader) throws IOException {
		String result = reader.readLine();
		if (result == null)
			return null;

		if (result.startsWith("key_mods_") && result.indexOf(':') != -1) {
			String key = result.substring(9, result.indexOf(':'));
			String value = result.substring(result.indexOf(':') + 1);
			StreamSupport.stream(Arrays.spliterator(allKeys), false)
					.filter((binding) -> binding.getTranslationKey().equals(key)).findFirst()
					.ifPresent((binding) -> KeyBindingExtension.from(binding).setMods(Integer.parseInt(value)));
		}

		return result;
	}

	@Inject(method = "isPressed", at = @At("RETURN"), cancellable = true)
	private static void requireMods(KeyBinding key, CallbackInfoReturnable<Boolean> callback) {
		if (callback.getReturnValueZ() && !KeyBindingExtension.from(key).areModsPressed())
			callback.setReturnValue(false);
	}

	@Shadow
	public boolean vbo;

	@Shadow
	public KeyBinding[] allKeys;

}
