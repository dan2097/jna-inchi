package com.github.dan2097.jnainchi;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dan2097.jnainchi.InchiOptions.InchiOptionsBuilder;

import inchi.InchiLibrary;
import inchi.InchiLibrary.IXA_ATOMID;
import inchi.InchiLibrary.IXA_BONDID;
import inchi.InchiLibrary.IXA_INCHIBUILDER_HANDLE;
import inchi.InchiLibrary.IXA_INCHIBUILDER_OPTION;
import inchi.InchiLibrary.IXA_INCHIBUILDER_STEREOOPTION;
import inchi.InchiLibrary.IXA_MOL_HANDLE;
import inchi.InchiLibrary.IXA_STATUS_HANDLE;
import inchi.InchiLibrary.IXA_STEREOID;

public class JnaInchi {
  
  public JnaInchi() {
  }
  
  public InchiOutput toInchi(InchiInput inchiInput) {
    return toInchi(inchiInput, new InchiOptionsBuilder().build());
  }
  
  public InchiOutput toInchi(InchiInput inchiInput, InchiOptions options) {
    List<InchiAtom> atoms = inchiInput.getAtoms();
    int atomCount = atoms.size();
    if (atomCount > Short.MAX_VALUE) {
      throw new IllegalStateException("InChI is limited to 32767 atoms, input contained " + atomCount + " atoms");
    }
    List<InchiBond> bonds = inchiInput.getBonds();
    List<InchiStereo> stereos = inchiInput.getStereos();
    if (stereos.size() > Short.MAX_VALUE) {
      throw new IllegalStateException("Too many stereochemistry elements in input");
    }

    IXA_STATUS_HANDLE logger = InchiLibrary.IXA_STATUS_Create();
    IXA_MOL_HANDLE nativeMol = InchiLibrary.IXA_MOL_Create(logger);
    try {
      Map<InchiAtom, IXA_ATOMID> atomToNativeAtom = addAtoms(nativeMol, logger, atoms);
      addBonds(nativeMol, logger, bonds, atomToNativeAtom);
      addStereos(nativeMol, logger, stereos, atomToNativeAtom);
      return buildInchi(logger, nativeMol, options);
    }
    finally {
      InchiLibrary.IXA_MOL_Destroy(logger, nativeMol);
      InchiLibrary.IXA_STATUS_Destroy(logger);
    }
  }

  private Map<InchiAtom, IXA_ATOMID> addAtoms(IXA_MOL_HANDLE mol, IXA_STATUS_HANDLE logger, List<InchiAtom> atoms) {
    Map<InchiAtom, IXA_ATOMID> atomToNativeAtom = new HashMap<>();
    for (InchiAtom atom : atoms) {
      //For performance only call InchiLibrary when values differ from the defaults
      IXA_ATOMID nativeAtom = InchiLibrary.IXA_MOL_CreateAtom(logger, mol);
      atomToNativeAtom.put(atom, nativeAtom);
      
      if (atom.getX() != 0) {
        InchiLibrary.IXA_MOL_SetAtomX(logger, mol, nativeAtom, atom.getX());
      }
      if (atom.getY() != 0) {
        InchiLibrary.IXA_MOL_SetAtomY(logger, mol, nativeAtom, atom.getY());
      }
      if (atom.getZ() != 0) {
        InchiLibrary.IXA_MOL_SetAtomZ(logger, mol, nativeAtom, atom.getZ());
      }
      String elName = atom.getElName();
      if (!elName.equals("C")) {
        if (elName.length() > 5) {
          throw new IllegalArgumentException("Element name was too long: " + elName);
        }
        InchiLibrary.IXA_MOL_SetAtomElement(logger, mol, nativeAtom, elName);
      }
      if (atom.getIsotopicMass() != 0) {
        InchiLibrary.IXA_MOL_SetAtomMass(logger, mol, nativeAtom, atom.getIsotopicMass());
      }
      if (atom.getCharge() != 0) {
        InchiLibrary.IXA_MOL_SetAtomCharge(logger, mol, nativeAtom, atom.getCharge());
      }
      if (atom.getRadical() != InchiRadical.NONE) {
        InchiLibrary.IXA_MOL_SetAtomRadical(logger, mol, nativeAtom, atom.getRadical().getCode());
      }
      if (atom.getImplicitHydrogen() > 0) {
        //InChI also supports the concept of implicit deuterium/tritium, but as this is unusual this wrapper requires such cases to be given as explicit atoms
        InchiLibrary.IXA_MOL_SetAtomHydrogens(logger, mol, nativeAtom, 0, atom.getImplicitHydrogen());
      }
    }
    return atomToNativeAtom;
  }
  
  private void addBonds(IXA_MOL_HANDLE mol, IXA_STATUS_HANDLE logger, List<InchiBond> bonds, Map<InchiAtom, IXA_ATOMID> atomToNativeAtom) {
    for (InchiBond bond : bonds) {
      IXA_ATOMID nativeAtom1 = atomToNativeAtom.get(bond.getStart());
      IXA_ATOMID nativeAtom2 = atomToNativeAtom.get(bond.getEnd());
      if (nativeAtom1 == null || nativeAtom2 == null) {
        throw new IllegalStateException("Bond referenced an atom that was not part of the InchiInput");
      }
      IXA_BONDID nativeBond = InchiLibrary.IXA_MOL_CreateBond(logger, mol, nativeAtom1, nativeAtom2);
      if (bond.getType() != InchiBondType.SINGLE) {
        InchiLibrary.IXA_MOL_SetBondType(logger, mol, nativeBond, bond.getType().getCode());
      }
      //InchiLibrary.IXA_MOL_SetBondWedge(log, mol, nativeBond, vRefAtom, vDirection);
      //InchiLibrary.IXA_MOL_SetDblBondConfig(log, mol, nativeBond, vConfig);
    }
  }
  private void addStereos(IXA_MOL_HANDLE nativeMol, IXA_STATUS_HANDLE logger, List<InchiStereo> stereos, Map<InchiAtom, IXA_ATOMID> atomToNativeAtom) {
    for (InchiStereo stereo : stereos) {
      switch (stereo.getType()) {
      case Tetrahedral:
      {
        IXA_ATOMID centralAtom = atomToNativeAtom.get(stereo.getCentralAtom());
        InchiAtom[] atomsInCenter = stereo.getAtoms();
        InchiAtom atomsInCenter1 = atomsInCenter[0];
        InchiAtom atomsInCenter2 = atomsInCenter[1];
        InchiAtom atomsInCenter3 = atomsInCenter[2];
        InchiAtom atomsInCenter4 = atomsInCenter[3];
        
        IXA_ATOMID vertex1 = atomsInCenter1 != null ? atomToNativeAtom.get(atomsInCenter1) : null;
        IXA_ATOMID vertex2 = atomsInCenter2 != null ? atomToNativeAtom.get(atomsInCenter2) : null;
        IXA_ATOMID vertex3 = atomsInCenter3 != null ? atomToNativeAtom.get(atomsInCenter3) : null;
        IXA_ATOMID vertex4 = atomsInCenter4 != null ? atomToNativeAtom.get(atomsInCenter4) : null;
  
        IXA_STEREOID center = InchiLibrary.IXA_MOL_CreateStereoTetrahedron(logger, nativeMol, centralAtom, vertex1, vertex2, vertex3, vertex4);
        byte parity = stereo.getParity().getCode();
        InchiLibrary.IXA_MOL_SetStereoParity(logger, nativeMol, center, parity);
        break;
      }
      case Allene:
      {
        IXA_ATOMID centralAtom = atomToNativeAtom.get(stereo.getCentralAtom());
        InchiAtom[] atomsInCenter = stereo.getAtoms();
        InchiAtom atomsInCenter1 = atomsInCenter[0];
        InchiAtom atomsInCenter2 = atomsInCenter[1];
        InchiAtom atomsInCenter3 = atomsInCenter[2];
        InchiAtom atomsInCenter4 = atomsInCenter[3];
        
        IXA_ATOMID vertex1 = atomToNativeAtom.get(atomsInCenter1);
        IXA_ATOMID vertex2 = atomToNativeAtom.get(atomsInCenter2);
        IXA_ATOMID vertex3 = atomToNativeAtom.get(atomsInCenter3);
        IXA_ATOMID vertex4 = atomToNativeAtom.get(atomsInCenter4);
        IXA_STEREOID center = InchiLibrary.IXA_MOL_CreateStereoAntiRectangle(logger, nativeMol, centralAtom, vertex1, vertex2, vertex3, vertex4);
        byte parity = stereo.getParity().getCode();
        InchiLibrary.IXA_MOL_SetStereoParity(logger, nativeMol, center, parity);
        break;
      }
      case DoubleBond:
      {
        InchiAtom[] atomsInCenter = stereo.getAtoms();
        InchiAtom atomsInCenter1 = atomsInCenter[0];
        InchiAtom atomsInCenter2 = atomsInCenter[1];
        InchiAtom atomsInCenter3 = atomsInCenter[2];
        InchiAtom atomsInCenter4 = atomsInCenter[3];
  
        IXA_ATOMID vertex1 = atomToNativeAtom.get(atomsInCenter1);
        IXA_ATOMID vertex2 = atomToNativeAtom.get(atomsInCenter2);
        IXA_ATOMID vertex3 = atomToNativeAtom.get(atomsInCenter3);
        IXA_ATOMID vertex4 = atomToNativeAtom.get(atomsInCenter4);
        IXA_BONDID centralBond = InchiLibrary.IXA_MOL_GetCommonBond(logger, nativeMol, vertex2, vertex3);
        IXA_STEREOID center = InchiLibrary.IXA_MOL_CreateStereoRectangle(logger, nativeMol, centralBond, vertex1, vertex2, vertex3, vertex4);
        byte parity = stereo.getParity().getCode();
        InchiLibrary.IXA_MOL_SetStereoParity(logger, nativeMol, center, parity);
        break;
      }
      default:
        break;
      }
    }
  }

  private InchiOutput buildInchi(IXA_STATUS_HANDLE logger, IXA_MOL_HANDLE nativeMol, InchiOptions options) {
    IXA_INCHIBUILDER_HANDLE builder = InchiLibrary.IXA_INCHIBUILDER_Create(logger);
    try {
      InchiLibrary.IXA_INCHIBUILDER_SetMolecule(logger, builder, nativeMol);
      
      if (options.getTimeout() != 0){
        InchiLibrary.IXA_INCHIBUILDER_SetOption_Timeout(logger, builder, options.getTimeout());
      }
      for (InchiFlag flag : options.getFlags()) {
        switch (flag) {
        case AuxNone:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_AuxNone, true);
          break;
        case ChiralFlagOFF:
          InchiLibrary.IXA_MOL_SetChiral(logger, nativeMol, false);
          break;
        case ChiralFlagON:
          InchiLibrary.IXA_MOL_SetChiral(logger, nativeMol, true);
          break;
        case DoNotAddH:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_DoNotAddH, true);
          break;
        case FixedH:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_FixedH, true);
          break;
        case KET:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_KET, true);
          break;
        case LargeMolecules:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_LargeMolecules, true);
          break;
        case NEWPSOFF:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_NewPsOff, true);
          break;
        case OneFiveT:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_15T, true);
          break;
        case RecMet:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SLUUD, true);
          break;
        case SLUUD:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SUU, true);
          break;
        case SNon:
          InchiLibrary.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SNon);
          break;
        case SRac:
          InchiLibrary.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SRac);
          break;
        case SRel:
          InchiLibrary.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SRel);
          break;
        case SUCF:
          InchiLibrary.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SUCF);
          break;
        case SUU:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SUU, true);
          break;
        case SaveOpt:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SaveOpt, true);
          break;
        case WarnOnEmptyStructure:
          InchiLibrary.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_WarnOnEmptyStructure, true);
          break;
        default:
          throw new IllegalStateException("Unexpected InChI option flag: " + flag);
        }
      }

      String inchi = InchiLibrary.IXA_INCHIBUILDER_GetInChI(logger, builder);
      String auxInfo = InchiLibrary.IXA_INCHIBUILDER_GetAuxInfo(logger, builder);
      String log = InchiLibrary.IXA_INCHIBUILDER_GetLog(logger, builder);
      
      InchiStatus status = InchiStatus.SUCCESS;
      if (InchiLibrary.IXA_STATUS_HasError(logger)) {
        status = InchiStatus.ERROR;
      }
      else if (InchiLibrary.IXA_STATUS_HasWarning(logger)) {
        status = InchiStatus.WARNING;
      }
      
      StringBuilder sb = new StringBuilder();
      int messageCount = InchiLibrary.IXA_STATUS_GetCount(logger);
      for (int i = 0; i < messageCount; i++) {
        if (i > 0) {
          sb.append("; ");
        }
        sb.append(InchiLibrary.IXA_STATUS_GetMessage(logger, i));
      }
      return new InchiOutput(inchi, auxInfo, sb.toString(), log, status);
    }
    finally {
      InchiLibrary.IXA_INCHIBUILDER_Destroy(logger, builder); 
    }
  }

  public InchiOutput molToInchi(String molText) {
    return molToInchi(molText, new InchiOptionsBuilder().build());
  }
  
  public InchiOutput molToInchi(String molText, InchiOptions options) {
    IXA_STATUS_HANDLE logger = InchiLibrary.IXA_STATUS_Create();
    IXA_MOL_HANDLE nativeMol = InchiLibrary.IXA_MOL_Create(logger);
    try {
      InchiLibrary.IXA_MOL_ReadMolfile(logger, nativeMol, molText);
      return buildInchi(logger, nativeMol, options);
    }
    finally {
      InchiLibrary.IXA_MOL_Destroy(logger, nativeMol);
      InchiLibrary.IXA_STATUS_Destroy(logger);
    }
  }

  public InchiKeyOutput inchiToInchiKey(String inchi){
    byte[] inchiKey = new byte[28];
    byte[] szXtra1 = new byte[65];
    byte[] szXtra2 = new byte[65];
    InchiKeyStatus ret = InchiKeyStatus.of(InchiLibrary.GetINCHIKeyFromINCHI(inchi, 1, 1, inchiKey, szXtra1, szXtra2));
    try {
      return new InchiKeyOutput(new String(inchiKey, "UTF-8"), ret);
    } catch (UnsupportedEncodingException e) {
      // broken VM
      throw new RuntimeException(e);
    }
  }

}
