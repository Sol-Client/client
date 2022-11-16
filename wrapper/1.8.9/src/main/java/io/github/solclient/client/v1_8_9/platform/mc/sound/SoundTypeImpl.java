package io.github.solclient.client.v1_8_9.platform.mc.sound;

import io.github.solclient.client.platform.mc.resource.Identifier;
import io.github.solclient.client.platform.mc.sound.SoundType;
import lombok.*;

@Data
@RequiredArgsConstructor
public class SoundTypeImpl implements SoundType {

	private final Identifier id;

}
