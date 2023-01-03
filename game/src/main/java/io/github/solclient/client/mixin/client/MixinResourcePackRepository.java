package io.github.solclient.client.mixin.client;

import java.io.File;
import java.util.*;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.resources.*;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.settings.GameSettings;

@Mixin(ResourcePackRepository.class)
public class MixinResourcePackRepository {

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z"))
	public boolean skipIt(String instance, Object other) {
		return false;
	}

	@Inject(method = "<init>", at = @At("RETURN"))
	public void postInit(File dirResourcepacksIn, File dirServerResourcepacksIn, IResourcePack rprDefaultResourcePackIn,
			IMetadataSerializer rprMetadataSerializerIn, GameSettings settings, CallbackInfo callback) {

		Map<File, ResourcePackRepository> repos = new HashMap<>();

		Iterator<String> packIterator = settings.resourcePacks.iterator();

		while (packIterator.hasNext()) {
			String packName = packIterator.next();

			File file = new File(dirResourcepacksIn, packName);

			if (!file.exists()) {
				continue;
			}

			File parent = file.getParentFile();

			ResourcePackRepository applicableRepo;

			if (packName.contains("/")) {
				applicableRepo = repos.computeIfAbsent(parent, (ignored) -> {
					ResourcePackRepository repo = new ResourcePackRepository(parent, dirServerResourcepacksIn,
							rprDefaultResourcePackIn, rprMetadataSerializerIn, settings);

					repo.getRepositoryEntriesAll();

					return repo;
				});
			} else {
				applicableRepo = (ResourcePackRepository) (Object) this;
			}

			for (ResourcePackRepository.Entry entry : applicableRepo.getRepositoryEntriesAll()) {
				if (entry.getResourcePackName().equals(packName)) {
					if (entry.func_183027_f() == 1
							|| settings.incompatibleResourcePacks.contains(entry.getResourcePackName())) {
						repositoryEntries.add(entry);
						break;
					}

					packIterator.remove();
					logger.warn("Removed selected resource pack {} because it\'s no longer compatible",
							entry.getResourcePackName());
				}
			}
		}
	}

	@Shadow
	@Final
	private static Logger logger;

	@Shadow
	private List<ResourcePackRepository.Entry> repositoryEntries;

}
