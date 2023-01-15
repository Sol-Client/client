package io.github.solclient.client.mixin.client;

import java.util.*;

import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.resource.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.Client;
import net.minecraft.util.Identifier;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinSimpleReloadableResourceManager {

	@Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
	public void getResource(Identifier location, CallbackInfoReturnable<Resource> callback) {
		if (Client.INSTANCE.getPseudoResources().get(location) != null)
			callback.setReturnValue(Client.INSTANCE.getPseudoResources().get(location));
	}

	@Inject(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;notifyListeners()V", shift = At.Shift.BEFORE))
	public void injectDomains(List<ResourcePack> packs, CallbackInfo callback) {
		for (String domain : new String[] { "replaymod", "jgui", "sol_client" }) {
			namespaces.add(domain);
			fallbackManagers.put(domain, fallbackManagers.get("minecraft"));
		}
	}

	@Final
	@Shadow
	private Set<String> namespaces;

	@Final
	@Shadow
	private Map<String, FallbackResourceManager> fallbackManagers;

}
