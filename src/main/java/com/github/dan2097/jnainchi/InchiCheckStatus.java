package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import inchi.InchiLibrary;

public enum InchiCheckStatus {
  
  VALID_STANDARD(InchiLibrary.tagRetValCheckINCHI.INCHI_VALID_STANDARD),
  VALID_NON_STANDARD(InchiLibrary.tagRetValCheckINCHI.INCHI_VALID_NON_STANDARD),
  VALID_BETA(InchiLibrary.tagRetValCheckINCHI.INCHI_VALID_BETA),
  INVALID_PREFIX(InchiLibrary.tagRetValCheckINCHI.INCHI_INVALID_PREFIX),
  INVALID_VERSION(InchiLibrary.tagRetValCheckINCHI.INCHI_INVALID_VERSION),
  INVALID_LAYOUT(InchiLibrary.tagRetValCheckINCHI.INCHI_INVALID_LAYOUT),
  FAIL_I2I(InchiLibrary.tagRetValCheckINCHI.INCHI_FAIL_I2I);
  
  private final int code;
  
  private InchiCheckStatus(int code){
    this.code = code;
  }

  private static final Map<Integer, InchiCheckStatus> map = new HashMap<>();
  
  static {
    for (InchiCheckStatus val : InchiCheckStatus.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiCheckStatus of(int code) {
    return map.get(code);
  }

}
