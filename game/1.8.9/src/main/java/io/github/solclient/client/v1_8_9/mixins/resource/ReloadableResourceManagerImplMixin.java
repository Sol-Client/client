package io.github.solclient.client.v1_8_9.mixins.resource;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.util.TranslationProvider;
import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.resource.*;

@Mixin(ReloadableResourceManagerImpl.class)
public abstract class ReloadableResourceManagerImplMixin {

	@Shadow
	private Map<String, FallbackResourceManager> fallbackManagers;
	@Shadow
	private Set<String> namespaces;

	@Inject(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;notifyListeners()V", shift = At.Shift.BEFORE))
	public void injectNamespaces(List<ResourcePack> packs, CallbackInfo callback) {
		namespaces.add("sol_client");
		fallbackManagers.put("sol_client", fallbackManagers.get("minecraft"));
	}

	@Inject(method = "reload", at = @At("RETURN"))
	public void loadSCLanguage(CallbackInfo callback) {
		TranslationProvider.load();
	}

}
