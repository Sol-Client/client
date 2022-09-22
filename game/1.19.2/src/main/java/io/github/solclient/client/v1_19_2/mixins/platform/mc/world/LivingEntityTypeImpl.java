package io.github.solclient.client.v1_19_2.mixins.platform.mc.world;

import org.spongepowered.asm.mixin.Mixin;

import io.github.solclient.client.platform.mc.world.entity.LivingEntityType;
import net.minecraft.entity.EntityGroup;

@Mixin(EntityGroup.class)
public class LivingEntityTypeImpl implements LivingEntityType {

}
