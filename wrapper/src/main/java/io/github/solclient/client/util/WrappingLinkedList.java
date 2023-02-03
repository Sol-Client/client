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
