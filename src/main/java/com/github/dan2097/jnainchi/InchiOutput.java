package com.github.dan2097.jnainchi;

import inchi.tagINCHI_Output;

public class InchiOutput {
  
  private final String inchi;
  private final String auxInfo;
  private final String message;
  private final String log;
  private final InchiStatus status;

  public InchiOutput(tagINCHI_Output output, InchiStatus status) {
    this.inchi = output.szInChI;
    this.auxInfo = output.szAuxInfo;
    this.message = output.szMessage;
    this.log = output.szLog;
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
