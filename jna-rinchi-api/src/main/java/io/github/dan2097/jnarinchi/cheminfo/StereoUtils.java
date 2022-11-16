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

import io.github.dan2097.jnainchi.*;
import io.github.dan2097.jnarinchi.RinchiInputComponent;

import java.util.*;

public class StereoUtils {
	public enum MolCoordinatesType {
		_0D, _2D, _3D
	}
	
	/**
	 * Determines the number of tetrahedral chirality atoms (within stereo elements list).
	 * 
	 * @param inchiInput molecule structure represented as an {@link InchiInput} object
	 * @return number of tetrahedral chirality atoms
	 */
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
	
	/**
	 * Determines the atom parities for all stereo elements of type {@link InchiStereoType#Tetrahedral}.
	 * Parity may be recalculated to match the tetrahedral stereo element with ligand atoms reordered with 
	 * increasing atom indices.
	 * 
	 * @param inchiInput molecule structure represented as an {@link InchiInput} object
	 * @param checkParityAccordingAtomNumbering determines whether to take into account atom indices
	 * @return a hashed map with atom parities data
	 */	
	public static Map<InchiAtom,InchiStereoParity> getAtomParities(InchiInput inchiInput, 
				boolean checkParityAccordingAtomNumbering) {
		Map<InchiAtom,InchiStereoParity> parities = new HashMap<>();
		for (int i = 0; i < inchiInput.getStereos().size(); i++) {
			InchiStereo stereo = inchiInput.getStereos().get(i);
			if (stereo.getType() == InchiStereoType.Tetrahedral)
				if (stereo.getParity() == InchiStereoParity.ODD || 
					stereo.getParity() == InchiStereoParity.EVEN || 
					stereo.getParity() == InchiStereoParity.UNKNOWN) 
				{	
					if (checkParityAccordingAtomNumbering)
						stereo = sortTetrahedralLigandsToBeWithIncreasingIndices(inchiInput, stereo);
					if (stereo != null)
						parities.put(stereo.getCentralAtom(), stereo.getParity());
				}	
		}
		return parities;
	}
	
	/**
	 * Determines the bond parities for all stereo elements of type {@link InchiStereoType#DoubleBond}.
	 * 
	 * @param ric molecule structure represented as a {@link RinchiInputComponent} object
	 * @return a hashed map with bond parities data
	 */
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
	
	/**
	 * Utility function for creating a stereo element (object) of type {@link InchiStereoType#Tetrahedral}
	 * for a particular atom and given parity. Implicit H atoms are taken into account.
	 * 
	 * @param ric molecule structure represented as a {@link RinchiInputComponent} object
	 * @param atom the tetrahedral stereo center
	 * @param parity the atom parity 
	 * @return tetrahedral stereo element as an object of the type {@link InchiStereo}
	 */
	public static InchiStereo createTetrahedralStereo(RinchiInputComponent ric, InchiAtom atom, InchiStereoParity parity) {
		List<InchiAtom> neighbAtoms = ric.getConnectedAtomList(atom);
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
		
		return InchiStereo.createTetrahedralStereo(atom,
				sortedAtoms[0], sortedAtoms[1], sortedAtoms[2], sortedAtoms[3], parity);
	}
	
	/**
	 * Utility function for sorting atoms to be with an increasing indices 
	 * within the context of a given chemical structure.
	 * 
	 * @param inchiInput molecule structure represented as an {@link InchiInput} object 
	 * @param atoms atoms to be sorted
	 * @return resulting array with sorted atoms
	 */
	public static InchiAtom[] sortAtomsToBeWithIncreasingIndices(InchiInput inchiInput, List<InchiAtom> atoms) {
		if (atoms == null)
			return null;

		List<InchiAtom> sortedList = new ArrayList<>(atoms);
		sortedList.sort(Comparator.comparingInt(atom -> inchiInput.getAtoms().indexOf(atom)));

		return sortedList.toArray(new InchiAtom[sortedList.size()]);
	}
	
	/**
	 * Creates a new new tetrahedral stereo element with ligants sorted according to atom indices.
	 * Stereo element parity is updated/inverted to the new atom order
	 * 
	 * @param inchiInput molecule structure represented as an {@link InchiInput} object
	 * @param stereo original stereo element, an object of type {@link InchiStereo}
	 * @return resulting new tetrahedral stereo element
	 */
	public static InchiStereo sortTetrahedralLigandsToBeWithIncreasingIndices(InchiInput inchiInput, InchiStereo stereo) {
		InchiAtom[] ligands = stereo.getAtoms();
		int numOfSwaps = 0;
		int n = ligands.length;
		
		//Handle implicit hydrogen ligand which must be the highest numbered atom
		int numImplH = 0;
		if (ligands[n-1] == InchiStereo.STEREO_IMPLICIT_H)
			numImplH = 1;
		for (int i = 0; i < n-1; i++) 
			if (ligands[i] == InchiStereo.STEREO_IMPLICIT_H) {
				if (numImplH > 0)
					return null; //Incorrect chiral atom with 2 implicit H ligands
				else {
					numImplH = 1;
					swap(i, n-1, ligands);
					numOfSwaps++;
				}
			}		
		
		//Bubble sorting ligands 0, 1, ..., k-1  (k = n or n-1)		
		int k = n-numImplH;
		for (int i = k-1; i >= 0; i--) 
			for (int j = 0; j < i; j++) 
				if (inchiInput.getAtoms().indexOf(ligands[i]) > inchiInput.getAtoms().indexOf(ligands[j])) {
					swap (i,j, ligands);
					numOfSwaps++;
				}	
		
		//Invert parity for odd number of swaps
		InchiStereoParity newParity = ((numOfSwaps % 2) == 0) ? stereo.getParity() : invert(stereo.getParity());
		
		//Create new tetrahedral stereo element
		return InchiStereo.createTetrahedralStereo(stereo.getCentralAtom(), 
				ligands[0], ligands[1], ligands[2], ligands[3], newParity);
	}
	
	/**
	 * This function tries to guess existing Tetrahedral stereos 
	 * using only the bond stereo information (e.g. UP/DOWN setting)
	 * The created stereo objects are of type UNDEFINED
	 * The correct recognition of the absolute stereo (if not given via atom attributes)
	 * should be done by using the 2D or 3D coordinates
	 * 
	 * @param ric target RinchiInputComponent object
	 */
	public static  void guessUndefinedTetrahedralStereosBasedOnBondInfo(RinchiInputComponent ric, Set<InchiAtom> knownCenters) {
		List<InchiAtom> newCenters = new ArrayList<>();  
		for (int i = 0; i < ric.getBonds().size(); i++) {
			InchiBond bo = ric.getBonds().get(i);
			if (bo.getType() != InchiBondType.SINGLE)
				continue;
			InchiStereo stereo = null;
			
			//Create a Tetrahedral Stereo with a center first atom of the bond
			if (bo.getStereo() == InchiBondStereo.SINGLE_1DOWN || 
					bo.getStereo() == InchiBondStereo.SINGLE_1UP ||
					bo.getStereo() == InchiBondStereo.SINGLE_1EITHER)				
				stereo = createTetrahedralStereo(ric, bo.getStart(), InchiStereoParity.UNDEFINED);
			
			//Create a Tetrahedral Stereo with a center first atom of the bond
			if (bo.getStereo() == InchiBondStereo.SINGLE_2DOWN || 
					bo.getStereo() == InchiBondStereo.SINGLE_2UP ||
					bo.getStereo() == InchiBondStereo.SINGLE_2EITHER)				
				stereo = createTetrahedralStereo(ric, bo.getEnd(), InchiStereoParity.UNDEFINED);
			
			if (stereo != null && 
					!newCenters.contains(stereo.getCentralAtom()) &&
					!knownCenters.contains(stereo.getCentralAtom())) {
				ric.addStereo(stereo);
				newCenters.add(stereo.getCentralAtom());
			}	
		}
	}
	
	/**
	 * Utility function for swapping two array objects with given indices.
	 * @param i the index of first object
	 * @param j the index of second object
	 * @param objects the array with objects
	 */
	public static void swap(int i, int j, Object[] objects) {
		Object obj = objects[i];
		objects[i] = objects[j];
		objects[j] = obj;
	}
	
	
	/**
	 * Returns the inverted parity
	 * 
	 * @param parity original parity
	 * @return inverted parity
	 */
	public static InchiStereoParity invert(InchiStereoParity parity) {
		switch (parity) {
		case ODD:
			return InchiStereoParity.EVEN;
		case EVEN:
			return InchiStereoParity.ODD;	
		}
		return parity;
	}
	
	/**
	 * Determines the coordinates type (OD, 2D or 3D) from available information within
	 * {@link InchiInput} object
	 * 
	 * @param inchiInput molecule structure represented as an {@link InchiInput} object
	 * @return molecule coordinates type, an enum value of the type {@link MolCoordinatesType}
	 */
	public static MolCoordinatesType getMolCoordinatesType(InchiInput inchiInput) {
		int[] stat = coordinateStatistics(inchiInput);
		if (stat != null)
			return getMolCoordinatesType(stat[0], stat[1], stat[2]);
		else
			return null;
	}
	
	/**
	 * Function performs a coordinates statistics. The numbers of non zero X, Y and Z coordinates are counted.
	 * 
	 * @param inchiInput molecule structure represented as an {@link InchiInput} object
	 * @return an array with numbers of non zero X, Y and Z coordinates, accordingly
	 */
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
	
	/**
	 * Determines the coordinates type (OD, 2D or 3D) from available coordinates statistics:
	 * the numbers of non zero X, Y and Z coordinates.
	 * 
	 * @param nX the numbers of non zero X coordinates
	 * @param nY the numbers of non zero Y coordinates
	 * @param nZ the numbers of non zero Z coordinates
	 * @return molecule coordinates type, an enum value of the type {@link MolCoordinatesType}
	 */
	public static MolCoordinatesType getMolCoordinatesType(int nX, int nY, int nZ) {
		if (nZ != 0)
			return MolCoordinatesType._3D;

		if (nY != 0)
			return MolCoordinatesType._2D;

		return MolCoordinatesType._0D;
	}
}
