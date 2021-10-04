package me.mcblueparrot.client.mixin;

import lombok.Getter;
import lombok.Setter;
import me.mcblueparrot.client.Cullable;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TileEntity.class)
public class MixinTileEntity implements Cullable {

    @Getter
    @Setter
    private boolean culled;

}
