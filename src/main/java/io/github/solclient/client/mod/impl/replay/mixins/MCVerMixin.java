package io.github.solclient.client.mod.impl.replay.mixins;

import org.spongepowered.asm.mixin.*;

import com.replaymod.core.versions.MCVer;

import io.github.solclient.util.GlobalConstants;

@Mixin(MCVer.class)
public class MCVerMixin {

	/**
	 * @author TheKodeToad - blame me
	 * @reason no one else will be messing with this
	 */
	@Overwrite(remap = false)
	public static boolean hasOptifine() {
		return GlobalConstants.optifine;
	}

}
