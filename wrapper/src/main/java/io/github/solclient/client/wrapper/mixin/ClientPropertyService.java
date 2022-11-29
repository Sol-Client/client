package io.github.solclient.client.wrapper.mixin;

import java.util.*;

import org.spongepowered.asm.service.*;

import lombok.*;

@SuppressWarnings("unchecked")
public final class ClientPropertyService implements IGlobalPropertyService {

	private final Map<IPropertyKey, Object> keys = new HashMap<>();

	@Override
	public IPropertyKey resolveKey(String name) {
		return new Key(name);
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
