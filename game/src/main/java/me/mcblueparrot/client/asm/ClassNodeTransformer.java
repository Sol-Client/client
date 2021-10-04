package me.mcblueparrot.client.asm;

import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;

public interface ClassNodeTransformer {

    boolean test(String name);

    void apply(ClassNode clazz) throws IOException;

}
