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
package io.github.dan2097.jnarinchi.cheminfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiInput;
import io.github.dan2097.jnainchi.InchiStereo;
import io.github.dan2097.jnainchi.InchiStereoParity;
import io.github.dan2097.jnainchi.InchiStereoType;
import io.github.dan2097.jnarinchi.RinchiInputComponent;

public class StereoUtils {

	public static enum MolCoordinatesType {
		_0D, _2D, _3D
	}
	
	public static int getNumberOfTetrahedralChiralityAtoms(InchiInput inchiInput) {
		int nTH = 0;
		for (int i = 0; i < inchiInput.getStereos().size(); i++) {
			InchiStereo stereo = inchiInput.getStereos().get(i);
			if (stereo.getType() == InchiStereoType.Tetrahedral)
				if (stereo.getParity() == InchiStereoParity.ODD || 
					stereo.getParity() == InchiStereoParity.EVEN)
					nTH++;
		}
		return nTH;
	}
	
	public static Map<InchiAtom,InchiStereoParity> getAtomParities(InchiInput inchiInput) {
		Map<InchiAtom,InchiStereoParity> parities = new HashMap<>();
		for (int i = 0; i < inchiInput.getStereos().size(); i++) {
			InchiStereo stereo = inchiInput.getStereos().get(i);
			if (stereo.getType() == InchiStereoType.Tetrahedral)
				if (stereo.getParity() == InchiStereoParity.ODD || 
					stereo.getParity() == InchiStereoParity.EVEN || 
					stereo.getParity() == InchiStereoParity.UNKNOWN) {
					parities.put(stereo.getCentralAtom(), stereo.getParity());
				}	
		}
		return parities;
	}
	
	public static Map<InchiBond,InchiStereoParity> getDoubleBondParities(RinchiInputComponent ric) {
		Map<InchiBond,InchiStereoParity> boParities = new HashMap<>();
		for (int i = 0; i < ric.getStereos().size(); i++) {
			InchiStereo stereo = ric.getStereos().get(i);
			if (stereo.getType() == InchiStereoType.DoubleBond)
				if (stereo.getParity() == InchiStereoParity.ODD || 
					stereo.getParity() == InchiStereoParity.EVEN || 
					stereo.getParity() == InchiStereoParity.UNKNOWN) {
					InchiBond bo = ric.getBond(stereo.getAtoms()[1], stereo.getAtoms()[2]);
					if (bo != null)
						boParities.put(bo, stereo.getParity());
				}	
		}
		return boParities;
	}
	
	public static InchiStereo createTetrahedralStereo(RinchiInputComponent ric, InchiAtom atom, InchiStereoParity parity) {
		List<InchiAtom> neighbAtoms = ric.getConectedAtomList(atom);
		if (neighbAtoms.size() < 3 || neighbAtoms.size() > 4)
			return null; //Unable to create stereo element
		
		if (neighbAtoms.size() == 3) { 
			if (atom.getImplicitHydrogen() == 1) {
				if (MoleculeUtils.containsHydrogen(neighbAtoms))
					return null; //one implicit and one explicit hydrogen neighbors
				
				neighbAtoms.add(InchiStereo.STEREO_IMPLICIT_H);
			}	
			else 				  
				return null; //Unable to create stereo element
		}
		
		//Check for lone pair is not needed
		//since InchiStereo encode lone pair by adding central atom within
		//the list of ligands (i.e. in this case 4 ligands are present)
		
		InchiAtom[] sortedAtoms = sortAtomsToBeWithIncreasingIndices(ric, neighbAtoms);
		
		InchiStereo stereo = InchiStereo.createTetrahedralStereo(atom, 
				sortedAtoms[0], sortedAtoms[1], sortedAtoms[2], sortedAtoms[3], parity);
		
		return stereo;
	}
	
	public static InchiAtom[] sortAtomsToBeWithIncreasingIndices(InchiInput inchiInput, List<InchiAtom> atoms) {
		if (atoms == null)
			return null;
		InchiAtom[] sorted = atoms.toArray(new InchiAtom[] {});
		int n = atoms.size();
		
		if (n <= 1)
			return sorted;
		
		if (n == 2) {
			if (inchiInput.getAtoms().indexOf(sorted[0]) > inchiInput.getAtoms().indexOf(sorted[1]))
				swap(0, 1, sorted);
			return sorted;
		}
		
		//get atom indices
		Map<InchiAtom, Integer> atomIndices = new HashMap<>();
		for (int i = 0; i < n; i++)
			atomIndices.put(sorted[i], inchiInput.getAtoms().indexOf(sorted[i]));
		//bubble sorting
		for (int i = n-1; i >= 0; i--) 
			for (int j = 0; j < i; j++) {
				if (atomIndices.get(sorted[i]) > atomIndices.get(sorted[j])) 
					swap (i,j, sorted);
			}
		return sorted;
	}
	
	private static void swap(int i, int j, Object[] objects) {
		Object obj = objects[i];
		objects[i] = objects[j];
		objects[j] = obj;
	}
	
	public static MolCoordinatesType getMolCoordinatesType(InchiInput inchiInput) {
		int[] stat = coordinateStatistics(inchiInput);
		if (stat != null)
			return getMolCoordinatesType(stat[0], stat[1], stat[2]);
		else
			return null;
	}
	
	public static int[] coordinateStatistics (InchiInput inchiInput) {
		if (inchiInput == null)
			return null;
		
		int nX = 0;
		int nY = 0;
		int nZ = 0;
		
		for (int i = 0; i < inchiInput.getAtoms().size(); i++) {
			InchiAtom at = inchiInput.getAtom(i);
			if (at.getX() != 0.0)
				nX++;
			if (at.getY() != 0.0)
				nY++;
			if (at.getZ() != 0.0)
				nZ++;
		}
		return new int[] {nX, nY, nZ};
	}
	
	public static MolCoordinatesType getMolCoordinatesType(int nX, int nY, int nZ) {
		if (nZ != 0)
			return MolCoordinatesType._3D;
		if (nY != 0)
			return MolCoordinatesType._2D;
		return MolCoordinatesType._0D;
	}
}
