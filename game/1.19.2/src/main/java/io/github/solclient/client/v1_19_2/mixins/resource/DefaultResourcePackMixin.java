package io.github.solclient.client.v1_19_2.mixins.resource;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resource.DefaultClientResourcePack;
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
