package com.github.dan2097.jnainchi;

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

}
