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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InchiInput {
  
  private List<InchiAtom> atoms = new ArrayList<>();
  private List<InchiBond> bonds = new ArrayList<>();
  private List<InchiStereo> stereos = new ArrayList<>();

  public void addAtom(InchiAtom atom) {
    this.atoms.add(atom);
  }

  public void addBond(InchiBond bond) {
    this.bonds.add(bond);
  }
  
  public void addStereo(InchiStereo stereo) {
    this.stereos.add(stereo);
  }
  
  public InchiAtom getAtom(int i) {
    return atoms.get(i);
  }

  public InchiBond getBond(int i) {
    return bonds.get(i);
  }
  
  public List<InchiAtom> getAtoms() {
    return Collections.unmodifiableList(atoms);
  }

  public List<InchiBond> getBonds() {
    return Collections.unmodifiableList(bonds);
  }

  public List<InchiStereo> getStereos() {
    return Collections.unmodifiableList(stereos);
  }

}
