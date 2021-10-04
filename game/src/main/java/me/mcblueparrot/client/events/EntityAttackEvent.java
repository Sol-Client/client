package me.mcblueparrot.client.events;

import net.minecraft.entity.Entity;

public class EntityAttackEvent {

    public Entity victim;

    public EntityAttackEvent(Entity victim) {
        this.victim = victim;
    }

}
