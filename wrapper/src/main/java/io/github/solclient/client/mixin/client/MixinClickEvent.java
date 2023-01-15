package io.github.solclient.client.mixin.client;

import net.minecraft.text.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.util.extension.ClickEventExtension;
import lombok.Getter;

@Mixin(ClickEvent.class)
public class MixinClickEvent implements ClickEventExtension {

	@Getter
	private Runnable receiver;

	@Override
	public ClickEventExtension setReceiver(Runnable receiver) {
		this.receiver = receiver;
		return this;
	}

}
