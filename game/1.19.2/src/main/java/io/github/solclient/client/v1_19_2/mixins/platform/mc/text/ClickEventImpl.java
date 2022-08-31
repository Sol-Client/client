package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.github.solclient.client.platform.VirtualEnum;
import io.github.solclient.client.platform.mc.text.ClickEvent;

@Mixin(net.minecraft.text.ClickEvent.class)
@Implements(@Interface(iface = ClickEvent.class, prefix = "platform$"))
public abstract class ClickEventImpl {

	public @NotNull ClickEvent.Action platform$getAction() {
		return (ClickEvent.Action) (Object) getAction();
	}

	@Shadow
	public abstract net.minecraft.text.ClickEvent.Action getAction();

	public @NotNull String platform$getValue() {
		return getValue();
	}

	@Shadow
	public abstract String getValue();

}

@Mixin(ClickEvent.class)
interface ClickEventImpl$Static {

	@Overwrite(remap = false)
	static ClickEvent create(ClickEvent.Action action, String value) {
		return (ClickEvent) new net.minecraft.text.ClickEvent((net.minecraft.text.ClickEvent.Action) (Object) action, value);
	}

}

@Mixin(net.minecraft.text.ClickEvent.Action.class)
class ClickEventImpl$ActionImpl implements ClickEvent.Action {

	@Override
	public String getName() {
		return toEnum().name();
	}

	@Override
	public int getOrdinal() {
		return toEnum().ordinal();
	}

	@Override
	public Enum<?> toEnum() {
		return (Enum<?>) (Object) this;
	}

	@Override
	public VirtualEnum[] getValues() {
		return (VirtualEnum[]) (Object) net.minecraft.text.ClickEvent.Action.values();
	}

	@Override
	public Enum<?>[] getEnumValues() {
		return net.minecraft.text.ClickEvent.Action.values();
	}
}

@Mixin(ClickEvent.Action.class)
interface ClickEventImpl$ActionImpl$Static {

	@Overwrite(remap = false)
	static ClickEvent.Action get(String name) {
		return (ClickEvent.Action) (Object) net.minecraft.text.ClickEvent.Action.valueOf(name);
	}

}
