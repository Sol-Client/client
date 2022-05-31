package io.github.solclient.client.mixin.client;

import java.io.File;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.util.Utils;
import net.minecraft.client.resources.AbstractResourcePack;

@Mixin(AbstractResourcePack.class)
public class MixinAbstractResourcePack {

	@Overwrite
	public String getPackName() {
		return Utils.getRelativeToPackFolder(resourcePackFile);
	}

	@Shadow
	@Final
	protected File resourcePackFile;

}
