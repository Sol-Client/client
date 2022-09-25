package io.github.solclient.client.v1_19_2.mixins.platform.mc.util;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import io.github.solclient.client.platform.VirtualEnum;
import io.github.solclient.client.platform.mc.util.OperatingSystem;
import net.minecraft.util.Util;

@Mixin(Util.OperatingSystem.class)
public class OperatingSystemImpl implements OperatingSystem {

	@Override
	public String enumName() {
		return toEnum().name();
	}

	@Override
	public int enumOrdinal() {
		return toEnum().ordinal();
	}

	@Override
	public Enum<?> toEnum() {
		return (Enum<?>) (Object) this;
	}

	@Override
	public VirtualEnum[] getValues() {
		return (VirtualEnum[]) Util.OperatingSystem.values();
	}

	@Override
	public Enum<?>[] getEnumValues() {
		return Util.OperatingSystem.values();
	}

}

@Mixin(OperatingSystem.class)
interface OperatingSystemImpl$Static {

	@Overwrite(remap = false)
	static OperatingSystem get(String name) {
		return (OperatingSystem) (Object) Util.OperatingSystem.valueOf(name);
	}

}