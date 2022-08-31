package io.github.solclient.client.v1_8_9.platform.mc.text;

import io.github.solclient.client.platform.mc.text.TextColour;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Formatting;

@Data
@RequiredArgsConstructor
public class TextColourImpl implements TextColour {

	private final Formatting formatting;

}
