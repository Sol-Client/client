package io.github.solclient.client.mixin.client;

import java.io.File;

import net.minecraft.resource.AbstractFileResourcePack;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.util.Utils;

@Mixin(AbstractFileResourcePack.class)
public class MixinAbstractFileResourcePack {

	@Overwrite
	public String getName() {
		return Utils.getRelativeToPackFolder(base);
	}

	@Final
	protected @Shadow File base;

}
