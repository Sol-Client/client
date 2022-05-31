package io.github.solclient.client.mixin;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.culling.Cullable;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.tileentity.TileEntity;

@Mixin(TileEntity.class)
public class MixinTileEntity implements Cullable {

	@Getter
	@Setter
	private boolean culled;

}
