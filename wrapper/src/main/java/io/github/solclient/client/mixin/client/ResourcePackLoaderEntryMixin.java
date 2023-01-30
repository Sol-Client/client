package io.github.solclient.client.mixin.client;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.resource.ResourcePackLoader;

@Mixin(ResourcePackLoader.Entry.class)
public class ResourcePackLoaderEntryMixin {

	@Redirect(method = "toString", at = @At(value = "INVOKE", target = "Ljava/io/File;getName()Ljava/lang/String;"))
	public String getPackName(File instance) {
		return MinecraftUtils.getRelativeToPackFolder(instance);
	}

}
