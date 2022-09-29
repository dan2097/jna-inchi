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

/**
 * Enum to indicate the role of the component of a reaction.
 * <br>
 * Each constant is associated with a short designation for this role as used by RInChI.
 * For a given enum the short designation can be returned with {@link #getShortRinchiDesignation()}.
 * For a given short designation, the enum can be returned with {@link #getRoleFromShortDesignation(String)}.
 */
public enum ReactionComponentRole
{
	REAGENT ("R"), 
	PRODUCT ("P"), 
	AGENT ("A");
	
	private final String shortRinchiDesignation;
	
	ReactionComponentRole(String shortRinchiDesignation) {
		this.shortRinchiDesignation = shortRinchiDesignation;
	}

	/**
	 * Returns the short designation of this component role as used by RInChI.
	 * @return the short designation
	 */
	public String getShortRinchiDesignation() {
		return shortRinchiDesignation;
	}

	/**
	 * Returns the enum associated with the provided short designation of this component role as used by RInChI.
	 * @param shortDesignation short designation the enum is returned for
	 * @return enum associated with <code>shortDesignation</code> or <code>null</code> if there is no enum whose short RInChI designation matches <code>shortResignation</code>
	 */
	public static ReactionComponentRole getRoleFromShortDesignation(String shortDesignation) {
		for (ReactionComponentRole role : ReactionComponentRole.values())
			if (role.getShortRinchiDesignation().equals(shortDesignation))
				return role;

		return null;
	}
}
