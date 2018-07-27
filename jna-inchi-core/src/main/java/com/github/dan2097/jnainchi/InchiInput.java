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
