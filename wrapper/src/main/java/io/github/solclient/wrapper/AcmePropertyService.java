package io.github.solclient.wrapper;

import java.util.*;

import org.spongepowered.asm.service.*;

import lombok.*;

@SuppressWarnings("unchecked")
public final class AcmePropertyService implements IGlobalPropertyService {

	private final Map<IPropertyKey, Object> props = new HashMap<>();

	@Override
	public IPropertyKey resolveKey(String name) {
		return new Key(name);
	}

	@Override
	public <T> T getProperty(IPropertyKey key) {
		return (T) props.get(key);
	}

	@Override
	public void setProperty(IPropertyKey key, Object value) {
		props.put(key, value);
	}

	@Override
	public <T> T getProperty(IPropertyKey key, T defaultValue) {
		return (T) props.getOrDefault(key, defaultValue);
	}

	@Override
	public String getPropertyString(IPropertyKey key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	@EqualsAndHashCode
	@RequiredArgsConstructor
	final class Key implements IPropertyKey {
		final String name;
	}

}
