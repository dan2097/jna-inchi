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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnainchi.InchiRadical;

public class TestRinchiInput {

	@Test 
	public void testBezene01() {
		//Testing benzene with kekule structure
		RinchiInput rInp = new RinchiInput();
		RinchiInputComponent ric1 = new RinchiInputComponent();
		rInp.addComponent(ric1);
		ric1.setRole(ReactionComponentRole.REAGENT);
		InchiAtom at1 = new InchiAtom("C");
		InchiAtom at2 = new InchiAtom("C");
		InchiAtom at3 = new InchiAtom("C");
		InchiAtom at4 = new InchiAtom("C");
		InchiAtom at5 = new InchiAtom("C");
		InchiAtom at6 = new InchiAtom("C");
		ric1.addAtom(at1);
		ric1.addAtom(at2);
		ric1.addAtom(at3);
		ric1.addAtom(at4);
		ric1.addAtom(at5);
		ric1.addAtom(at6);
		ric1.addBond(new InchiBond(at1,at2, InchiBondType.SINGLE));
		ric1.addBond(new InchiBond(at2,at3, InchiBondType.DOUBLE));
		ric1.addBond(new InchiBond(at3,at4, InchiBondType.SINGLE));
		ric1.addBond(new InchiBond(at4,at5, InchiBondType.DOUBLE));
		ric1.addBond(new InchiBond(at5,at6, InchiBondType.SINGLE));
		ric1.addBond(new InchiBond(at6,at1, InchiBondType.DOUBLE));

		RinchiOutput rOutput = JnaRinchi.toRinchi(rInp);
		assertEquals("RInChI=1.00.1S/<>C6H6/c1-2-4-6-5-3-1/h1-6H/d-", rOutput.getRinchi(), "RInChI for benzene");
	}
	
	@Test 
	public void testBezene02() {
		//Testing benzene with 'aromatic' bonds
		RinchiInput rInp = new RinchiInput();
		RinchiInputComponent ric1 = new RinchiInputComponent();
		rInp.addComponent(ric1);
		ric1.setRole(ReactionComponentRole.REAGENT);
		InchiAtom at1 = new InchiAtom("C");
		InchiAtom at2 = new InchiAtom("C");
		InchiAtom at3 = new InchiAtom("C");
		InchiAtom at4 = new InchiAtom("C");
		InchiAtom at5 = new InchiAtom("C");
		InchiAtom at6 = new InchiAtom("C");
		ric1.addAtom(at1);
		ric1.addAtom(at2);
		ric1.addAtom(at3);
		ric1.addAtom(at4);
		ric1.addAtom(at5);
		ric1.addAtom(at6);
		ric1.addBond(new InchiBond(at1,at2, InchiBondType.ALTERN));
		ric1.addBond(new InchiBond(at2,at3, InchiBondType.ALTERN));
		ric1.addBond(new InchiBond(at3,at4, InchiBondType.ALTERN));
		ric1.addBond(new InchiBond(at4,at5, InchiBondType.ALTERN));
		ric1.addBond(new InchiBond(at5,at6, InchiBondType.ALTERN));
		ric1.addBond(new InchiBond(at6,at1, InchiBondType.ALTERN));

		RinchiOutput rOutput = JnaRinchi.toRinchi(rInp);
		assertEquals("RInChI=1.00.1S/<>C6H6/c1-2-4-6-5-3-1/h1-6H/d-", rOutput.getRinchi(), "RInChI for benzene");
	}
	
	
	@Test 
	public void testRadical01() {
		//Testing a propane radical 
		RinchiInput rInp = new RinchiInput();
		RinchiInputComponent ric1 = new RinchiInputComponent();
		rInp.addComponent(ric1);
		ric1.setRole(ReactionComponentRole.REAGENT);
		InchiAtom at1 = new InchiAtom("C");
		at1.setRadical(InchiRadical.DOUBLET);
		InchiAtom at2 = new InchiAtom("C");
		InchiAtom at3 = new InchiAtom("C");		
		ric1.addAtom(at1);
		ric1.addAtom(at2);
		ric1.addAtom(at3);
		ric1.addBond(new InchiBond(at1,at2, InchiBondType.SINGLE));
		ric1.addBond(new InchiBond(at2,at3, InchiBondType.SINGLE));
		
		RinchiOutput rOutput = JnaRinchi.toRinchi(rInp);
		assertEquals("RInChI=1.00.1S/<>C3H7/c1-3-2/h1,3H2,2H3/d-", rOutput.getRinchi(), "RInChI for propan radical");
	}
	
	@Test 
	public void testRadical02() {
		//Testing a compound with a radical
		RinchiInputFromRinchiOutput rInpOut = JnaRinchi.getRinchiInputFromRinchi(
				"RInChI=1.00.1S/<>C3H7/c1-3-2/h1,3H2,2H3/d-", 
				"RAuxInfo=1.00.1/<>0/N:1,3,2/CRV:1d/rA:3nC.2CC/rB:s1;s2;/rC:;;;");
		RinchiInput rInp = rInpOut.getRinchInput();
		int nDoublets = 0;
		for (int i = 0; i < rInp.getComponents().get(0).getAtoms().size(); i++)
			if (rInp.getComponents().get(0).getAtom(i).getRadical() == InchiRadical.DOUBLET)
				nDoublets++;
		
		assertEquals(1, nDoublets, "Number of doublet radicals");
	}
	
	@Test 
	public void testRadical03() {
		//Testing a compound with two radicals 
		RinchiInputFromRinchiOutput rInpOut = JnaRinchi.getRinchiInputFromRinchi(
				"RInChI=1.00.1S/<>C3H6/c1-3-2/h1-3H2/d-", 
				"RAuxInfo=1.00.1/<>0/N:1,3,2/E:(1,2)/CRV:1d,2d/rA:3nC.2CC.2/rB:s1;s2;/rC:;;;");
		RinchiInput rInp = rInpOut.getRinchInput();
		int nDoublets = 0;
		for (int i = 0; i < rInp.getComponents().get(0).getAtoms().size(); i++)
			if (rInp.getComponents().get(0).getAtom(i).getRadical() == InchiRadical.DOUBLET)
				nDoublets++;
		
		assertEquals(2, nDoublets, "Number of doublet radicals");
	}
	
}
