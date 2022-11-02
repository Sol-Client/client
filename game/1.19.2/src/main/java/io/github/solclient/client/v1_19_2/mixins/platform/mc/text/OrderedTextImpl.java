package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.text.OrderedText;
import net.minecraft.text.CharacterVisitor;

@Mixin(net.minecraft.text.OrderedText.class)
public interface OrderedTextImpl extends OrderedText {

	@Override
	default @NotNull OrderedText getPlainOrdered() {
		return null; // TODO
	}

	@Shadow
	boolean accept(CharacterVisitor visitor);

}
