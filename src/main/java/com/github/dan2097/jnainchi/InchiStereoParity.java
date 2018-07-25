package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

public enum InchiStereoParity {

  NONE(0),
  
  ODD(1),
  
  EVEN(2),
  
  UNKNOWN(3),
  
  UNDEFINED(4);
  
  private final byte code;
  
  private InchiStereoParity(int code) {
    this.code = (byte) code;
  }
  
  byte getCode() {
    return code;
  }
  
  private static final Map<Byte, InchiStereoParity> map = new HashMap<>();
  
  static {
    for (InchiStereoParity val : InchiStereoParity.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiStereoParity of(byte code) {
    return map.get(code);
  }
}
