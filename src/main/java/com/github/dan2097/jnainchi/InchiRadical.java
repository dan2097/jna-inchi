package com.github.dan2097.jnainchi;

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
}