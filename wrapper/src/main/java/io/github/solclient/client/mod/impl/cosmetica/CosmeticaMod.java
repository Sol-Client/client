package io.github.solclient.client.mod.impl.cosmetica;

import java.io.IOException;
import java.util.*;

import cc.cosmetica.api.*;
import cc.cosmetica.api.ShoulderBuddies;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.WorldLoadEvent;
import io.github.solclient.client.mod.*;
import io.github.solclient.client.util.MinecraftUtils;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class CosmeticaMod extends Mod {

	public static CosmeticaMod instance;
	public static boolean enabled;

	private CosmeticaAPI api;
	private Map<UUID, UserInfo> dataCache = new HashMap<>();
	private Map<Model, BakedModel> bakeryCache = new WeakHashMap<>();
	private boolean loginStarted;

	@Override
	public void onRegister() {
		super.onRegister();
		instance = this;
	}

	@Override
	protected void onEnable() {
		super.onEnable();
		enabled = true;
		clear();
		if (!loginStarted)
			logIn();
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
		if (mc.player != null) {
			// prioritise the main player
			get(mc.player);
		}
	}

	private void logIn() {
		loginStarted = true;
		Thread thread = new Thread(() -> {
			try {
				if (mc.getSession().getProfile().getId() != null) {
					api = CosmeticaAPI.fromMinecraftToken(mc.getSession().getAccessToken(),
							mc.getSession().getUsername(), mc.getSession().getProfile().getId());
					return;
				}
			} catch (NullPointerException | CosmeticaAPIException | IllegalStateException | FatalServerErrorException
					| IOException error) {
				logger.warn("Failed to authenticate with Cosmetica API; falling back to anonymous requests", error);
			}
			api = CosmeticaAPI.newUnauthenticatedInstance();
		});
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public String getId() {
		return "cosmetica";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.INTEGRATION;
	}

	public BakedModel bakeIfAbsent(Model model) {
		return bakeryCache.computeIfAbsent(model, Util::createModel);
	}

	public List<Model> getHats(PlayerEntity player) {
		return get(player).map(UserInfo::getHats).orElse(Collections.emptyList());
	}

	public Optional<ShoulderBuddies> getShoulderBuddies(PlayerEntity player) {
		return get(player).flatMap(UserInfo::getShoulderBuddies);
	}

	public Optional<Identifier> getCapeTexture(PlayerEntity player) {
		Optional<Cape> optCape = get(player).flatMap(UserInfo::getCape);
		if (!optCape.isPresent()) {
			return Optional.empty();
		}
		Cape cape = optCape.get();
		return Optional.of(Texture.load(2, cape.getFrameDelay() / 50, cape.getImage()));
	}

	public Optional<String> getLore(PlayerEntity player) {
		return get(player).map(UserInfo::getLore)
				.flatMap((lore) -> lore.isEmpty() ? Optional.empty() : Optional.of(lore));
	}

	public Optional<UserInfo> get(PlayerEntity player) {
		return get(player.getUuid(), player.getTranslationKey());
	}

	public Optional<UserInfo> get(UUID uuid, String username) {
		if (api == null || uuid.version() != 4) {
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
		MinecraftUtils.USER_DATA.submit(() -> {
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
