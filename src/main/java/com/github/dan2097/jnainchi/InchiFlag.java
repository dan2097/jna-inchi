package com.github.dan2097.jnainchi;

public enum InchiFlag {

  /** Both ends of wedge point to stereocenters [compatible with standard InChI]*/
  NEWPSOFF,
  
  /** All hydrogens in input structure are explicit [compatible with standard InChI]*/
  DoNotAddH,
  
  /** Ignore stereo [compatible with standard InChI]*/
  SNon,
  
  /** Use relative stereo*/
  SRel,
  
  /** Use racemic stereo*/
  SRac,
  
  /** Use Chiral Flag in MOL/SD file record: if On – use Absolute stereo, Off – use Relative stereo*/
  SUCF,
  
  /** Set chiral flag ON*/
  ChiralFlagON,
  
  /** Set chiral flag OFF*/
  ChiralFlagOFF,
  
  /** Allows input of molecules up to 32767 atoms [Produces 'InChI=1B' indicating beta status of resulting identifiers]*/
  LargeMolecules,
  
  /** Always indicate unknown/undefined stereo*/
  SUU,
  
  /** Stereo labels for "unknown" and "undefined" are different, 'u' and '?', resp.*/
  SLUUD,
  
  /** Include Fixed H layer*/
  FixedH,
  
  /** Include reconnected metals results*/
  RecMet,
  
  /** Account for keto-enol tautomerism (experimental)*/
  KET,
  
  /** Account for 1,5-tautomerism (experimental)*/
  OneFiveT,
  
  /** Omit auxiliary information*/
  AuxNone,
  
  /** Warn and produce empty InChI for empty structure*/
  WarnOnEmptyStructure,
  
  /** Save custom InChI creation options (non-standard InChI)*/
  SaveOpt;
  
  @Override
  public String toString() {
    if (this == OneFiveT) {
      //Java doesn't allow enusm to start with digits
      return "15T";
    }
    else {
      return super.toString();
    }
  }
}
