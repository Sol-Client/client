package io.github.solclient.client.addon;

import java.nio.file.Path;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.*;
import lombok.*;

public class Addon extends Mod {

	@Getter
	@Setter(AccessLevel.PACKAGE)
	private AddonInfo info;

	@Override
	public String getTranslationKey(String key) {
		return getId() + '.' + key;
	}

	@Override
	public String getName() {
		return info.getName().orElseGet(super::getName);
	}

	@Override
	public String getDescription() {
		return info.getDescription().orElseGet(super::getDescription);
	}

	public String getVersion() {
		return info.getVersion();
	}

	@Override
	public String getDetail() {
		return ' ' + getVersion();
	}

	@Override
	public String getId() {
		return info.getId();
	}

	@Override
	public Path getConfigFolder() {
		return Client.INSTANCE.getConfigFolder().resolve("addon/" + getId());
	}

	public Path getConfigFile() {
		return getConfigFolder().resolve("config.json");
	}

	@Override
	public final ModCategory getCategory() {
		return ModCategory.ADDONS;
	}

	@Override
	public boolean isEnabledByDefault() {
		return true;
	}

}
