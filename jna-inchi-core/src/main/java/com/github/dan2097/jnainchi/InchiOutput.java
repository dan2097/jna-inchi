package com.github.dan2097.jnainchi;

public class InchiOutput {
  
  private final String inchi;
  private final String auxInfo;
  private final String message;
  private final String log;
  private final InchiStatus status;

  InchiOutput(String inchi, String auxInfo, String message, String log, InchiStatus status) {
    this.inchi = inchi;
    this.auxInfo = auxInfo;
    this.message = message;
    this.log = log;
    this.status = status;
  }

  public String getInchi() {
    return inchi;
  }

  public String getAuxInfo() {
    return auxInfo;
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
