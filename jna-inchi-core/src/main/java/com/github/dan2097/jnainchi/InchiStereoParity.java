package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import com.github.dan2097.jnainchi.inchi.InchiLibrary.tagINCHIStereoParity0D;

public enum InchiStereoParity {

  NONE(tagINCHIStereoParity0D.INCHI_PARITY_NONE),
  
  ODD(tagINCHIStereoParity0D.INCHI_PARITY_ODD),
  
  EVEN(tagINCHIStereoParity0D.INCHI_PARITY_EVEN),
  
  UNKNOWN(tagINCHIStereoParity0D.INCHI_PARITY_UNKNOWN),
  
  UNDEFINED(tagINCHIStereoParity0D.INCHI_PARITY_UNDEFINED);
  
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
