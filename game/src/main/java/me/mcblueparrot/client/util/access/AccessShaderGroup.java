package me.mcblueparrot.client.util.access;

import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

public interface AccessShaderGroup {

    List<Shader> getListShaders();

}
