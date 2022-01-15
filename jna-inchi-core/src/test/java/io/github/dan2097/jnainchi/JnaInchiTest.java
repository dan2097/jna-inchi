/**
 * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
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
package io.github.dan2097.jnainchi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class JnaInchiTest {
  
  @Test
  public void testToInchi() {
    InchiInput input = new InchiInput();
    InchiAtom a1 = new InchiAtom("C");
    a1.setImplicitHydrogen(3);
    InchiAtom a2 = new InchiAtom("C");
    a2.setImplicitHydrogen(1);
    InchiAtom a3 = new InchiAtom("C");
    a3.setImplicitHydrogen(1);
    InchiAtom a4 = new InchiAtom("Br");
    InchiBond b1 = new InchiBond(a1, a2, InchiBondType.SINGLE);
    InchiBond b2 = new InchiBond(a2, a3, InchiBondType.DOUBLE);
    InchiBond b3 = new InchiBond(a3, a4, InchiBondType.SINGLE);
    
    InchiStereo stereo = InchiStereo.createDoubleBondStereo(a1, a2, a3, a4, InchiStereoParity.ODD);
    input.addAtom(a1);
    input.addAtom(a2);
    input.addAtom(a3);
    input.addAtom(a4);
    input.addBond(b1);
    input.addBond(b2);
    input.addBond(b3);
    input.addStereo(stereo);
    InchiOutput output1 = JnaInchi.toInchi(input);
    assertEquals(InchiStatus.SUCCESS, output1.getStatus());
    assertEquals("InChI=1S/C3H5Br/c1-2-3-4/h2-3H,1H3/b3-2-", output1.getInchi());
    
    InchiOutput output2 = JnaInchi.toInchi(input, new InchiOptions.InchiOptionsBuilder().withFlag(InchiFlag.SNon).build());
    assertEquals(InchiStatus.SUCCESS, output2.getStatus());
    assertEquals("InChI=1S/C3H5Br/c1-2-3-4/h2-3H,1H3", output2.getInchi());
  }
  
  @Test
  public void testInchiToInchi() {
    InchiOutput output = JnaInchi.inchiToInchi("InChI=1S/C3H5Br/c1-2-3-4/h2-3H,1H3/b3-2-", new InchiOptions.InchiOptionsBuilder().withFlag(InchiFlag.SNon).build());
    assertEquals(InchiStatus.SUCCESS, output.getStatus());
    assertEquals("InChI=1S/C3H5Br/c1-2-3-4/h2-3H,1H3", output.getInchi());
  }

  @Test
  public void testMolToInchi() {
    String mol = "\n OpenBabel12062120242D\n\n  5  4  0  0  1  0  0  0  0  0999 V2000\n    1.5000   -0.8660    0.0000 F   0  0  0  0  0  0  0  0  0  0  0  0\n    0.5000   -0.8660    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n   -0.3660   -1.3660    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    0.5000   -1.8660    0.0000 Br  0  0  0  0  0  0  0  0  0  0  0  0\n   -0.0000   -0.0000    0.0000 I   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  1  1  0  0  0\n  2  4  1  0  0  0  0\n  2  5  1  0  0  0  0\nM  END\n";
    InchiOutput output1 = JnaInchi.molToInchi(mol);
    assertEquals(InchiStatus.SUCCESS, output1.getStatus());
    assertEquals("InChI=1S/CHBrFI/c2-1(3)4/h1H/t1-/m0/s1", output1.getInchi());
    
    InchiOutput output2 = JnaInchi.molToInchi(mol, new InchiOptions.InchiOptionsBuilder().withFlag(InchiFlag.SNon).build());
    assertEquals(InchiStatus.SUCCESS, output2.getStatus());
    assertEquals("InChI=1S/CHBrFI/c2-1(3)4/h1H", output2.getInchi());
  }
  
  @Test
  public void testPolymerToInChI() {
    String mol = "poly(ethylene)\n  -INDIGO-01152200132D\n\n  4  3  0  0  0  0  0  0  0  0999 V2000\n   -1.9875    0.8946    0.0000 *   0  0  0  0  0  0  0  0  0  0  0  0\n   -1.1411    0.8839    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n   -0.4286    0.4714    0.0000 C   0  0  0  0  0  0  0  0  0  0  0  0\n    0.5357    0.4661    0.0000 *   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  1  0  0  0  0\n  3  4  1  0  0  0  0\nM  STY  1   1 SRU\nM  SLB  1   1   1\nM  SCN  1   1 HT \nM  SAL   1  2   2   3\nM  SBL   1  2   1   3\nM  SMT   1 n\nM  SDI   1  4   -0.0268    0.8839   -0.0321    0.0589\nM  SDI   1  4   -1.4946    0.4768   -1.4839    1.3018\nM  END\n";
    InchiOutput output1 = JnaInchi.molToInchi(mol);
    assertEquals(InchiStatus.ERROR, output1.getStatus());
    assertNull(output1.getInchi());
    
    InchiOutput output2 = JnaInchi.molToInchi(mol, new InchiOptions.InchiOptionsBuilder().withFlag(InchiFlag.Polymers).build());
    assertEquals(InchiStatus.SUCCESS, output2.getStatus());
    assertNotNull(output2.getInchi());//polymer support is still in beta so subject to change
  }
  
  @Test
  public void testUnsupportedSgroup() {
    String mol = "\n OpenBabel12062120242D\n\n  5  4  0  0  1  0  0  0  0  0999 V2000\n    1.5000   -0.8660    0.0000 F   0  0  0  0  0  0  0  0  0  0  0  0\n    0.5000   -0.8660    0.0000 C   0  0  2  0  0  0  0  0  0  0  0  0\n   -0.3660   -1.3660    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n    0.5000   -1.8660    0.0000 Br  0  0  0  0  0  0  0  0  0  0  0  0\n   -0.0000   -0.0000    0.0000 I   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0  0  0  0\n  2  3  1  1  0  0  0\n  2  4  1  0  0  0  0\n  2  5  1  0  0  0  0\nM  STY  1   1 DAT\nM  END\n";
    InchiOutput output = JnaInchi.molToInchi(mol);
    //InChI 1.06 classic API gives a WARNING about ignoring polymer data
    //InChI 1.06 IXA API gives an error as it implicitly reads the molfile as if InchiFlag.Polymers was set
    assertFalse(output.getStatus() == InchiStatus.ERROR);
    assertEquals("InChI=1S/CHBrFI/c2-1(3)4/h1H/t1-/m0/s1", output.getInchi());
  }

  @Test
  public void testInchiKeyGeneration() {
    InchiKeyOutput output = JnaInchi.inchiToInchiKey("InChI=1S/C7H5N3O6/c1-4-6(9(13)14)2-5(8(11)12)3-7(4)10(15)16/h2-3H,1H3");
    assertEquals(InchiKeyStatus.OK, output.getStatus());
    assertEquals("SPSSULHKWOKEEL-UHFFFAOYSA-N", output.getInchiKey());
    assertNotNull(output.getBlock1HashExtension());
    assertNotNull(output.getBlock2HashExtension());
  }
  
  @Test
  public void testCheckInchiLoose() {
    assertEquals(InchiCheckStatus.VALID_STANDARD, JnaInchi.checkInchi("InChI=1S/C7H5N3O6/c1-4-6(9(13)14)2-5(8(11)12)3-7(4)10(15)16/h2-3H,1H3", false));
    assertEquals(InchiCheckStatus.VALID_NON_STANDARD, JnaInchi.checkInchi("InChI=1/C7H5N3O6/c1-4-6(9(13)14)2-5(8(11)12)3-7(4)10(15)16/h2-3H,1H3", false));
  }
  
  @Test
  public void testCheckInchiStrict() {
    //Doesn't work, InChI bug?
    //assertEquals(InchiCheckStatus.VALID_STANDARD, JnaInchi.checkInchi("InChI=1S/C7H5N3O6/c1-4-6(9(13)14)2-5(8(11)12)3-7(4)10(15)16/h2-3H,1H3", true));
    assertEquals(InchiCheckStatus.VALID_NON_STANDARD, JnaInchi.checkInchi("InChI=1/C7H5N3O6/c1-4-6(9(13)14)2-5(8(11)12)3-7(4)10(15)16/h2-3H,1H3", true));
  }
  
  @Test
  public void testCheckInchiKey() {
    InchiKeyCheckStatus output = JnaInchi.checkInchiKey("SPSSULHKWOKEEL-UHFFFAOYSA-N");
    assertEquals(InchiKeyCheckStatus.VALID_STANDARD, output);
  }
  
  @Test
  public void testInchiInputFromInchi() {
    InchiInputFromInchiOutput output = JnaInchi.getInchiInputFromInchi("InChI=1S/C2H3BrClI/c1-2(3,4)5/h1H3/t2-/m0/s1");
    assertEquals(InchiStatus.SUCCESS, output.getStatus());
    InchiInput inchiInput = output.getInchiInput();
    assertNotNull(inchiInput);
    assertEquals(5, inchiInput.getAtoms().size());
    assertEquals("C", inchiInput.getAtom(0).getElName());
    assertEquals("C", inchiInput.getAtom(1).getElName());
    assertEquals("Br", inchiInput.getAtom(2).getElName());
    assertEquals("Cl", inchiInput.getAtom(3).getElName());
    assertEquals("I", inchiInput.getAtom(4).getElName());
    assertEquals(4, inchiInput.getBonds().size());
    assertEquals(1, inchiInput.getStereos().size());
  }
  
  @Test
  public void testInchiInputFromAuxInfo() {
    String auxInfo = "AuxInfo=1/0/N:3,2,5,1,4/it:im/rA:5ClC.oCIBr/rB:p1;s2;s2;N2;/rC:0,-1.54,0;;0,1.54,0;1.54,0,0;-1.54,0,0;";
    InchiInputFromAuxinfoOutput output = JnaInchi.getInchiInputFromAuxInfo(auxInfo, false, false);
    assertEquals(InchiStatus.SUCCESS, output.getStatus());
    InchiInput inchiInput = output.getInchiInput();
    assertNotNull(inchiInput);
    assertEquals(5, inchiInput.getAtoms().size());
    assertEquals("Cl", inchiInput.getAtom(0).getElName());
    assertEquals("C", inchiInput.getAtom(1).getElName());
    assertEquals("C", inchiInput.getAtom(2).getElName());
    assertEquals("I", inchiInput.getAtom(3).getElName());
    assertEquals("Br", inchiInput.getAtom(4).getElName());
    assertEquals(4, inchiInput.getBonds().size());
    assertEquals(1, inchiInput.getStereos().size());

    assertEquals("InChI=1S/C2H3BrClI/c1-2(3,4)5/h1H3/t2-/m0/s1", JnaInchi.toInchi(inchiInput).getInchi());
  }

}
