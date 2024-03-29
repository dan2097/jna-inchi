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
 * Immutable data class for the result of a decomposition of RInChI/RAuxInfo into separate InChIs.
 *
 * @author Nikolay Kochev
 */
public class RinchiDecompositionOutput extends Output {
    private final static int ARRAY_IS_NULL = -1;
    private final ReactionDirection direction;
    private final String[] inchis;
    private final String[] auxInfos;
    private final ReactionComponentRole[] roles;

    public RinchiDecompositionOutput(ReactionDirection direction, String[] inchis, String[] auxInfos, ReactionComponentRole[] roles,
                                     Status status, int errorCode, String errorMessage) {
        super(status, errorCode, errorMessage);
        this.direction = direction;

        // make sure that the number of elements in the arrays storing the individual InChIs, auxiliary information
        // and reaction component roles is equal; otherwise, we throw an exception
        final int noElementsInchis = inchis == null ? ARRAY_IS_NULL : inchis.length;
        final int noElementsAuxInfo = auxInfos == null ? ARRAY_IS_NULL : auxInfos.length;
        final int noElementsRoles = roles == null ? ARRAY_IS_NULL : roles.length;
        if (noElementsInchis != noElementsAuxInfo || noElementsAuxInfo != noElementsRoles) {
            throw new IllegalArgumentException("The number of InChIs (" + noElementsInchis + "), auxiliary information (" +
                    noElementsAuxInfo + ") and reaction component roles (" + noElementsRoles + ") must be equal.");
        }

        this.inchis = inchis;
        this.auxInfos = auxInfos;
        this.roles = roles;
    }

    public ReactionDirection getDirection() {
        return direction;
    }

    public String[] getInchis() {
        return inchis;
    }

    public String[] getAuxInfos() {
        return auxInfos;
    }

    public ReactionComponentRole[] getRoles() {
        return roles;
    }

    public int getNumberOfComponents() {
        if (inchis == null)
            return 0;
        else
            return inchis.length;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getClass().getSimpleName());
        stringBuilder.append("={reactionDirection=");
        stringBuilder.append(direction);
        stringBuilder.append(", inchis=");
        stringBuilder.append(arrayElementsToString(inchis));
        stringBuilder.append(", auxInfo=");
        stringBuilder.append(arrayElementsToString(auxInfos));
        stringBuilder.append(", reactionComponentRole=");
        stringBuilder.append(arrayElementsToString(roles));
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    private String arrayElementsToString(Object[] array) {
        StringBuilder stringBuilder = new StringBuilder();

        if (array == null) {
            stringBuilder.append("null");
        } else {
            stringBuilder.append("[");
            for (Object object : array) {
                stringBuilder.append(object.toString());
                stringBuilder.append(", ");
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
            stringBuilder.append("]");
        }

        return stringBuilder.toString();
    }
}
