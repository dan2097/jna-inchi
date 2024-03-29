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
package io.github.dan2097.jnarinchi.cheminfo;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnainchi.InchiInput;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Various utilities that operate on the molecular representation of molecules as used by jna-inchi.
 */
public class MoleculeUtils {
    /**
     * Determines whether at least one hydrogen is present within the atom list.
     *
     * @param atoms the list of atoms.
     * @return true if at least one hydrogen is present
     */
    public static boolean containsHydrogen(List<InchiAtom> atoms) {
        for (InchiAtom a : atoms)
            if (a.getElName().equals("H"))
                return true;

        return false;
    }

    /**
     * Function sets the implicit hydrogens for all atoms of a structure represented as {@link InchiInput}.
     * The function uses {@link #getImplicitHAtomsCount(String, int, int)} to define the maximum possible number of
     * valences to be filled with H atoms. The result of {@link #getExplicitAtomValencies(InchiInput)}
     * is subtracted from this maximum possible number of valences.
     *
     * @param inchiInput molecule structure represented as {@link InchiInput}
     */
    public static void setImplicitHydrogenAtoms(InchiInput inchiInput) {
        Map<InchiAtom, Integer> atomExplVal = getExplicitAtomValencies(inchiInput);

        for (InchiAtom at : inchiInput.getAtoms()) {
            Integer explVal = atomExplVal.get(at);
            if (explVal == null)
                explVal = 0;
            int maxImplHydrogen = getImplicitHAtomsCount(at.getElName(), at.getCharge(), explVal);

            if (maxImplHydrogen >= explVal)
                at.setImplicitHydrogen(maxImplHydrogen - explVal);
        }
    }

    /**
     * Determines the explicit valency for each atom of a molecule.
     * Explicit atom valencies are inferred from the bonds and their orders connected to an atom.
     *
     * @param inchiInput molecule structure represented as {@link InchiInput}
     * @return a map with inferred explicit atom valencies
     */
    public static Map<InchiAtom, Integer> getExplicitAtomValencies(InchiInput inchiInput) {
        Map<InchiAtom, Integer> atomVal = new HashMap<>();
        for (InchiBond bo : inchiInput.getBonds()) {

            //start atom
            Integer val = atomVal.get(bo.getStart());
            if (val == null)
                val = getOrder(bo.getType());
            else
                val = val + getOrder(bo.getType());
            atomVal.put(bo.getStart(), val);

            // end atom
            val = atomVal.get(bo.getEnd());
            if (val == null)
                val = getOrder(bo.getType());
            else
                val = val + getOrder(bo.getType());
            atomVal.put(bo.getEnd(), val);
        }
        return atomVal;
    }

    /**
     * Utility function to convert {@link InchiBondType} value
     * to a bond order represented as a numeric value.
     *
     * @param ibt {@link InchiBondType} value
     * @return bond order as a numeric value
     */
    public static int getOrder(InchiBondType ibt) {
        switch (ibt) {
            case NONE:
                return 0;
            case SINGLE:
                return 1;
            case DOUBLE:
                return 2;
            case TRIPLE:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Function returns the implicit valence (h atom count) for a given atom,
     * taking into account atom element, charge and
     * explicit valence (sum of all bond orders).
     * Code of the function implements the MDL valence model.
     * See CDK org.openscience.cdk.io.MDLValence.java
     *
     * <blockquote> $Id: MDLValence.h 2288 2012-11-26 03:39:27Z glandrum $
     * <p>
     * Copyright (C) 2012 NextMove Software
     * <p>
     * All Rights Reserved This file is part of the RDKit. The contents
     * are covered by the terms of the BSD license which is included in the file
     * license.txt, found at the root of the RDKit source tree. </blockquote>
     */
    public static int getImplicitHAtomsCount(String elName, int charge, int val) {
        switch (elName) {
            case "H":  //1
            case "Li": //3
            case "Na": //11
            case "K":  //19
            case "Rb": //37
            case "Cs": //55
            case "Fr": //87
                if (charge == 0 && val <= 1) return 1;
                break;

            case "Be": //4
            case "Mg": //12
            case "Ca": //20
            case "Sr": //38
            case "Ba": //56
            case "Ra": //88
                switch (charge) {
                    case 0:
                        if (val <= 2) return 2;
                        break;
                    case 1:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "B": //5
                switch (charge) {
                    case -4:
                        if (val <= 1) return 1;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "C": //6
                switch (charge) {
                    case -3:
                        if (val <= 1) return 1;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 2) return 2;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "N": //7
                switch (charge) {
                    case -2:
                        if (val <= 1) return 1;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 2) return 2;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "O": //8
                switch (charge) {
                    case -1:
                        if (val <= 1) return 1;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 2) return 2;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "F": //9
                switch (charge) {
                    case 0:
                        if (val <= 1) return 1;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 2) return 2;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Al": //13
                switch (charge) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Si": //41
                switch (charge) {
                    case -3:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 2) return 2;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "P": //15
                switch (charge) {
                    case -2:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 2) return 2;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "S": //16
                switch (charge) {
                    case -1:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 2) return 2;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Cl": //17
                switch (charge) {
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 2) return 2;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Ga": //31
                switch (charge) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Ge": //32
                switch (charge) {
                    case -3:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "As": //33
                switch (charge) {
                    case -2:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Se": //34
                switch (charge) {
                    case -1:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Br": //35
                switch (charge) {
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "In": //49
                switch (charge) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        break;
                    case 2:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Sn": //50
            case "Pb": //82
                switch (charge) {
                    case -3:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        break;
                    case 3:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Sb": //51
            case "Bi": //83
                switch (charge) {
                    case -2:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 0:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        break;
                    case 4:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Te": //52
            case "Po": //84
                switch (charge) {
                    case -1:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 0:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 1:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 2:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 3:
                        if (val <= 3) return 3;
                        break;
                    case 5:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "I": //53
            case "At": //85
                switch (charge) {
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case 1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case 2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case 3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 4:
                        if (val <= 3) return 3;
                        break;
                    case 6:
                        if (val <= 1) return 1;
                        break;
                }
                break;

            case "Tl": //81
                switch (charge) {
                    case -4:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        if (val <= 7) return 7;
                        break;
                    case -3:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        if (val <= 6) return 6;
                        break;
                    case -2:
                        if (val <= 3) return 3;
                        if (val <= 5) return 5;
                        break;
                    case -1:
                        if (val <= 2) return 2;
                        if (val <= 4) return 4;
                        break;
                    case 0:
                        if (val <= 1) return 1;
                        if (val <= 3) return 3;
                        break;
                }
                break;
        }

        return val;
    }
}
