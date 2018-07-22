package com.github.dan2097.jnainchi;

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
}
