/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnarinchi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class RinchiInput {

	private List<RinchiInputComponent> components = new ArrayList<>();
	private ReactionDirection direction = ReactionDirection.FORWARD;

	public void addComponent(RinchiInputComponent component) {
		this.components.add(component);
	}

	public RinchiInputComponent getComponent(int i) {
		return components.get(i);
	}
	
	public List<RinchiInputComponent> getComponents() {
		return Collections.unmodifiableList(components);
	}

	public ReactionDirection getDirection() {
		return direction;
	}

	public void setDirection(ReactionDirection direction) {
		this.direction = direction;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Reaction direction: ").append(direction).append("\n");
		for (int i = 0; i < components.size(); i++) {
			sb.append("Component ").append(i+1);
			sb.append(" ").append(components.get(i).getRole()).append("\n");
			sb.append(components.get(i).toString());
		}
		return sb.toString();
	}
	
}
