package io.github.solclient.client.v1_8_9.mixins.resource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.solclient.client.util.TranslationProvider;
import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.ResourcePack;

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
