package io.github.solclient.client.wrapper.mixin;
//package io.github.solclient.client.mixin;
//
//import org.objectweb.asm.ClassReader;
//import org.objectweb.asm.tree.ClassNode;
//import org.spongepowered.asm.launch.MixinBootstrap;
//import org.spongepowered.asm.mixin.MixinEnvironment;
//import org.spongepowered.asm.mixin.transformer.Proxy;
//import org.spongepowered.asm.service.IClassBytecodeProvider;
//import org.spongepowered.asm.service.MixinService;
//
//public class ClientMixinTransformer {
//
//	private final Proxy transformer;
//	private final MixinEnvironment environment;
//
//	public ClientMixinTransformer() throws ReflectiveOperationException {
//		MixinBootstrap.init();
//		transformer = new Proxy();
//		environment = MixinEnvironment.getDefaultEnvironment();
//	}
//
//	public byte[] transform(String name, byte[] data) {
//		if(data.length > 0) {
//			ClassNode classNode = new ClassNode();
//			ClassReader classReader = new ClassReader(data);
//			classReader.accept(classNode, 0);
//
//			ClientBytecodeProvider provider = (BytecodeProvider) MixinService.getService().getBytecodeProvider();
//
//			if(!provider.getIgnore().contains(name)) {
//				if(mixinTransformer.transformClass(environment, name.replace("/", "."), classNode)) {
//					ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
//					classNode.accept(classWriter);
//					return classWriter.toByteArray();
//				}
//			}
//			else {
//				provider.getIgnore().remove(name);
//			}
//		}
//
//		return data;
//	}
//
//}
