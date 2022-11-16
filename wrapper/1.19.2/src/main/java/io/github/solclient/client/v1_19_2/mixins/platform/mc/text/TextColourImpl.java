package io.github.solclient.client.v1_19_2.mixins.platform.mc.text;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.platform.mc.text.TextColour;
import net.minecraft.text.TextColor;

@Mixin(TextColor.class)
public abstract class TextColourImpl implements TextColour {

}
