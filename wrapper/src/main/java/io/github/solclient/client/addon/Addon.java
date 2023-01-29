package io.github.solclient.client.addon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

import com.google.gson.*;

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
		return info.getName();
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
