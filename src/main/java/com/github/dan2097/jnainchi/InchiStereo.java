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
