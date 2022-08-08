package io.github.solclient.client.v1_19_1.mixins.platform.resource;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.mc.resource.Identifier;

@Mixin(net.minecraft.util.Identifier.class)
public class IdentifierImpl implements Identifier {

	@Shadow
	protected @Final String namespace;
	@Shadow
	protected @Final String path;

	@Override
	public String namespace() {
		return namespace;
	}

	@Override
	public String path() {
		return path;
	}

}

@Mixin(Identifier.class)
interface IdentifierImpl$Static {

	@Overwrite
	static Identifier parse(String path) {
		return (Identifier) new net.minecraft.util.Identifier(path);
	}

	@Overwrite
	static Identifier create(String namespace, String path) {
		return (Identifier) new net.minecraft.util.Identifier(namespace, path);
	}

}