package me.mcblueparrot.client.extension;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import lombok.Getter;
import net.minecraft.launchwrapper.Launch;

public class ExtensionManager {

	public static final ExtensionManager INSTANCE = new ExtensionManager();
	private static final Logger LOGGER = LogManager.getLogger();

	private File folder = new File(Launch.minecraftHome, "extensions");

	private ClassLoader[] extensionClassLoaders;
	@Getter
	private List<LoadedExtension> extensions = new ArrayList<>();

	public ExtensionManager() {
		folder.mkdirs();
	}

	public void load() {
		try {
			// Walk through tree tree so extensions can be organised into folder.
			Files.walkFileTree(folder.toPath(), new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
					File file = path.toFile();

					if(file.getName().endsWith(".jar")) {
						String name = folder.toPath().relativize(path).toString();

						try {
							loadJAR(name, file);
						}
						catch(InvalidExtensionException | IOException error) {
							LOGGER.error("Could not load extension from " + name, error);
						}
					}

					return super.visitFile(path, attrs);
				}

			});
		}
		catch(IOException error) {
			LOGGER.error("Couldn't scan extensions folder");
		}

		extensionClassLoaders = new ClassLoader[extensions.size()];

		for(int i = 0; i < extensions.size(); i++) {
			extensionClassLoaders[i] = extensions.get(i).getLoader();
		}

		Launch.blackboard.put("extensionClassLoaders", extensionClassLoaders);

		for(LoadedExtension extension : extensions) {
			if(extension.getMixinConfig() != null) {
				Mixins.addConfiguration(extension.getMixinConfig());
			}
		}
	}

	public void loadJAR(String name, File jarFile) throws InvalidExtensionException, IOException {
		try {
			URLClassLoader loader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, Launch.classLoader);
			extensions.add(LoadedExtension.from(name, loader));

//			JarFile jar = new JarFile(jarFile);
//			Enumeration<? extends JarEntry> entries = jar.entries();
//
//			while(entries.hasMoreElements()) {
//				JarEntry entry = entries.nextElement();
//
//				if(!entry.getName().endsWith(".class")) {
//					continue;
//				}
//
//				String className = entry.getName().replace("/", ".").substring(0, entry.getName().lastIndexOf("."));
//				Class<?> clazz = Class.forName(className, false, loader);
//
//				if(clazz.isAnnotationPresent(RegisterMod.class)) {
//					try {
//						Constructor<?> constructor = clazz.getConstructor();
//
//						Mod mod = (Mod) constructor.newInstance();
//						Client.INSTANCE.register(() -> mod);
//					}
//					catch(NoSuchMethodException | SecurityException error) {
//						jar.close();
//						throw new InvalidExtensionException(jarFile, "Mod class" + clazz + " must have an empty constructor", error);
//					}
//					catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException error) {
//						jar.close();
//						throw new InvalidExtensionException(jarFile, "Could not initialise mod class " + clazz);
//					}
//					catch(ClassCastException error) {
//						jar.close();
//						throw new InvalidExtensionException(jarFile, "Mod class " + clazz + " should extend Mod");
//					}
//				}
//			}
//			jar.close();
		}
		catch(MalformedURLException error) {
			throw new InvalidExtensionException(error);
		}
	}

}
