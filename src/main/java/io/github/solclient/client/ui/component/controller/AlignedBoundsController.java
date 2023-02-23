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

package io.github.solclient.client.ui.component.controller;

import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.util.data.*;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AlignedBoundsController implements Controller<Rectangle> {

	private final Alignment xAlignment;
	private final Alignment yAlignment;
	private final Controller<Rectangle> baseController;

	public AlignedBoundsController(Alignment xAlignment, Alignment yAlignment) {
		this(Alignment.fromNullable(xAlignment), Alignment.fromNullable(yAlignment), (component, defaultBounds) -> defaultBounds);
	}

	@Override
	public Rectangle get(Component component, Rectangle defaultBounds) {
		return baseController.get(component, new Rectangle(
				xAlignment.getPosition(component.getParent().getBounds().getWidth(), defaultBounds.getWidth()),
				yAlignment.getPosition(component.getParent().getBounds().getHeight(), defaultBounds.getHeight()),
				defaultBounds.getWidth(), defaultBounds.getHeight()));
	}

}
