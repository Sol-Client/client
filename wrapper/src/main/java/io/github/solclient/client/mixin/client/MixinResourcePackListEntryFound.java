package io.github.solclient.client.mixin.client;

import org.spongepowered.asm.mixin.*;

import net.minecraft.client.resources.*;

@Mixin(ResourcePackListEntryFound.class)
public class MixinResourcePackListEntryFound {

	@Shadow
	@Final
	private ResourcePackRepository.Entry field_148319_c;

	@Overwrite
	public String func_148312_b() {
		String name = field_148319_c.getResourcePackName();

		if (name.contains("/")) {
			name = name.substring(field_148319_c.getResourcePackName().lastIndexOf("/") + 1);
		}

		return name;
	}

}
