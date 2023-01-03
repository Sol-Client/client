package io.github.solclient.client.mod.impl.cosmetica;

import java.io.IOException;
import java.util.*;

import cc.cosmetica.api.*;
import cc.cosmetica.api.ShoulderBuddies;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.WorldLoadEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.util.Utils;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class CosmeticaMod extends Mod {

	public static CosmeticaMod instance;
	public static boolean enabled;

	private CosmeticaAPI api;
	private Map<UUID, UserInfo> dataCache = new HashMap<>();
	private Map<Model, IBakedModel> bakeryCache = new WeakHashMap<>();

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
		clear();
		logIn();
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
	}

	@Override
	protected void onDisable() {
		super.onDisable();
		enabled = false;
	}

	private void clear() {
		Texture.disposeAll();
		if (!dataCache.isEmpty()) {
			dataCache = new HashMap<>();
		}
	}

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		clear();
		if (mc.thePlayer != null) {
			// prioritise the main player
			get(mc.thePlayer);
		}
	}

	private void logIn() {
		try {
			if (mc.getSession().getProfile().getId() != null) {
				api = CosmeticaAPI.fromMinecraftToken(mc.getSession().getToken(), mc.getSession().getUsername(),
						mc.getSession().getProfile().getId());
				return;
			}
		} catch (NullPointerException | CosmeticaAPIException | IllegalStateException | FatalServerErrorException
				| IOException error) {
			logger.warn("Failed to authenticate with Cosmetica API; falling back to anonymous requests", error);
		}
		api = CosmeticaAPI.newUnauthenticatedInstance();
	}

	@Override
	public String getId() {
		return "cosmetica";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	public IBakedModel bakeIfAbsent(Model model) {
		return bakeryCache.computeIfAbsent(model, Util::createModel);
	}

	public List<Model> getHats(EntityPlayer player) {
		return get(player).map(UserInfo::getHats).orElse(Collections.emptyList());
	}

	public Optional<ShoulderBuddies> getShoulderBuddies(EntityPlayer player) {
		return get(player).flatMap(UserInfo::getShoulderBuddies);
	}

	public Optional<ResourceLocation> getCapeTexture(EntityPlayer player) {
		Optional<Cape> optCape = get(player).flatMap(UserInfo::getCape);
		if (!optCape.isPresent()) {
			return Optional.empty();
		}
		Cape cape = optCape.get();
		return Optional.of(Texture.load(2, cape.getFrameDelay() / 50, cape.getImage()));
	}

	public Optional<String> getLore(EntityPlayer player) {
		return get(player).map(UserInfo::getLore)
				.flatMap((lore) -> lore.isEmpty() ? Optional.empty() : Optional.of(lore));
	}

	public Optional<UserInfo> get(EntityPlayer player) {
		return get(player.getUniqueID(), player.getName());
	}

	public Optional<UserInfo> get(UUID uuid, String username) {
		if (uuid.version() != 4) {
			return Optional.empty();
		}

		// no synchronisation for performance
		UserInfo result = dataCache.get(uuid);

		if (result == null) {
			synchronized (dataCache) {
				// ensure the fetch doesn't keep reoccurring
				result = dataCache.putIfAbsent(uuid, UserInfo.DUMMY);
			}

			// it is possible that the result is no longer null when synchronised
			if (result == null) {
				fetch(uuid, username);
			}
		}

		if (result == UserInfo.DUMMY) {
			// return null instead of dummy
			result = null;
		}

		return Optional.ofNullable(result);
	}

	private void fetch(UUID uuid, String username) {
		Utils.USER_DATA.submit(() -> {
			ServerResponse<UserInfo> result = api.getUserInfo(uuid, username);

			if (!result.isSuccessful()) {
				logger.warn("UserInfo request ({}, {}) failed", uuid, username, result.getException());
				return;
			}

			synchronized (dataCache) {
				dataCache.put(uuid, result.get());
			}
		});
	}

}
