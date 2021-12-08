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
package com.github.dan2097.jnainchi;

public class InchiStereo {
  
  /**
   * Use when a stereocentre's configuration references an implicit hydrogen 
   */
  public static final InchiAtom STEREO_IMPLICIT_H = new InchiAtom("H");
  
  private final InchiAtom[] atoms;
  private final InchiAtom centralAtom;
  private final InchiStereoType type;
  private final InchiStereoParity parity;
  

  InchiStereo(InchiAtom[] atoms, InchiAtom centralAtom, InchiStereoType type, InchiStereoParity parity) {
    if (atoms == null) {
      throw new IllegalArgumentException("atoms was null");
    }
    if (type == null) {
      throw new IllegalArgumentException("type was null");
    }
    if (parity == null) {
      throw new IllegalArgumentException("parity was null");
    }
    if (atoms.length != 4) {
      throw new IllegalArgumentException("Atoms array should be length 4");
    }
    for (int i = 0; i < atoms.length; i++) {
      if (atoms[i] == null) {
        throw new IllegalArgumentException("Atom at index " + i + " was null, use STEREO_IMPLICIT_H for implicit hydrogen, and the atom with a lone pair for lone pairs");
      }
    }
    if (type != InchiStereoType.DoubleBond && centralAtom == null) {
      throw new IllegalArgumentException("centralAtom was null");
    }
    this.atoms = atoms;
    this.centralAtom = centralAtom;
    this.type = type;
    this.parity = parity;
  }
  
  /**
   * Defines the stereo configuration around the give centralAtom. The four vertexes of the tetrahedral centre should be given along with the parity.
   * If one of the vertexes is an implicit hydrogen use {@link #STEREO_IMPLICIT_H}. If one is a lone pair, use the centralAtom for this vertex
   * @param centralAtom
   * @param atom1
   * @param atom2
   * @param atom3
   * @param atom4
   * @param parity
   * @return
   */
  public static InchiStereo createTetrahedralStereo(InchiAtom centralAtom, InchiAtom atom1, InchiAtom atom2, InchiAtom atom3, InchiAtom atom4, InchiStereoParity parity) {
    return new InchiStereo(new InchiAtom[] {atom1, atom2, atom3, atom4}, centralAtom, InchiStereoType.Tetrahedral, parity);
  }

  /**
   * <pre>
   * Given
   * A       E
   *  \     /
   *   C = D
   *  /     \
   * B       F
   * 
   * atom1 is A (or B)
   * atom2 is C
   * atom3 is D
   * atom4 is E (or F)
   * and the parity is whether atom1 and atom2 are on the same side; {@link InchiStereoParity#ODD} if on the same side
   * Atom1/2 should be chosen such that neither are implicit hydrogen
   * 
   * For a cumulene (NOTE stereochemistry on cumulenes with more than 3 double bonds are unsupported by InChI)
   * Given
   * A               G
   *  \             /
   *   C = D = E = F
   *  /             \
   * B               H
   * 
   * atom1 is A (or B)
   * atom2 is D
   * atom3 is E
   * atom4 is G (or H)
   * 
   * Atom1/2 should be chosen such that neither are implicit hydrogen
   * 
   * For the 2 adjacent double-bond case use {@link InchiStereo#createAllenalStereo(InchiAtom, InchiAtom, InchiAtom, InchiAtom, InchiAtom, InchiStereoParity)}
   * </pre>
   * @param atom1
   * @param atom2
   * @param atom3
   * @param atom4
   * @param parity
   * @return
   */
  public static InchiStereo createDoubleBondStereo(InchiAtom atom1, InchiAtom atom2, InchiAtom atom3, InchiAtom atom4, InchiStereoParity parity) {
    if (STEREO_IMPLICIT_H == atom1 || STEREO_IMPLICIT_H == atom2|| STEREO_IMPLICIT_H == atom3 || STEREO_IMPLICIT_H == atom4) {
      throw new IllegalArgumentException("Double bond stereo should use non-implicit hydrogn atoms");
    }
    return new InchiStereo(new InchiAtom[] {atom1, atom2, atom3, atom4}, null, InchiStereoType.DoubleBond, parity);
  }
  
  /**
   * <pre>
   * Defines the stereo configuration of an allenal stereocentre, these behave like an extended tetrahedron.
   * The four vertexes of the tetrahedron should be given along with the parity.
   * If one of the vertexes is an implicit hydrogen use {@link #STEREO_IMPLICIT_H}.
   * 
   * Given
   * A           F
   *  \         /
   *   C = D = E
   *  /         \
   * B           G
   * 
   * centralAtom is D
   * atom1 is A 
   * atom2 is B
   * atom3 is F
   * atom4 is G
   * 
   * (NOTE allenal centers with more than 2 double bonds are unsupported by InChI)
   * </pre>
   * @param centralAtom
   * @param atom1
   * @param atom2
   * @param atom3
   * @param atom4
   * @param parity
   * @return
   */
  public static InchiStereo createAllenalStereo(InchiAtom centralAtom, InchiAtom atom1, InchiAtom atom2, InchiAtom atom3, InchiAtom atom4, InchiStereoParity parity) {
    return new InchiStereo(new InchiAtom[] {atom1, atom2, atom3, atom4}, centralAtom, InchiStereoType.Allene, parity);
  }
  
  public InchiAtom[] getAtoms() {
    return atoms;
  }

  /**
   * Null for {@link InchiStereoType#DoubleBond}
   * @return
   */
  public InchiAtom getCentralAtom() {
    return centralAtom;
  }

  public InchiStereoType getType() {
    return type;
  }

  public InchiStereoParity getParity() {
    return parity;
  }

}
