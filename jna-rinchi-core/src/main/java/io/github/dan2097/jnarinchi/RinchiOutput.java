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
 * Immutable data class for the result of RInChI and RAuxInfo generation.
 *
 * @author Nikolay Kochev
 */
public class RinchiOutput extends Output {

    private final String rinchi;
    private final String auxInfo;

    public RinchiOutput(String rinchi, String auxInfo, Status status, int errorCode, String errorMessage) {
        super(status, errorCode, errorMessage);
        this.rinchi = rinchi;
        this.auxInfo = auxInfo;
    }

    public String getRinchi() {
        return rinchi;
    }

    public String getAuxInfo() {
        return auxInfo;
    }

    @Override
    public String toString() {
        return rinchi;
    }

}
