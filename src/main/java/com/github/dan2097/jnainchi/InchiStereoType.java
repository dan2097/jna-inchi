package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

public enum InchiStereoType {

  None(0),
  
  DoubleBond(1),
  
  Tetrahedral(2),
  
  Allene(3);
  
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
