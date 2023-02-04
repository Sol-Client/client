package io.github.solclient.client.mixin.mod;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.itemphysics.ItemData;
import lombok.*;
import net.minecraft.entity.ItemEntity;

public class ItemPhysicsMixins {

	@Getter
	@Setter
	@Mixin(ItemEntity.class)
	public static class ItemEntityMixin implements ItemData {

		private long lastUpdate;
		private float rotation;

	}

}
