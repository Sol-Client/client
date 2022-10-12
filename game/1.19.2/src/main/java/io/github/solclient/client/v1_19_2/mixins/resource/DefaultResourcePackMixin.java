package io.github.solclient.client.v1_19_2.mixins.resource;

import java.util.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resource.DefaultResourcePack;

@Mixin(DefaultResourcePack.class)
public class DefaultResourcePackMixin {

	@Shadow
	public @Final @Mutable Set<String> namespaces;

	@Inject(method = "<init>", at = @At("RETURN"))
	public void injectNamespaces(CallbackInfo callback) {
		namespaces = new HashSet<>(namespaces);
		namespaces.add("sol_client");
	}

}
