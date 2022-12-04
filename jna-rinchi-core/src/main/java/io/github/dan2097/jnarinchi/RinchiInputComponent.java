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

import io.github.dan2097.jnainchi.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class models a particular component of a reaction.
 * <p>
 * It inherits from {@link InchiInput} which stores the input for InChI generation
 * and uses {@link InchiAtom atom}, @link InchiBond bond} and {@link InchiStereo}
 * objects to represent molecular structures.
 * <br>
 * RinchiInputComponent adds a {@link ReactionComponentRole component role} and the
 * ability to convert an instance to a String representation.
 * </p>
 *
 * @author Nikolay Kochev
 */
public class RinchiInputComponent extends InchiInput {
    private ReactionComponentRole role = ReactionComponentRole.REAGENT;

    public ReactionComponentRole getRole() {
        return role;
    }

    public void setRole(ReactionComponentRole role) {
        this.role = role;
    }

    /**
     * Returns a list of atoms that are connected to <code>atom</code>.
     *
     * @param atom adjacent atoms to this atom are returned in a list
     * @return list of atoms connected to <code>atom</code> or null if <code>atom</code> equals <code>null</code>
     */
    public List<InchiAtom> getConnectedAtomList(InchiAtom atom) {
        if (atom == null) {
            return null;
        }
        List<InchiAtom> list = new ArrayList<>();

        for (int i = 0; i < getBonds().size(); i++) {
            InchiBond bond = getBond(i);
            if (bond.getStart() == atom)
                list.add(bond.getEnd());
            else if (bond.getEnd() == atom)
                list.add(bond.getStart());
        }

        return list;
    }

    /**
     * Returns the bond that connects <code>atom1</code> and <code>atom2</code>.
     *
     * @param atom1 the first atom participating in the bond to be returned
     * @param atom2 the second atom participating in the bond to be returned
     * @return the bond that connects <code>atom1</code> and <code>atom2</code> or <code>null</code> if no such bond exists
     */
    public InchiBond getBond(InchiAtom atom1, InchiAtom atom2) {
        for (int i = 0; i < getBonds().size(); i++) {
            InchiBond bond = getBond(i);
            if ((bond.getStart() == atom1 && bond.getEnd() == atom2) || (bond.getStart() == atom2 && bond.getEnd() == atom1)) {
                return bond;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  Atoms:").append("\n");
        for (int i = 0; i < getAtoms().size(); i++)
            sb.append("    ").append(atomToString(i)).append("\n");

        if (!getBonds().isEmpty())
            sb.append("  Bonds:").append("\n");
        for (int i = 0; i < getBonds().size(); i++)
            sb.append("    ").append(bondToString(i)).append("\n");

        if (!getStereos().isEmpty())
            sb.append("  Stereos:").append("\n");
        for (int i = 0; i < getStereos().size(); i++)
            sb.append("    ").append(stereoToString(i)).append("\n");

        return sb.toString();
    }

    private String atomToString(int i) {
        InchiAtom atom = getAtom(i);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(i + 1).append(" ").append(atom.getElName());
        if (atom.getImplicitHydrogen() != 0)
            stringBuilder.append(" H").append(atom.getImplicitHydrogen());
        else
            stringBuilder.append("   ");

        stringBuilder.append("  ").append(atom.getX()).append("  ").append(atom.getY()).append("  ").append(atom.getZ());

        if (atom.getCharge() != 0)
            stringBuilder.append(" ").append(atom.getCharge());
        if (atom.getIsotopicMass() != 0)
            stringBuilder.append(" iso ").append(atom.getIsotopicMass());
        if (atom.getRadical() != InchiRadical.NONE)
            stringBuilder.append("  radical ").append(atom.getRadical());

        return stringBuilder.toString();
    }

    private String bondToString(int i) {
        InchiBond bond = getBond(i);
        int atomIndex1 = getAtoms().indexOf(bond.getStart()) + 1;
        int atomIndex2 = getAtoms().indexOf(bond.getEnd()) + 1;

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(atomIndex1).append(" ").append(atomIndex2).append(" ").append(bond.getType());
        if (bond.getStereo() != InchiBondStereo.NONE)
            stringBuilder.append("  stereo ").append(bond.getStereo());

        return stringBuilder.toString();
    }

    private String stereoToString(int atIndex) {
        InchiStereo stereo = getStereos().get(atIndex);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(stereo.getType().toString()).append(": ");

        switch (stereo.getType()) {
            case Tetrahedral:
                stringBuilder.append("center ").append(getAtoms().indexOf(stereo.getCentralAtom()) + 1);
                stringBuilder.append(" ligands");
                for (int i = 0; i < 4; i++)
                    stringBuilder.append(" ").append(getAtoms().indexOf(stereo.getAtoms()[i]) + 1);
                break;
            case DoubleBond:
                stringBuilder.append(" ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[0]) + 1).append(" - ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[1]) + 1).append(" = ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[2]) + 1).append(" - ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[3]) + 1);
                break;
            case Allene:
                stringBuilder.append(" ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[0]) + 1).append(" - ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[1]) + 1).append(" = ");
                stringBuilder.append(getAtoms().indexOf(stereo.getCentralAtom()) + 1).append(" = ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[2]) + 1).append(" - ");
                stringBuilder.append(getAtoms().indexOf(stereo.getAtoms()[3]) + 1);
                break;
        }

        stringBuilder.append("  ").append(stereo.getParity());

        return stringBuilder.toString();
    }
}
