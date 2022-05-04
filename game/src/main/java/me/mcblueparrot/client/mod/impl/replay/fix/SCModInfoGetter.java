package me.mcblueparrot.client.mod.impl.replay.fix;

import java.util.Collection;
import java.util.Collections;

import com.replaymod.replaystudio.data.ModInfo;

import me.mcblueparrot.client.annotation.ForgeCompat;

@Deprecated
@ForgeCompat
public class SCModInfoGetter {

	public static Collection<ModInfo> getInstalledNetworkMods() {
		return Collections.emptyList();
	}

}
