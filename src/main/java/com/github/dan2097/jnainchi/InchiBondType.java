package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import com.github.dan2097.jnainchi.inchi.InchiLibrary.tagINCHIBondType;

public enum InchiBondType {

  NONE(tagINCHIBondType.INCHI_BOND_TYPE_NONE),
  
  SINGLE(tagINCHIBondType.INCHI_BOND_TYPE_SINGLE),
  
  DOUBLE(tagINCHIBondType.INCHI_BOND_TYPE_DOUBLE),
  
  TRIPLE(tagINCHIBondType.INCHI_BOND_TYPE_TRIPLE),
  
  /** avoid by all means */
  ALTERN(tagINCHIBondType.INCHI_BOND_TYPE_ALTERN);
  
  private final byte code;
  
  private InchiBondType(int code) {
    this.code = (byte) code;
  }

  byte getCode() {
    return code;
  }
  
  private static final Map<Byte, InchiBondType> map = new HashMap<>();
  
  static {
    for (InchiBondType val : InchiBondType.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiBondType of(byte code) {
    return map.get(code);
  }
}
