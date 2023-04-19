package io.github.solclient.client.mod.impl.itemphysics.mixins;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.mod.impl.itemphysics.ItemData;
import lombok.*;
import net.minecraft.entity.ItemEntity;

@Getter
@Setter
@Mixin(ItemEntity.class)
public class ItemEntityMixin implements ItemData {

	private long lastUpdate;
	private float rotation;

}
