package io.github.solclient.client.mixin.client;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.resources.ResourcePackRepository;

@Mixin(ResourcePackRepository.Entry.class)
public class MixinResourcePackRepositoryEntry {

	@Redirect(method = "toString", at = @At(value = "INVOKE", target = "Ljava/io/File;getName()Ljava/lang/String;"))
	public String getPackName(File instance) {
		return Utils.getRelativeToPackFolder(instance);
	}

}
