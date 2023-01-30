package io.github.solclient.client.mixin.client;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import io.github.solclient.client.Client;
import io.github.solclient.client.addon.*;
import io.github.solclient.client.mod.Mod;
import net.minecraft.client.resource.FallbackResourceManager;
import net.minecraft.resource.*;
import net.minecraft.util.Identifier;

@Mixin(ReloadableResourceManagerImpl.class)
public class SimpleReloadableResourceManagerMixin {

	@Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
	public void getResource(Identifier location, CallbackInfoReturnable<Resource> callback) {
		if (Client.INSTANCE.getPseudoResources().get(location) != null)
			callback.setReturnValue(Client.INSTANCE.getPseudoResources().get(location));
	}

	@Inject(method = "reload", at = @At(value = "INVOKE", target = "Lnet/minecraft/resource/ReloadableResourceManagerImpl;notifyListeners()V", shift = At.Shift.BEFORE))
	public void injectDomains(List<ResourcePack> packs, CallbackInfo callback) {
		injectNamespace("replaymod");
		injectNamespace("jgui");
		injectNamespace("sol_client");

		if (AddonManager.getInstance().isLoaded()) {
			for (Mod mod : Client.INSTANCE.getMods())
				if (mod instanceof Addon)
					injectNamespace(((Addon) mod).getInfo().getId());
		} else
			for (AddonInfo info : AddonManager.getInstance().getQueuedAddons())
				injectNamespace(info.getId());
	}

	private void injectNamespace(String namespace) {
		namespaces.add(namespace);
		fallbackManagers.put(namespace, fallbackManagers.get("minecraft"));
	}

	@Final
	@Shadow
	private Set<String> namespaces;

	@Final
	@Shadow
	private Map<String, FallbackResourceManager> fallbackManagers;

}
