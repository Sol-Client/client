package io.github.solclient.client.wrapper.mixin;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import io.github.solclient.client.wrapper.mixin.ClientPropertyService.Key;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

// who still uses blackboards?
@SuppressWarnings("unchecked")
public class ClientPropertyService implements IGlobalPropertyService {

	private final Map<IPropertyKey, Object> keys = new HashMap<>();
	private final Map<String, IPropertyKey> keyPool = new HashMap<>();

	@Override
	public IPropertyKey resolveKey(String name) {
		return keyPool.computeIfAbsent(name, Key::new);
	}

	@Override
	public <T> T getProperty(IPropertyKey key) {
		return (T) keys.get(key);
	}

	@Override
	public void setProperty(IPropertyKey key, Object value) {
		keys.put(key, value);
	}

	@Override
	public <T> T getProperty(IPropertyKey key, T defaultValue) {
		return (T) keys.getOrDefault(key, defaultValue);
	}

	@Override
	public String getPropertyString(IPropertyKey key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	@EqualsAndHashCode
	@RequiredArgsConstructor
	class Key implements IPropertyKey {

		private final String key;

		@Override
		public String toString() {
			return key;
		}

	}

}
