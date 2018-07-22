package com.github.dan2097.jnainchi;

public enum InchiStatus {
  /** Success; no errors or warnings */
  OKAY,

  /** Success; warning(s) issued */
  WARNING,

  /** Error: no InChI has been created */
  ERROR,

  /**
   * Severe error: no InChI has been created (typically, memory allocation
   * failure)
   */
  FATAL,

  /** Unknown program error */
  UNKNOWN,

  /** Previous call to InChI has not returned yet */
  BUSY,

  /** No structural data have been provided */
  EOF,

  /** Not used in InChI library */
  SKIP,
  
  /** get structure Error: no InChI has been created */
  ERROR_get,
  
  /** compute InChI Error: no InChI has been created */
  ERROR_comp;
  

  static InchiStatus fromInchiRetCode(int code) {
    switch (code) {
    case -2:
      return InchiStatus.SKIP;
    case -1:
      return InchiStatus.EOF;
    case 0:
      return InchiStatus.OKAY;
    case 1:
      return InchiStatus.WARNING;
    case 2:
      return InchiStatus.ERROR;
    case 3:
      return InchiStatus.FATAL;
    case 4:
      return InchiStatus.UNKNOWN;
    case 5:
      return InchiStatus.BUSY;
    default:
      return null;
    }
  }
  
  static InchiStatus fromInchiFromMolRetCode(int code) {
    switch (code) {
    case 0:
      return InchiStatus.OKAY;
    case 1:
      return InchiStatus.WARNING;
    case 2:
      return InchiStatus.ERROR;
    case 4:
      return InchiStatus.ERROR_get;
    case 5:
      return InchiStatus.ERROR_comp;
    default:
      return null;
    }
  }

}
