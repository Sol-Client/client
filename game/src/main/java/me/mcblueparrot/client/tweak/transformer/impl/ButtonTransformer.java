package me.mcblueparrot.client.tweak.transformer.impl;

import me.mcblueparrot.client.tweak.transformer.ClassNodeTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.lang.reflect.Modifier;

public class ButtonTransformer implements ClassNodeTransformer {

    @Override
    public boolean test(String name) {
        return name.equals("net/minecraft/client/gui/GuiButton");
    }

    @Override
    public void apply(ClassNode clazz) {
        for(FieldNode field : clazz.fields) {
            if(field.name.equals("width") || field.name.equals("field_146120_f")
                    || field.name.equals("height") || field.name.equals("field_146121_g")) {
                field.access = (field.access & ~Modifier.PROTECTED) | Modifier.PUBLIC;
            }
        }
    }

}
