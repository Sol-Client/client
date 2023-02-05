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

package io.github.solclient.client.util;

import java.util.LinkedList;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public final class WrappingLinkedList<E> extends LinkedList<E> {

	private static final long serialVersionUID = 1L;

	private final E filler;
	private final int max;

	@Override
	public boolean add(E e) {
		boolean result = super.add(e);
		wrap();
		return result;
	}

	@Override
	public void add(int index, E element) {
		super.add(index, element);
		wrap();
	}

	@Override
	public void addFirst(E e) {
		super.addFirst(e);
		wrap();
	}

	@Override
	public void addLast(E e) {
		super.addLast(e);
		wrap();
	}

	private void wrap() {
		if (size() > max)
			removeLast();
	}

	@Override
	public E get(int index) {
		if (index >= size() && index < max)
			return filler;

		return super.get(index);
	}

	@Override
	public E getFirst() {
		if (size() == 0)
			return filler;

		return super.getFirst();
	}

	@Override
	public E getLast() {
		if (size() < max)
			return filler;

		return super.getLast();
	}

}
