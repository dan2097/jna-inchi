package com.github.dan2097.jnainchi;

public class InchiInputFromInchiOutput {
  
  private final InchiInput inchiInput;
  private final String message;
  private final String log;
  private final InchiStatus status;
  
  
  InchiInputFromInchiOutput(InchiInput inchiInput, String message, String log, InchiStatus status, String warnings) {
    this.inchiInput = inchiInput;
    this.message = message;
    this.log = log;
    this.status = status;
  }

  public InchiInput getInchiInput() {
    return inchiInput;
  }

  public String getMessage() {
    return message;
  }

  public String getLog() {
    return log;
  }

  public InchiStatus getStatus() {
    return status;
  }

}
