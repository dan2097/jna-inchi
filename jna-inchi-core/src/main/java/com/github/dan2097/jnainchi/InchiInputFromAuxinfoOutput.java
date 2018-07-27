package com.github.dan2097.jnainchi;

public class InchiInputFromAuxinfoOutput {
  
  private final InchiInput inchiInput;
  private final Boolean chiralFlag;
  private final String message;
  private final InchiStatus status;
  
  
  InchiInputFromAuxinfoOutput(InchiInput inchiInput, Boolean chiralFlag,  String message, InchiStatus status) {
    this.inchiInput = inchiInput;
    this.chiralFlag = chiralFlag;
    this.message = message;
    this.status = status;
  }

  public InchiInput getInchiInput() {
    return inchiInput;
  }

  /**
   * True if the structure was marked as chiral
   * False if marked as not chiral
   * null if not marked
   * @return
   */
  public Boolean getChiralFlag() {
    return chiralFlag;
  }


  public String getMessage() {
    return message;
  }


  public InchiStatus getStatus() {
    return status;
  }

}
