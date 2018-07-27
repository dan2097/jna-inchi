package com.github.dan2097.jnainchi;

public class InchiKeyOutput {

  private final String inchiKey;
  private final InchiKeyStatus status;
  
  public InchiKeyOutput(String inchiKey, InchiKeyStatus status) {
    this.inchiKey = inchiKey;
    this.status = status;
  }

  public String getInchiKey() {
    return inchiKey;
  }

  public InchiKeyStatus getStatus() {
    return status;
  }

}
