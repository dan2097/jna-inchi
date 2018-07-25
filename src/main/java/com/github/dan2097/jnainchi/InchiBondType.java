package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

public enum InchiBondType {

  NONE(0),
  
  SINGLE(1),
  
  DOUBLE(2),
  
  TRIPLE(3),
  
  /** avoid by all means */
  ALTERN(4);
  
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
