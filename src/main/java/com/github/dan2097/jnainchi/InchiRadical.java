package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

public enum InchiRadical {

  NONE(0),
  
  SINGLET(1),
  
  DOUBLET(2),
  
  TRIPLET(3);
  
  private final byte code;
  
  private InchiRadical(int code) {
    this.code = (byte) code;
  }
  
  byte getCode() {
    return code;
  }
  
  private static final Map<Byte, InchiRadical> map = new HashMap<>();
  
  static {
    for (InchiRadical val : InchiRadical.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiRadical of(byte code) {
    return map.get(code);
  }

}