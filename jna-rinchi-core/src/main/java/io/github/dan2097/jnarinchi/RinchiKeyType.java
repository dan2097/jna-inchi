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
 * Enum to indicate the type of RInChIKey.
 * <br>
 * Each type is associated with a short designation as used by RInChI to refer to this type.
 */
public enum RinchiKeyType {
    LONG("L"),
    SHORT("S"),
    WEB("W");

    private final String shortDesignation;

    RinchiKeyType(String shortDesignation) {
        this.shortDesignation = shortDesignation;
    }

    /**
     * Returns the short designation of this key type as used by RInChI.
     * @return short designation
     */
    public String getShortDesignation() {
        return shortDesignation;
    }
}
