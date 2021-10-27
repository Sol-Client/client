package me.mcblueparrot.client.events;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.shader.ShaderGroup;

public class PostProcessingEvent {

    public Type type;
    public List<ShaderGroup> groups = new ArrayList<>();

    public PostProcessingEvent(Type type) {
        this.type = type;
    }

    public enum Type {
        RENDER,
        UPDATE
    }

}
