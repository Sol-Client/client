package me.mcblueparrot.client.events;

import net.minecraft.entity.Entity;

public class EntityDamageEvent {

    public Entity entity;

    public EntityDamageEvent(Entity entity) {
        this.entity = entity;
    }

}
