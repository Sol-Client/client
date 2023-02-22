/*
 * Sol Client - an open source Minecraft client
 * Copyright (C) 2021-2023  TheKodeToad and Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
