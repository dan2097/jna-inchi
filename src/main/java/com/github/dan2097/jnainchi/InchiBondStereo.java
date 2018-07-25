package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

public enum InchiBondStereo {

  NONE(0),
  
  SINGLE_1UP(1),
  
  SINGLE_1EITHER(4),
  
  SINGLE_1DOWN(6),

  SINGLE_2UP(-1),
  
  SINGLE_2EITHER(-4),
  
  SINGLE_2DOWN(-6),
  
  /** unknown stereobond geometry*/
  DOUBLE_EITHER(3);
  
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
