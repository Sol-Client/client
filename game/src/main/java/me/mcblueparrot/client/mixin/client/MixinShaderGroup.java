package me.mcblueparrot.client.mixin.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import me.mcblueparrot.client.util.access.AccessShaderGroup;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;

@Mixin(ShaderGroup.class)
public abstract class MixinShaderGroup implements AccessShaderGroup {


    @Override
    @Accessor
    public abstract List<Shader> getListShaders();

}
