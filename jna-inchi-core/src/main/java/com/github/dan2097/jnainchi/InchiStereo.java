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
  
  private final InchiAtom[] atoms;
  private final InchiAtom centralAtom;
  private final InchiStereoType type;
  private final InchiStereoParity parity;

  /**
   * @param atoms [must be 4 atoms]
   * @param centralAtom [null for double bond]
   * @param type
   * @param parity
   */
  public InchiStereo(InchiAtom[] atoms, InchiAtom centralAtom, InchiStereoType type, InchiStereoParity parity) {
    if (atoms == null || type == null || parity == null) {
      throw new IllegalArgumentException("null parameter");
    }
    if (atoms.length != 4) {
      throw new IllegalArgumentException("Atoms array should be length 4");
    }
    this.atoms = atoms;
    this.centralAtom = centralAtom;
    this.type = type;
    this.parity = parity;
  }
  
  public InchiAtom[] getAtoms() {
    return atoms;
  }

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
