package me.mcblueparrot.client.mixin;

import lombok.Getter;
import lombok.Setter;
import me.mcblueparrot.client.Cullable;
import me.mcblueparrot.client.util.access.AccessEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Entity.class)
public abstract class MixinEntity implements Cullable, AccessEntity {

    @Getter
    @Setter
    private boolean culled;

    @Accessor
    public abstract boolean getIsInWeb();

}
