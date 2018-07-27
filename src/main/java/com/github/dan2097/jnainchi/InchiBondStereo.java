package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import inchi.InchiLibrary.tagINCHIBondStereo2D;

public enum InchiBondStereo {
  
  NONE(tagINCHIBondStereo2D.INCHI_BOND_STEREO_NONE),
  
  SINGLE_1UP(tagINCHIBondStereo2D.INCHI_BOND_STEREO_SINGLE_1UP),
  
  SINGLE_1EITHER(tagINCHIBondStereo2D.INCHI_BOND_STEREO_SINGLE_1EITHER),
  
  SINGLE_1DOWN(tagINCHIBondStereo2D.INCHI_BOND_STEREO_SINGLE_1DOWN),

  SINGLE_2UP(tagINCHIBondStereo2D.INCHI_BOND_STEREO_SINGLE_2UP),
  
  SINGLE_2EITHER(tagINCHIBondStereo2D.INCHI_BOND_STEREO_SINGLE_2EITHER),
  
  SINGLE_2DOWN(tagINCHIBondStereo2D.INCHI_BOND_STEREO_SINGLE_2DOWN),
  
  /** unknown stereobond geometry*/
  DOUBLE_EITHER(tagINCHIBondStereo2D.INCHI_BOND_STEREO_DOUBLE_EITHER);
  
  private final byte code;
  
  private InchiBondStereo(int code) {
    this.code = (byte) code;
  }
  
  byte getCode() {
    return code;
  }
  
  private static final Map<Byte, InchiBondStereo> map = new HashMap<>();
  
  static {
    for (InchiBondStereo val : InchiBondStereo.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiBondStereo of(byte code) {
    return map.get(code);
  }

}
