package io.github.solclient.client.mixin.client;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.Client;
import net.minecraft.client.resources.*;
import net.minecraft.util.ResourceLocation;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager {

	@Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
	public void getResource(ResourceLocation location, CallbackInfoReturnable<IResource> callback) {
		if (Client.INSTANCE.getPseudoResources().get(location) != null) {
			callback.setReturnValue(Client.INSTANCE.getPseudoResources().get(location));
		}
	}

	@Inject(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/"
			+ "SimpleReloadableResourceManager;notifyReloadListeners()V", shift = At.Shift.BEFORE))
	public void injectDomains(List<IResourcePack> resourcesPacksList, CallbackInfo callback) {
		for (String domain : new String[] { "replaymod", "jgui", "sol_client" }) {
			setResourceDomains.add(domain);
			domainResourceManagers.put(domain, domainResourceManagers.get("minecraft"));
		}
	}

	@Final
	@Shadow
	private Set<String> setResourceDomains;

	@Final
	@Shadow
	private Map<String, FallbackResourceManager> domainResourceManagers;

}
