package io.github.solclient.client.v1_19_2.mixins.platform.mc.resource;

import org.spongepowered.asm.mixin.*;

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

	@Overwrite(remap = false)
	static Identifier parse(String path) {
		return (Identifier) new net.minecraft.util.Identifier(path);
	}

	@Overwrite(remap = false)
	static Identifier create(String namespace, String path) {
		return (Identifier) new net.minecraft.util.Identifier(namespace, path);
	}

}