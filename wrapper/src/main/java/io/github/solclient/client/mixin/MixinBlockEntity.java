package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.culling.Cullable;
import lombok.*;
import net.minecraft.block.entity.BlockEntity;

@Mixin(BlockEntity.class)
public class MixinBlockEntity implements Cullable {

	@Getter
	@Setter
	private boolean culled;

}
