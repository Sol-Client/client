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

package io.github.solclient.client.ui.component.impl;

import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.util.data.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ListComponent extends Component {

	private final Alignment alignment;

	public ListComponent() {
		this.alignment = Alignment.CENTRE;
	}

	public void add(Component component) {
		add(getSubComponents().size(), component);
	}

	public void add(int index, Component component) {
		add(index, component,
				new AlignedBoundsController(alignment, Alignment.START, (sizingComponent, defaultBounds) -> {
					Component previous = null;
					Rectangle lastBounds = null;

					int prevIndex = subComponents.indexOf(component) - 1;
					if (prevIndex > -1) {
						previous = subComponents.get(prevIndex);
						lastBounds = previous.getCachedBounds();
					}

					return new Rectangle(defaultBounds.getX(),
							previous == null ? 0 : lastBounds.getY() + lastBounds.getHeight() + getSpacing(),
							defaultBounds.getWidth(), defaultBounds.getHeight());
				}));
	}

	protected int getContentHeight() {
		if (subComponents.isEmpty())
			return 0;

		return getBounds(subComponents.get(subComponents.size() - 1)).getEndY();
	}

	@Override
	protected boolean shouldCull(Component component) {
		return component.getBounds().getEndY() < 0 || component.getBounds().getY() > getBounds().getHeight();
	}

	public int getSpacing() {
		return 5;
	}

}
