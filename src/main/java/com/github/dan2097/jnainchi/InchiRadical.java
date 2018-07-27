package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import inchi.InchiLibrary.tagINCHIRadical;

public enum InchiRadical {

  NONE(tagINCHIRadical.INCHI_RADICAL_NONE),
  
  SINGLET(tagINCHIRadical.INCHI_RADICAL_SINGLET),
  
  DOUBLET(tagINCHIRadical.INCHI_RADICAL_DOUBLET),
  
  TRIPLET(tagINCHIRadical.INCHI_RADICAL_TRIPLET);

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