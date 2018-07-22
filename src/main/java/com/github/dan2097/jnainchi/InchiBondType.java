package com.github.dan2097.jnainchi;

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
}
