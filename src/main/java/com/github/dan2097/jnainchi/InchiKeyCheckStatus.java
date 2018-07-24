package com.github.dan2097.jnainchi;

import java.util.HashMap;
import java.util.Map;

import inchi.InchiLibrary;

public enum InchiKeyCheckStatus {
  
  VALID_STANDARD(InchiLibrary.tagRetValGetINCHIKey.INCHIKEY_VALID_STANDARD),
  VALID_NON_STANDARD(InchiLibrary.tagRetValGetINCHIKey.INCHIKEY_VALID_NON_STANDARD),
  INVALID_LENGTH(InchiLibrary.tagRetValGetINCHIKey.INCHIKEY_INVALID_LENGTH),
  INVALID_LAYOUT(InchiLibrary.tagRetValGetINCHIKey.INCHIKEY_INVALID_LAYOUT),
  INVALID_VERSION(InchiLibrary.tagRetValGetINCHIKey.INCHIKEY_INVALID_VERSION);
  
  private final int code;
  
  private InchiKeyCheckStatus(int code){
    this.code = code;
  }

  private static final Map<Integer, InchiKeyCheckStatus> map = new HashMap<>();
  
  static {
    for (InchiKeyCheckStatus val : InchiKeyCheckStatus.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiKeyCheckStatus of(int code) {
    return map.get(code);
  }

}
