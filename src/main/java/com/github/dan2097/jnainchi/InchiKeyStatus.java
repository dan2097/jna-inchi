package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

public enum InchiKeyStatus {
  
  /** Success; no errors or warnings*/
  OK(0),
  
  /** Unknown program error*/
  UNKNOWN_ERROR(1),
  
  /** Source string is empty*/
  EMPTY_INPUT(2),
  
  /** Invalid InChI prefix or invalid version (not 1)*/
  INVALID_INCHI_PREFIX(3),
  
  /** Not enough memory*/
  NOT_ENOUGH_MEMORY(4),
  
  /** Source InChI has invalid layout*/
  INVALID_INCHI(20),

  /** Source standard InChI has invalid layout*/
  INVALID_STD_INCHI(21);
    
  private final int code;
  
  private InchiKeyStatus(int code){
    this.code = code;
  }

  private static final Map<Integer, InchiKeyStatus> map = new HashMap<>();
  
  static {
    for (InchiKeyStatus val : InchiKeyStatus.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiKeyStatus of(int code) {
    return map.get(code);
  }

}
