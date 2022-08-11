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
import java.util.Map;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnainchi.InchiInput;

public class MoleculeUtils {
	
	public static void setImplicitHydrogenAtoms(InchiInput inchiInput) {
		Map<InchiAtom,Integer> atomExplVal =  getExplicitAtomValencies(inchiInput);
		for (InchiAtom at : inchiInput.getAtoms()) {
			Integer explVal = atomExplVal.get(at);
			if (explVal == null)
				explVal = 0;
			int maxImplHydrogen = getMaxImlicitHAtomsCount(at.getElName(), at.getCharge(), explVal) ;
			
			if (maxImplHydrogen >= explVal)
				at.setImplicitHydrogen(maxImplHydrogen - explVal);
		}
	}
	
	public static Map<InchiAtom,Integer> getExplicitAtomValencies(InchiInput inchiInput) {
		Map<InchiAtom,Integer> atomVal = new HashMap<>();		
		for (InchiBond bo : inchiInput.getBonds()) {
			//start atom
			Integer val = atomVal.get(bo.getStart());
			if (val == null)
				val = new Integer(getOrder(bo.getType()));
			else
				val = val + getOrder(bo.getType());
			atomVal.put(bo.getStart(), val);
			// end atom
			val = atomVal.get(bo.getEnd());
			if (val == null)
				val = new Integer(getOrder(bo.getType()));
			else
				val = val + getOrder(bo.getType());
			atomVal.put(bo.getEnd(), val);
		}
		return atomVal;
	}
			
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
		}
		return 0;
	}
	
	
	public static int getMaxImlicitHAtomsCount(String elName, int charge, int val) {
		switch (elName) {
		case "H":
			return 0;
			
		case "C" :	
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
			
		case "N":
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
			
		case "O":
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
			
		}
		
		return 0;
	}
	
}
