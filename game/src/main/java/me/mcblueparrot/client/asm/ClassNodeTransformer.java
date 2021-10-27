package me.mcblueparrot.client.asm;

import java.io.IOException;

import org.objectweb.asm.tree.ClassNode;

public interface ClassNodeTransformer {

    boolean test(String name);

    void apply(ClassNode clazz) throws IOException;

}
