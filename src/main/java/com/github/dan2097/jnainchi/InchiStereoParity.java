package com.github.dan2097.jnainchi;

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
}
