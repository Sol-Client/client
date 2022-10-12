package io.github.solclient.client.v1_19_2.mixins.platform.mc.world.entity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;

import io.github.solclient.client.platform.mc.maths.*;
import io.github.solclient.client.platform.mc.world.entity.Entity;
import io.github.solclient.client.platform.mc.world.entity.player.Player;
import io.github.solclient.client.platform.mc.world.level.chunk.ChunkPos;
import net.minecraft.world.entity.EntityLike;

@Mixin(net.minecraft.entity.Entity.class)
@Implements(@Interface(iface = Entity.class, prefix = "platform$"))
public abstract class EntityImpl implements EntityLike {

	public double platform$x() {
		return getX();
	}

	@Shadow
	public abstract double getX();

	public double platform$y() {
		return getY();
	}

	@Shadow
	public abstract double getY();

	public double platform$z() {
		return getZ();
	}

	@Shadow
	public abstract double getZ();

	public double platform$previousX() {
		return prevX;
	}

	@Shadow
	public double prevX;

	public double platform$previousY() {
		return prevY;
	}

	@Shadow
	public double prevY;

	public double platform$previousZ() {
		return prevZ;
	}

	@Shadow
	public double prevZ;

	public float platform$yaw() {
		return getYaw();
	}

	@Shadow
	public abstract float getYaw();

	public float platform$pitch() {
		return getPitch();
	}

	@Shadow
	public abstract float getPitch();

	public Vec3d platform$getPosition() {
		return (Vec3d) getPos();
	}

	@Shadow
	public abstract net.minecraft.util.math.Vec3d getPos();

	public int platform$getNumericId() {
		return getId();
	}

	public UUID platform$getId() {
		return getUuid();
	}

	public Box platform$getBounds() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean platform$isGloballyInvisible() {
		return isInvisible();
	}

	@Shadow
	public abstract boolean isInvisible();

	public boolean platform$isInvisibleTo(@NotNull Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	public Vec3d platform$getEyePosition() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vec3d platform$getEyePosition(float tickDelta) {
		// TODO Auto-generated method stub
		return null;
	}

	public float platform$getEntityWidth() {
		return getWidth();
	}

	@Shadow
	public abstract float getWidth();

	public float platform$getEntityEyeHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Vec3d platform$getView(float tickDelta) {
		// TODO Auto-generated method stub
		return null;
	}

	public int platform$getChunkX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int platform$getChunkZ() {
		// TODO Auto-generated method stub
		return 0;
	}

	public ChunkPos platform$getChunkPos() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean platform$isPassenger() {
		return hasVehicle();
	}

	@Shadow
	public abstract boolean hasVehicle();

	public boolean platform$isEntityOnGround() {
		return isOnGround();
	}

	@Shadow
	public abstract boolean isOnGround();

	public boolean platform$isInWeb() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean platform$isInWater() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean platform$isEntitySprinting() {
		return isSprinting();
	}

	@Shadow
	public abstract boolean isSprinting();


	public float platform$getFallDistance() {
		return fallDistance;
	}

	@Shadow
	public float fallDistance;

}

