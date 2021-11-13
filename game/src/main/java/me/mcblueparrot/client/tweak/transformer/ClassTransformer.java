package me.mcblueparrot.client.tweak.transformer;

import me.mcblueparrot.client.tweak.transformer.impl.GuiButtonTransformer;
import me.mcblueparrot.client.tweak.transformer.impl.GuiScreenTransformer;
import me.mcblueparrot.client.tweak.transformer.impl.MinecraftTransformer;
import me.mcblueparrot.client.tweak.transformer.impl.WorldClientTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassTransformer implements IClassTransformer {

    private List<ClassNodeTransformer> transformers = new ArrayList<>();

    public ClassTransformer() {
        register(new GuiButtonTransformer());
        register(new GuiScreenTransformer());
        register(new WorldClientTransformer());
        register(new MinecraftTransformer());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        List<ClassNodeTransformer> applicable =
                transformers.stream().filter((transformers) -> transformers.test(name.replace(".", "/"))).collect(Collectors.toList());

        if(applicable.isEmpty()) return basicClass;

        ClassReader reader = new ClassReader(basicClass);
        ClassNode clazz = new ClassNode();
        reader.accept(clazz, 0);

        for(ClassNodeTransformer transformer : applicable) {
            try {
                transformer.apply(clazz);
            }
            catch(IOException error) {
                throw new IllegalStateException("Could not transform class " + name, error);
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        clazz.accept(writer);
        return writer.toByteArray();
    }

    public void register(ClassNodeTransformer transformer) {
        transformers.add(transformer);
    }

}
