package me.mcblueparrot.client.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.resources.ResourcePackListEntryFound;
import net.minecraft.client.resources.ResourcePackRepository;

@Mixin(ResourcePackListEntryFound.class)
public class MixinResourcePackListEntryFound {

	@Shadow
	@Final
	private ResourcePackRepository.Entry field_148319_c;

	@Overwrite
	public String func_148312_b() {
		String name = field_148319_c.getResourcePackName();

		if(name.contains("/")) {
			name = name.substring(field_148319_c.getResourcePackName().lastIndexOf("/") + 1);
		}

		return name;
	}

}
