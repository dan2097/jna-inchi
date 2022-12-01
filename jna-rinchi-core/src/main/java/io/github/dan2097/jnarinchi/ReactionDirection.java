/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnarinchi;

/**
 * Enum to indicate the direction of the reaction.
 * <br>
 * Each constant is associated with a short designation as used by RInChI.
 * For a given enum the short designation can be returned with {@link #getShortRinchiDesignation()}.
 * For a given short designation, the enum can be returned with {@link #getDirectionFromShortDesignation(String)}.
 */
public enum ReactionDirection {
    FORWARD("+"),
    BACKWARD("-"),
    EQUILIBRIUM("=");

    private final String shortRinchiDesignation;

    ReactionDirection(String shortRinchiDesignation) {
        this.shortRinchiDesignation = shortRinchiDesignation;
    }

    /**
     * Returns the short designation of this reaction direction as used by RInChI.
     * @return the short designation
     */
    public String getShortRinchiDesignation() {
        return shortRinchiDesignation;
    }

    /**
     * Returns the enum associated with the provided short designation as used by RInChI.
     * @param shortDesignation short designation the enum is returned for
     * @return enum associated with <code>shortDesignation</code> or <code>null</code> if there is no enum whose short RInChI designation matches <code>shortResignation</code>
     */
    public static ReactionDirection getDirectionFromShortDesignation(String shortDesignation) {
        for (ReactionDirection dir : ReactionDirection.values())
            if (dir.getShortRinchiDesignation().equals(shortDesignation))
                return dir;

        return null;
    }

}
