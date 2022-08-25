package io.github.solclient.client.v1_8_9.mixins.platform.mc.util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.VirtualEnum;
import io.github.solclient.client.platform.mc.util.OperatingSystem;
import net.minecraft.util.Util;

@Mixin(Util.OperatingSystem.class)
public class OperatingSystemImpl implements OperatingSystem {

	@Override
	public String name() {
		return toEnum().name();
	}

	@Override
	public int ordinal() {
		return toEnum().ordinal();
	}

	@Override
	public Enum<?> toEnum() {
		return (Enum<?>) (Object) this;
	}

	@Override
	public VirtualEnum[] values() {
		return (VirtualEnum[]) (Object) Util.OperatingSystem.values();
	}

	@Override
	public Enum<?>[] enumValues() {
		return Util.OperatingSystem.values();
	}

	@Overwrite
	private static OperatingSystem get(String name) {
		return (OperatingSystem) (Object) Util.OperatingSystem.valueOf(name);
	}

}
