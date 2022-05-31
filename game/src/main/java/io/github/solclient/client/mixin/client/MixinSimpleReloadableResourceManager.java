package io.github.solclient.client.mixin.client;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.solclient.client.Client;
import net.minecraft.client.resources.FallbackResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.util.ResourceLocation;

@Mixin(SimpleReloadableResourceManager.class)
public class MixinSimpleReloadableResourceManager {

	@Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
	public void getResource(ResourceLocation location, CallbackInfoReturnable<IResource> callback) {
		if(Client.INSTANCE.getResource(location) != null) {
			callback.setReturnValue(Client.INSTANCE.getResource(location));
		}
	}

	@Inject(method = "reloadResources", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/" +
			"SimpleReloadableResourceManager;notifyReloadListeners()V", shift = At.Shift.BEFORE))
	public void injectDomains(List<IResourcePack> resourcesPacksList, CallbackInfo callback) {
		for(String domain : new String[] {
				"replaymod",
				"jgui",
				"sol_client"
		}) {
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
