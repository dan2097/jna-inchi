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

import java.util.ArrayList;
import java.util.List;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiInput;

public class RinchiInputComponent extends InchiInput {
	
	private ReactionComponentRole role = ReactionComponentRole.REAGENT;

	public ReactionComponentRole getRole() {
		return role;
	}

	public void setRole(ReactionComponentRole role) {
		this.role = role;
	}
	
	public List<InchiAtom> getConectedAtomList(InchiAtom atom) {
		if (atom == null)
			return null;
		List<InchiAtom> list = new ArrayList<>();

		for (int i = 0; i < getBonds().size(); i++) {
			InchiBond bo = getBond(i);
			if (bo.getStart() == atom)
				list.add(bo.getEnd());
			else if (bo.getEnd() == atom)
				list.add(bo.getStart());
		}
		return list;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("  Atoms:").append("\n");
		for (int i = 0; i < getAtoms().size(); i++)
			sb.append("    ").append(atomToString(i)).append("\n");
		sb.append("  Bonds:").append("\n");
		for (int i = 0; i < getBonds().size(); i++)
			sb.append("    ").append(bondToString(i)).append("\n");
		return sb.toString();
	}
	
	private String atomToString(int i) {
		InchiAtom at = getAtom(i);
		String s = "" + (i+1) + " " + at.getElName();
		if (at.getImplicitHydrogen() != 0)
			s+= " H" + at.getImplicitHydrogen();
		else
			s+= "   ";
		if (at.getCharge() != 0)
			s+= " " + at.getCharge();
		if (at.getIsotopicMass() != 0)
			s+= " iso " + at.getIsotopicMass();
		return s;
	}
	
	private String bondToString(int i) {
		InchiBond bo = getBond(i);
		int at1Index = getAtoms().indexOf(bo.getStart()) + 1;
		int at2Index = getAtoms().indexOf(bo.getEnd()) + 1;
		String s = "" + at1Index + " " + at2Index + " " + bo.getType();
		return s;
	}
}
