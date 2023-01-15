package io.github.solclient.client.mixin.client;

import java.io.File;

import net.minecraft.client.resource.ResourcePackLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.Utils;

@Mixin(ResourcePackLoader.Entry.class)
public class MixinResourcePackLoaderEntry {

	@Redirect(method = "toString", at = @At(value = "INVOKE", target = "Ljava/io/File;getName()Ljava/lang/String;"))
	public String getPackName(File instance) {
		return Utils.getRelativeToPackFolder(instance);
	}

}
