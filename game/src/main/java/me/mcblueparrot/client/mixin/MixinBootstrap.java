package me.mcblueparrot.client.mixin;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.mcblueparrot.client.extension.ExtensionManager;
import net.minecraft.init.Bootstrap;

@Mixin(Bootstrap.class)
public class MixinBootstrap {

	@Final
	@Shadow
	private static Logger LOGGER;

	@Inject(method = "register", at = @At(value = "INVOKE", target = "Lnet/minecraft/init/Bootstrap;registerDispenserBehaviors()V", shift = Shift.AFTER))
	private static void onBootstrap(CallbackInfo callback) {
		LOGGER.info("Loading extensions...");

		ExtensionManager extensionManager = ExtensionManager.INSTANCE;
		extensionManager.load();

		LOGGER.info("Loaded {} extension(s)", extensionManager.getExtensions().size());
	}

}
