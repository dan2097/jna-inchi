package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import com.github.dan2097.jnainchi.inchi.InchiLibrary.tagINCHIStereoType0D;

public enum InchiStereoType {

  None(tagINCHIStereoType0D.INCHI_StereoType_None),
  
  DoubleBond(tagINCHIStereoType0D.INCHI_StereoType_DoubleBond),
  
  Tetrahedral(tagINCHIStereoType0D.INCHI_StereoType_Tetrahedral),
  
  Allene(tagINCHIStereoType0D.INCHI_StereoType_Allene);

  private final byte code;
  
  private InchiStereoType(int code) {
    this.code = (byte) code;
  }

  byte getCode() {
    return code;
  }
  
  private static final Map<Byte, InchiStereoType> map = new HashMap<>();
  
  static {
    for (InchiStereoType val : InchiStereoType.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiStereoType of(byte code) {
    return map.get(code);
  }
}
