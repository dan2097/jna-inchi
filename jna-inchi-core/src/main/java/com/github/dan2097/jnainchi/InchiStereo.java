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

  /**
   * @param atoms Must be exactly 4 non-null atoms, {@link InchiStereo#STEREO_IMPLICIT_H} for implicit hydrogen, the atom with a lone pair for lone pairs
   * @param centralAtom Null for double bond
   * @param type
   * @param parity
   */
  public InchiStereo(InchiAtom[] atoms, InchiAtom centralAtom, InchiStereoType type, InchiStereoParity parity) {
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
    this.atoms = atoms;
    this.centralAtom = centralAtom;
    this.type = type;
    this.parity = parity;
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
