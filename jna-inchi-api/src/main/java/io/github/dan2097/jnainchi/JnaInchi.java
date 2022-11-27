/**
 * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnainchi;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.sun.jna.NativeLong;
import com.sun.jna.Platform;

import io.github.dan2097.jnainchi.inchi.InchiLibrary;
import io.github.dan2097.jnainchi.inchi.InchiLibrary.IXA_BOND_WEDGE;
import io.github.dan2097.jnainchi.inchi.InchiLibrary.IXA_DBLBOND_CONFIG;
import io.github.dan2097.jnainchi.inchi.InchiLibrary.IXA_INCHIBUILDER_OPTION;
import io.github.dan2097.jnainchi.inchi.InchiLibrary.IXA_INCHIBUILDER_STEREOOPTION;
import io.github.dan2097.jnainchi.inchi.InchiLibrary.tagRetValGetINCHI;
import io.github.dan2097.jnainchi.inchi.InchiLibrary.tagRetValMOL2INCHI;
import io.github.dan2097.jnainchi.inchi.IxaFunctions;
import io.github.dan2097.jnainchi.inchi.IxaFunctions.IXA_ATOMID;
import io.github.dan2097.jnainchi.inchi.IxaFunctions.IXA_BONDID;
import io.github.dan2097.jnainchi.inchi.IxaFunctions.IXA_INCHIBUILDER_HANDLE;
import io.github.dan2097.jnainchi.inchi.IxaFunctions.IXA_MOL_HANDLE;
import io.github.dan2097.jnainchi.inchi.IxaFunctions.IXA_STATUS_HANDLE;
import io.github.dan2097.jnainchi.inchi.IxaFunctions.IXA_STEREOID;
import io.github.dan2097.jnainchi.inchi.tagINCHIStereo0D;
import io.github.dan2097.jnainchi.inchi.tagINCHI_Input;
import io.github.dan2097.jnainchi.inchi.tagINCHI_InputINCHI;
import io.github.dan2097.jnainchi.inchi.tagINCHI_Output;
import io.github.dan2097.jnainchi.inchi.tagINCHI_OutputStruct;
import io.github.dan2097.jnainchi.inchi.tagInchiAtom;
import io.github.dan2097.jnainchi.inchi.tagInchiInpData;

public class JnaInchi {
  
  private static final String platform;
  private static final Throwable libraryLoadingError;
  private static final int ISOTOPIC_SHIFT_RANGE_MIN = InchiLibrary.ISOTOPIC_SHIFT_FLAG - InchiLibrary.ISOTOPIC_SHIFT_MAX;
  private static final int ISOTOPIC_SHIFT_RANGE_MAX = InchiLibrary.ISOTOPIC_SHIFT_FLAG + InchiLibrary.ISOTOPIC_SHIFT_MAX;
  private static final Map<String, Integer> inchiBaseAtomicMasses = new HashMap<>();
  
  static {
    Throwable t = null;
    String p = null;
    try {
      p = Platform.RESOURCE_PREFIX;
      InchiLibrary.JNA_NATIVE_LIB.getName();
    }
    catch (Throwable e) { 
      t = e;
    }
    platform = p;
    libraryLoadingError = t;
    
    //avg mw from util.c
    inchiBaseAtomicMasses.put("H", 1);
    inchiBaseAtomicMasses.put("D", 2);
    inchiBaseAtomicMasses.put("T", 3);
    inchiBaseAtomicMasses.put("He", 4);
    inchiBaseAtomicMasses.put("Li", 7);
    inchiBaseAtomicMasses.put("Be", 9);
    inchiBaseAtomicMasses.put("B", 11);
    inchiBaseAtomicMasses.put("C", 12);
    inchiBaseAtomicMasses.put("N", 14);
    inchiBaseAtomicMasses.put("O", 16);
    inchiBaseAtomicMasses.put("F", 19);
    inchiBaseAtomicMasses.put("Ne", 20);
    inchiBaseAtomicMasses.put("Na", 23);
    inchiBaseAtomicMasses.put("Mg", 24);
    inchiBaseAtomicMasses.put("Al", 27);
    inchiBaseAtomicMasses.put("Si", 28);
    inchiBaseAtomicMasses.put("P", 31);
    inchiBaseAtomicMasses.put("S", 32);
    inchiBaseAtomicMasses.put("Cl", 35);
    inchiBaseAtomicMasses.put("Ar", 40);
    inchiBaseAtomicMasses.put("K", 39);
    inchiBaseAtomicMasses.put("Ca", 40);
    inchiBaseAtomicMasses.put("Sc", 45);
    inchiBaseAtomicMasses.put("Ti", 48);
    inchiBaseAtomicMasses.put("V", 51);
    inchiBaseAtomicMasses.put("Cr", 52);
    inchiBaseAtomicMasses.put("Mn", 55);
    inchiBaseAtomicMasses.put("Fe", 56);
    inchiBaseAtomicMasses.put("Co", 59);
    inchiBaseAtomicMasses.put("Ni", 59);
    inchiBaseAtomicMasses.put("Cu", 64);
    inchiBaseAtomicMasses.put("Zn", 65);
    inchiBaseAtomicMasses.put("Ga", 70);
    inchiBaseAtomicMasses.put("Ge", 73);
    inchiBaseAtomicMasses.put("As", 75);
    inchiBaseAtomicMasses.put("Se", 79);
    inchiBaseAtomicMasses.put("Br", 80);
    inchiBaseAtomicMasses.put("Kr", 84);
    inchiBaseAtomicMasses.put("Rb", 85);
    inchiBaseAtomicMasses.put("Sr", 88);
    inchiBaseAtomicMasses.put("Y", 89);
    inchiBaseAtomicMasses.put("Zr", 91);
    inchiBaseAtomicMasses.put("Nb", 93);
    inchiBaseAtomicMasses.put("Mo", 96);
    inchiBaseAtomicMasses.put("Tc", 98);
    inchiBaseAtomicMasses.put("Ru", 101);
    inchiBaseAtomicMasses.put("Rh", 103);
    inchiBaseAtomicMasses.put("Pd", 106);
    inchiBaseAtomicMasses.put("Ag", 108);
    inchiBaseAtomicMasses.put("Cd", 112);
    inchiBaseAtomicMasses.put("In", 115);
    inchiBaseAtomicMasses.put("Sn", 119);
    inchiBaseAtomicMasses.put("Sb", 122);
    inchiBaseAtomicMasses.put("Te", 128);
    inchiBaseAtomicMasses.put("I", 127);
    inchiBaseAtomicMasses.put("Xe", 131);
    inchiBaseAtomicMasses.put("Cs", 133);
    inchiBaseAtomicMasses.put("Ba", 137);
    inchiBaseAtomicMasses.put("La", 139);
    inchiBaseAtomicMasses.put("Ce", 140);
    inchiBaseAtomicMasses.put("Pr", 141);
    inchiBaseAtomicMasses.put("Nd", 144);
    inchiBaseAtomicMasses.put("Pm", 145);
    inchiBaseAtomicMasses.put("Sm", 150);
    inchiBaseAtomicMasses.put("Eu", 152);
    inchiBaseAtomicMasses.put("Gd", 157);
    inchiBaseAtomicMasses.put("Tb", 159);
    inchiBaseAtomicMasses.put("Dy", 163);
    inchiBaseAtomicMasses.put("Ho", 165);
    inchiBaseAtomicMasses.put("Er", 167);
    inchiBaseAtomicMasses.put("Tm", 169);
    inchiBaseAtomicMasses.put("Yb", 173);
    inchiBaseAtomicMasses.put("Lu", 175);
    inchiBaseAtomicMasses.put("Hf", 178);
    inchiBaseAtomicMasses.put("Ta", 181);
    inchiBaseAtomicMasses.put("W", 184);
    inchiBaseAtomicMasses.put("Re", 186);
    inchiBaseAtomicMasses.put("Os", 190);
    inchiBaseAtomicMasses.put("Ir", 192);
    inchiBaseAtomicMasses.put("Pt", 195);
    inchiBaseAtomicMasses.put("Au", 197);
    inchiBaseAtomicMasses.put("Hg", 201);
    inchiBaseAtomicMasses.put("Tl", 204);
    inchiBaseAtomicMasses.put("Pb", 207);
    inchiBaseAtomicMasses.put("Bi", 209);
    inchiBaseAtomicMasses.put("Po", 209);
    inchiBaseAtomicMasses.put("At", 210);
    inchiBaseAtomicMasses.put("Rn", 222);
    inchiBaseAtomicMasses.put("Fr", 223);
    inchiBaseAtomicMasses.put("Ra", 226);
    inchiBaseAtomicMasses.put("Ac", 227);
    inchiBaseAtomicMasses.put("Th", 232);
    inchiBaseAtomicMasses.put("Pa", 231);
    inchiBaseAtomicMasses.put("U", 238);
    inchiBaseAtomicMasses.put("Np", 237);
    inchiBaseAtomicMasses.put("Pu", 244);
    inchiBaseAtomicMasses.put("Am", 243);
    inchiBaseAtomicMasses.put("Cm", 247);
    inchiBaseAtomicMasses.put("Bk", 247);
    inchiBaseAtomicMasses.put("Cf", 251);
    inchiBaseAtomicMasses.put("Es", 252);
    inchiBaseAtomicMasses.put("Fm", 257);
    inchiBaseAtomicMasses.put("Md", 258);
    inchiBaseAtomicMasses.put("No", 259);
    inchiBaseAtomicMasses.put("Lr", 260);
    inchiBaseAtomicMasses.put("Rf", 261);
    inchiBaseAtomicMasses.put("Db", 270);
    inchiBaseAtomicMasses.put("Sg", 269);
    inchiBaseAtomicMasses.put("Bh", 270);
    inchiBaseAtomicMasses.put("Hs", 270);
    inchiBaseAtomicMasses.put("Mt", 278);
    inchiBaseAtomicMasses.put("Ds", 281);
    inchiBaseAtomicMasses.put("Rg", 281);
    inchiBaseAtomicMasses.put("Cn", 285);
    inchiBaseAtomicMasses.put("Nh", 278);
    inchiBaseAtomicMasses.put("Fl", 289);
    inchiBaseAtomicMasses.put("Mc", 289);
    inchiBaseAtomicMasses.put("Lv", 293);
    inchiBaseAtomicMasses.put("Ts", 297);
    inchiBaseAtomicMasses.put("Og", 294);
  }
    
  public static InchiOutput toInchi(InchiInput inchiInput) {
    return toInchi(inchiInput, InchiOptions.DEFAULT_OPTIONS);
  }
  
  public static InchiOutput toInchi(InchiInput inchiInput, InchiOptions options) {
    checkLibrary();
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

    IXA_STATUS_HANDLE logger = IxaFunctions.IXA_STATUS_Create();
    IXA_MOL_HANDLE nativeMol = IxaFunctions.IXA_MOL_Create(logger);
    IxaFunctions.IXA_MOL_ReserveSpace(logger, nativeMol, atomCount, bonds.size(), stereos.size());
    try {
      Map<InchiAtom, IXA_ATOMID> atomToNativeAtom = addAtoms(nativeMol, logger, atoms);
      addBonds(nativeMol, logger, bonds, atomToNativeAtom);
      addStereos(nativeMol, logger, stereos, atomToNativeAtom);
      return buildInchi(logger, nativeMol, options);
    }
    finally {
      IxaFunctions.IXA_MOL_Destroy(logger, nativeMol);
      IxaFunctions.IXA_STATUS_Destroy(logger);
    }
  }

  private static Map<InchiAtom, IXA_ATOMID> addAtoms(IXA_MOL_HANDLE mol, IXA_STATUS_HANDLE logger, List<InchiAtom> atoms) {
    Map<InchiAtom, IXA_ATOMID> atomToNativeAtom = new HashMap<>();
    for (InchiAtom atom : atoms) {
      //For performance only call IxaFunctions when values differ from the defaults
      IXA_ATOMID nativeAtom = IxaFunctions.IXA_MOL_CreateAtom(logger, mol);
      atomToNativeAtom.put(atom, nativeAtom);
      
      if (atom.getX() != 0) {
        IxaFunctions.IXA_MOL_SetAtomX(logger, mol, nativeAtom, atom.getX());
      }
      if (atom.getY() != 0) {
        IxaFunctions.IXA_MOL_SetAtomY(logger, mol, nativeAtom, atom.getY());
      }
      if (atom.getZ() != 0) {
        IxaFunctions.IXA_MOL_SetAtomZ(logger, mol, nativeAtom, atom.getZ());
      }
      String elName = atom.getElName();
      if (!elName.equals("C")) {
        if (elName.length() > 5) {
          throw new IllegalArgumentException("Element name was too long: " + elName);
        }
        IxaFunctions.IXA_MOL_SetAtomElement(logger, mol, nativeAtom, elName);
      }
      if (atom.getIsotopicMass() != 0) {
        IxaFunctions.IXA_MOL_SetAtomMass(logger, mol, nativeAtom, atom.getIsotopicMass());
      }
      if (atom.getCharge() != 0) {
        IxaFunctions.IXA_MOL_SetAtomCharge(logger, mol, nativeAtom, atom.getCharge());
      }
      if (atom.getRadical() != InchiRadical.NONE) {
        IxaFunctions.IXA_MOL_SetAtomRadical(logger, mol, nativeAtom, atom.getRadical().getCode());
      }
      if (atom.getImplicitHydrogen() != 0) {
        IxaFunctions.IXA_MOL_SetAtomHydrogens(logger, mol, nativeAtom, 0, atom.getImplicitHydrogen());
      }
      if (atom.getImplicitProtium() != 0) {
         IxaFunctions.IXA_MOL_SetAtomHydrogens(logger, mol, nativeAtom, 1, atom.getImplicitProtium());
      }
      if (atom.getImplicitDeuterium() != 0) {
         IxaFunctions.IXA_MOL_SetAtomHydrogens(logger, mol, nativeAtom, 2, atom.getImplicitDeuterium());
      }
      if (atom.getImplicitTritium() != 0) {
          IxaFunctions.IXA_MOL_SetAtomHydrogens(logger, mol, nativeAtom, 3, atom.getImplicitTritium());
      }
    }
    return atomToNativeAtom;
  }
  
  private static void addBonds(IXA_MOL_HANDLE mol, IXA_STATUS_HANDLE logger, List<InchiBond> bonds, Map<InchiAtom, IXA_ATOMID> atomToNativeAtom) {
    for (InchiBond bond : bonds) {
      IXA_ATOMID nativeAtom1 = atomToNativeAtom.get(bond.getStart());
      IXA_ATOMID nativeAtom2 = atomToNativeAtom.get(bond.getEnd());
      if (nativeAtom1 == null || nativeAtom2 == null) {
        throw new IllegalStateException("Bond referenced an atom that was not part of the InchiInput");
      }
      IXA_BONDID nativeBond = IxaFunctions.IXA_MOL_CreateBond(logger, mol, nativeAtom1, nativeAtom2);
      InchiBondType bondType = bond.getType();
      if (bondType != InchiBondType.SINGLE) {
        IxaFunctions.IXA_MOL_SetBondType(logger, mol, nativeBond, bondType.getCode());
      }
      switch (bond.getStereo()) {
      case DOUBLE_EITHER:
        //Default is to perceive configuration from 2D coordinates
        IxaFunctions.IXA_MOL_SetDblBondConfig(logger, mol, nativeBond, IXA_DBLBOND_CONFIG.IXA_DBLBOND_CONFIG_EITHER);
        break;
      case SINGLE_1DOWN:
        IxaFunctions.IXA_MOL_SetBondWedge(logger, mol, nativeBond, nativeAtom1, IXA_BOND_WEDGE.IXA_BOND_WEDGE_DOWN);
        break;
      case SINGLE_1EITHER:
        IxaFunctions.IXA_MOL_SetBondWedge(logger, mol, nativeBond, nativeAtom1, IXA_BOND_WEDGE.IXA_BOND_WEDGE_EITHER);
        break;
      case SINGLE_1UP:
        IxaFunctions.IXA_MOL_SetBondWedge(logger, mol, nativeBond, nativeAtom1, IXA_BOND_WEDGE.IXA_BOND_WEDGE_UP);
        break;
      case SINGLE_2DOWN:
        IxaFunctions.IXA_MOL_SetBondWedge(logger, mol, nativeBond, nativeAtom2, IXA_BOND_WEDGE.IXA_BOND_WEDGE_DOWN);
        break;
      case SINGLE_2EITHER:
        IxaFunctions.IXA_MOL_SetBondWedge(logger, mol, nativeBond, nativeAtom2, IXA_BOND_WEDGE.IXA_BOND_WEDGE_EITHER);
        break;
      case SINGLE_2UP:
        IxaFunctions.IXA_MOL_SetBondWedge(logger, mol, nativeBond, nativeAtom2, IXA_BOND_WEDGE.IXA_BOND_WEDGE_UP);
        break;
      case NONE:
        break;
      }  
    }
  }
  private static void addStereos(IXA_MOL_HANDLE nativeMol, IXA_STATUS_HANDLE logger, List<InchiStereo> stereos, Map<InchiAtom, IXA_ATOMID> atomToNativeAtom) {
    for (InchiStereo stereo : stereos) {
      InchiStereoType type = stereo.getType();
      if (type == InchiStereoType.None) {
        continue;
      }
      InchiAtom[] atomsInCenter = stereo.getAtoms();      
      IXA_ATOMID vertex1 = getStereoVertex(atomToNativeAtom, atomsInCenter[0]);
      IXA_ATOMID vertex2 = getStereoVertex(atomToNativeAtom, atomsInCenter[1]);
      IXA_ATOMID vertex3 = getStereoVertex(atomToNativeAtom, atomsInCenter[2]);
      IXA_ATOMID vertex4 = getStereoVertex(atomToNativeAtom, atomsInCenter[3]);
     
      IXA_STEREOID center;
      switch (type) {
      case Tetrahedral:
      {
        IXA_ATOMID centralAtom = atomToNativeAtom.get(stereo.getCentralAtom());
        if (centralAtom == null) {
          throw new IllegalStateException("Stereo configuration central atom referenced an atom that does not exist");
        }
        center = IxaFunctions.IXA_MOL_CreateStereoTetrahedron(logger, nativeMol, centralAtom, vertex1, vertex2, vertex3, vertex4);
        break;
      }
      case Allene:
      {
        IXA_ATOMID centralAtom = atomToNativeAtom.get(stereo.getCentralAtom());
        if (centralAtom == null) {
          throw new IllegalStateException("Stereo configuration central atom referenced an atom that does not exist");
        }
        center = IxaFunctions.IXA_MOL_CreateStereoAntiRectangle(logger, nativeMol, centralAtom, vertex1, vertex2, vertex3, vertex4);
        break;
      }
      case DoubleBond:
      {
        IXA_BONDID centralBond = IxaFunctions.IXA_MOL_GetCommonBond(logger, nativeMol, vertex2, vertex3);
        if (centralBond == null) {
          throw new IllegalStateException("Could not find olefin/cumulene central bond");
        }
        //We intentionally pass dummy values for vertex2/vertex3, as the IXA API doesn't actually need these as long as vertex1 and vertex4 aren't implicit hydrogen
        center = IxaFunctions.IXA_MOL_CreateStereoRectangle(logger, nativeMol, centralBond, vertex1, IxaFunctions.IXA_ATOMID_IMPLICIT_H, IxaFunctions.IXA_ATOMID_IMPLICIT_H, vertex4);
        break;
      }
      default:
        throw new IllegalStateException("Unexpected InChI stereo type:" + type);
      }
      byte parity = stereo.getParity().getCode();
      IxaFunctions.IXA_MOL_SetStereoParity(logger, nativeMol, center, parity);
    }
  }

  private static IXA_ATOMID getStereoVertex(Map<InchiAtom, IXA_ATOMID> atomToNativeAtom, InchiAtom inchiAtom) {
    if (InchiStereo.STEREO_IMPLICIT_H == inchiAtom) {
      return IxaFunctions.IXA_ATOMID_IMPLICIT_H;
    }
    IXA_ATOMID vertex = atomToNativeAtom.get(inchiAtom);
    if (vertex == null) {
      throw new IllegalStateException("Stereo configuration referenced an atom that does not exist");
    }
    return vertex;
  }

  private static InchiOutput buildInchi(IXA_STATUS_HANDLE logger, IXA_MOL_HANDLE nativeMol, InchiOptions options) {
    IXA_INCHIBUILDER_HANDLE builder = IxaFunctions.IXA_INCHIBUILDER_Create(logger);
    try {
      IxaFunctions.IXA_INCHIBUILDER_SetMolecule(logger, builder, nativeMol);
      
      long timeoutMilliSecs = options.getTimeoutMilliSeconds();
      if (timeoutMilliSecs != 0) {
        IxaFunctions.IXA_INCHIBUILDER_SetOption_Timeout_MilliSeconds(logger, builder, timeoutMilliSecs);
      }
      for (InchiFlag flag : options.getFlags()) {
        switch (flag) {
        case AuxNone:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_AuxNone, true);
          break;
        case ChiralFlagOFF:
          IxaFunctions.IXA_MOL_SetChiral(logger, nativeMol, false);
          break;
        case ChiralFlagON:
          IxaFunctions.IXA_MOL_SetChiral(logger, nativeMol, true);
          break;
        case DoNotAddH:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_DoNotAddH, true);
          break;
        case FixedH:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_FixedH, true);
          break;
        case KET:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_KET, true);
          break;
        case LargeMolecules:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_LargeMolecules, true);
          break;
        case NEWPSOFF:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_NewPsOff, true);
          break;
        case OneFiveT:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_15T, true);
          break;
        case RecMet:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_RecMet, true);
          break;
        case SLUUD:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SLUUD, true);
          break;
        case SNon:
          IxaFunctions.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SNon);
          break;
        case SRac:
          IxaFunctions.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SRac);
          break;
        case SRel:
          IxaFunctions.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SRel);
          break;
        case SUCF:
          IxaFunctions.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SUCF);
          break;
        case SAbs:
          IxaFunctions.IXA_INCHIBUILDER_SetOption_Stereo(logger, builder, IXA_INCHIBUILDER_STEREOOPTION.IXA_INCHIBUILDER_STEREOOPTION_SAbs);
          break;
        case SUU:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SUU, true);
          break;
        case SaveOpt:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SaveOpt, true);
          break;
        case WarnOnEmptyStructure:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_WarnOnEmptyStructure, true);
          break;
        case NoWarnings:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_NoWarnings, true);
          break;
        case LooseTSACheck:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_LooseTSACheck, true);
          break;
        case Polymers:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_Polymers, true);
          break;
        case Polymers105:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_Polymers105, true);
          break;
        case FoldCRU:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_FoldCRU, true);
          break;
        case NoFrameShift:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_NoFrameShift, true);
          break;
        case NoEdits:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_NoEdits, true);
          break;
        case NPZz:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_NPZZ, true);
          break;
        case SAtZz:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_SATZZ, true);
          break;
        case OutErrInChI:
          IxaFunctions.IXA_INCHIBUILDER_SetOption(logger, builder, IXA_INCHIBUILDER_OPTION.IXA_INCHIBUILDER_OPTION_OutErrInChI, true);
          break;
        default:
          throw new IllegalStateException("Unexpected InChI option flag: " + flag);
        }
      }

      String inchi = IxaFunctions.IXA_INCHIBUILDER_GetInChI(logger, builder);
      String auxInfo = IxaFunctions.IXA_INCHIBUILDER_GetAuxInfo(logger, builder);
      String log = IxaFunctions.IXA_INCHIBUILDER_GetLog(logger, builder);
      
      InchiStatus status = InchiStatus.SUCCESS;
      if (IxaFunctions.IXA_STATUS_HasError(logger)) {
        status = InchiStatus.ERROR;
      }
      else if (IxaFunctions.IXA_STATUS_HasWarning(logger)) {
        status = InchiStatus.WARNING;
      }
      
      StringBuilder sb = new StringBuilder();
      int messageCount = IxaFunctions.IXA_STATUS_GetCount(logger);
      for (int i = 0; i < messageCount; i++) {
        if (i > 0) {
          sb.append("; ");
        }
        sb.append(IxaFunctions.IXA_STATUS_GetMessage(logger, i));
      }
      return new InchiOutput(inchi, auxInfo, sb.toString(), log, status);
    }
    finally {
      IxaFunctions.IXA_INCHIBUILDER_Destroy(logger, builder); 
    }
  }

  public static InchiOutput molToInchi(String molText) {
    return molToInchi(molText, InchiOptions.DEFAULT_OPTIONS);
  }
  
  public static InchiOutput molToInchi(String molText, InchiOptions options) {
    checkLibrary();
    tagINCHI_Output nativeOutput = new tagINCHI_Output();
    try {
      int ret = InchiLibrary.MakeINCHIFromMolfileText(molText, options.toString(), nativeOutput);
      InchiStatus status;
      switch (ret) {
      case tagRetValMOL2INCHI.mol2inchi_Ret_OKAY:
        status = InchiStatus.SUCCESS;
        break;
      case tagRetValMOL2INCHI.mol2inchi_Ret_WARNING:
        status = InchiStatus.WARNING;
        break;
      case tagRetValMOL2INCHI.mol2inchi_Ret_EOF:
      case tagRetValMOL2INCHI.mol2inchi_Ret_ERROR:
      case tagRetValMOL2INCHI.mol2inchi_Ret_ERROR_get:
      case tagRetValMOL2INCHI.mol2inchi_Ret_ERROR_comp:
        status = InchiStatus.ERROR;
        break;
      default:
        status = InchiStatus.ERROR;
        break;
      }
      // The way nativeOutput.szLog is truncated can be a bit odd, but this seems pseudo-intentional, see copy_corrected_log_tail in inchi_dll.c 
      return new InchiOutput(nativeOutput.szInChI, nativeOutput.szAuxInfo, nativeOutput.szMessage, nativeOutput.szLog, status);
    }
    finally {
      InchiLibrary.FreeINCHI(nativeOutput);
    }
  }

  /**
   * Converts InChI into InChI for validation purposes.
   * It may also be used to filter out specific layers.
   * For instance, SNon would remove the stereochemical layer.
   * Omitting FixedH and/or RecMet would remove Fixed-H or Reconnected layers.
   * @param inchi
   * @param options
   * @return
   */
  public static InchiOutput inchiToInchi(String inchi, InchiOptions options) {
    checkLibrary();
    IXA_STATUS_HANDLE logger = IxaFunctions.IXA_STATUS_Create();
    IXA_MOL_HANDLE nativeMol = IxaFunctions.IXA_MOL_Create(logger);
    try {
      IxaFunctions.IXA_MOL_ReadInChI(logger, nativeMol, inchi);
      return buildInchi(logger, nativeMol, options);
    }
    finally {
      IxaFunctions.IXA_MOL_Destroy(logger, nativeMol);
      IxaFunctions.IXA_STATUS_Destroy(logger);
    }
  }

  public static InchiKeyOutput inchiToInchiKey(String inchi) {
    checkLibrary();
    byte[] inchiKeyBytes = new byte[28];
    byte[] szXtra1Bytes = new byte[65];
    byte[] szXtra2Bytes = new byte[65];
    InchiKeyStatus ret = InchiKeyStatus.of(InchiLibrary.GetINCHIKeyFromINCHI(inchi, 1, 1, inchiKeyBytes, szXtra1Bytes, szXtra2Bytes));
    String inchiKeyStr = new String(inchiKeyBytes, StandardCharsets.UTF_8).trim();
    String szXtra1 = new String(szXtra1Bytes, StandardCharsets.UTF_8).trim();
    String szXtra2 = new String(szXtra2Bytes, StandardCharsets.UTF_8).trim();
    return new InchiKeyOutput(inchiKeyStr, ret, szXtra1, szXtra2);
  }
  
  /**
   * Check if the string represents a valid InChI/StdInChI
   * If strict is true, try to perform InChI2InChI conversion; returns success if a resulting InChI string exactly matches source.
   * Be cautious: the result may be too strict, i.e. a 'false alarm', due to imperfection of conversion.
   * @param inchi
   * @param strict if false, just briefly check for proper layout (prefix, version, etc.)
   * @return InchiCheckStatus
   */
  public static InchiCheckStatus checkInchi(String inchi, boolean strict) {
    checkLibrary();
    return InchiCheckStatus.of(InchiLibrary.CheckINCHI(inchi, strict));
  }
  
  /**
   * Check if the string represents valid InChIKey
   * @param inchiKey
   * @return InchiKeyCheckStatus
   */
  public static InchiKeyCheckStatus checkInchiKey(String inchiKey) {
    checkLibrary();
    return InchiKeyCheckStatus.of(InchiLibrary.CheckINCHIKey(inchiKey));
  }
  
  /**
   * Creates the input data structure for InChI generation out of the auxiliary information (AuxInfo) 
   * string produced by previous InChI generator calls
   * @param auxInfo contains ASCIIZ string of InChI output for a single structure or only the AuxInfo line
   * @param doNotAddH if true then InChI will not be allowed to add implicit H
   * @param diffUnkUndfStereo if true, use different labels for unknown and undefined stereo
   * @return
   */
  public static InchiInputFromAuxinfoOutput getInchiInputFromAuxInfo(String auxInfo, boolean doNotAddH, boolean diffUnkUndfStereo) {
    checkLibrary();
    tagINCHI_Input pInp = new tagINCHI_Input();
    tagInchiInpData input = new tagInchiInpData(pInp);
    try {
      InchiStatus status = getInchiStatus(InchiLibrary.Get_inchi_Input_FromAuxInfo(auxInfo, doNotAddH, diffUnkUndfStereo, input));
      
      InchiInput inchiInput = new InchiInput();
      
      tagINCHI_Input populatedInput = input.pInp;
      if (populatedInput.num_atoms > 0) {
        tagInchiAtom[] nativeAtoms = new tagInchiAtom[populatedInput.num_atoms];
        populatedInput.atom.toArray(nativeAtoms);
        nativeToJavaAtoms(inchiInput, nativeAtoms);
        nativeToJavaBonds(inchiInput, nativeAtoms);
      }
      if (populatedInput.num_stereo0D > 0) {
        tagINCHIStereo0D[] nativeStereos = new tagINCHIStereo0D[populatedInput.num_stereo0D];
        populatedInput.stereo0D.toArray(nativeStereos);
        nativeToJavaStereos(inchiInput, nativeStereos);
      }
      String message = toString(input.szErrMsg);
      Boolean chiralFlag = null;
      if (input.bChiral == 1) {
        chiralFlag = true;
      }
      else if (input.bChiral == 2) {
        chiralFlag = false;
      }
      return new InchiInputFromAuxinfoOutput(inchiInput, chiralFlag, message, status);
    }
    finally {
      InchiLibrary.Free_inchi_Input(pInp);
      input.clear();
    }
  }
  
  public static InchiInputFromInchiOutput getInchiInputFromInchi(String inchi) {
    return getInchiInputFromInchi(inchi, InchiOptions.DEFAULT_OPTIONS);
  }
  
  public static InchiInputFromInchiOutput getInchiInputFromInchi(String inchi, InchiOptions options) {
    checkLibrary();
    tagINCHI_InputINCHI input = new tagINCHI_InputINCHI(inchi, options.toString());
    tagINCHI_OutputStruct output = new tagINCHI_OutputStruct();
    try {
      InchiStatus status = getInchiStatus(InchiLibrary.GetStructFromINCHI(input, output));
      InchiInput inchiInput = new InchiInput();
      
      if (output.num_atoms > 0) {
        tagInchiAtom[] nativeAtoms = new tagInchiAtom[output.num_atoms];
        output.atom.toArray(nativeAtoms);
        nativeToJavaAtoms(inchiInput, nativeAtoms);
        nativeToJavaBonds(inchiInput, nativeAtoms);
      }
      if (output.num_stereo0D > 0) {
        tagINCHIStereo0D[] nativeStereos = new tagINCHIStereo0D[output.num_stereo0D];
        output.stereo0D.toArray(nativeStereos);
        nativeToJavaStereos(inchiInput, nativeStereos);
      }
      String message = output.szMessage;
      String log = output.szLog;
      NativeLong[] nativeFlags = output.WarningFlags;//This is a flattened multi-dimensional array, unflatten as we convert
      long[][] warningFlags = new long[2][2];
      for (int i = 0; i < nativeFlags.length; i++) {
        long val = nativeFlags[i].longValue();
        switch (i) {
        case 0:
          warningFlags[0][0] = val;
          break;
        case 1:
          warningFlags[0][1] = val;
          break;
        case 2:
          warningFlags[1][0] = val;
          break;
        case 3:
          warningFlags[1][1] = val;
          break;
        default:
          break;
        }
      }
      return new InchiInputFromInchiOutput(inchiInput, message, log, status, warningFlags);
    }
    finally {
      InchiLibrary.FreeStructFromINCHI(output);
      input.clear();
    }
  }

  private static void nativeToJavaAtoms(InchiInput inchiInput, tagInchiAtom[] nativeAtoms) {
    for (int i = 0, numAtoms = nativeAtoms.length; i < numAtoms; i++) {
      tagInchiAtom nativeAtom = nativeAtoms[i];
      String elSymbol = toString(nativeAtom.elname);
      InchiAtom atom = new InchiAtom(elSymbol);
      atom.setX(nativeAtom.x);
      atom.setY(nativeAtom.y);
      atom.setZ(nativeAtom.z);
      atom.setImplicitHydrogen(nativeAtom.num_iso_H[0]);
      atom.setImplicitProtium(nativeAtom.num_iso_H[1]);
      atom.setImplicitDeuterium(nativeAtom.num_iso_H[2]);
      atom.setImplicitTritium(nativeAtom.num_iso_H[3]);
      int isotopicMass = nativeAtom.isotopic_mass;
      if (isotopicMass >= ISOTOPIC_SHIFT_RANGE_MIN && isotopicMass <= ISOTOPIC_SHIFT_RANGE_MAX) {
        //isotopic mass contains a delta from a hardcoded base mass
        int baseMass = inchiBaseAtomicMasses.getOrDefault(elSymbol, 0);
        int delta = isotopicMass - InchiLibrary.ISOTOPIC_SHIFT_FLAG;
        isotopicMass = baseMass + delta;
      }
      atom.setIsotopicMass(isotopicMass);
      atom.setRadical(InchiRadical.of(nativeAtom.radical));
      atom.setCharge(nativeAtom.charge);
      inchiInput.addAtom(atom);
    }
  }

  private static void nativeToJavaBonds(InchiInput inchiInput, tagInchiAtom[] nativeAtoms) {
    int numAtoms = nativeAtoms.length;
    boolean[] seenAtoms = new boolean[numAtoms];
    for (int i = 0; i < numAtoms; i++) {
      tagInchiAtom nativeAtom = nativeAtoms[i];
      int numBonds = nativeAtom.num_bonds;
      if (numBonds > 0) {
        InchiAtom atom = inchiInput.getAtom(i);
        for (int j = 0; j < numBonds; j++) {
          int neighborIdx = nativeAtom.neighbor[j];
          if (seenAtoms[neighborIdx]) {
            //Only add each bond once
            continue;
          }
          InchiAtom neighbor = inchiInput.getAtom(neighborIdx);
          InchiBondType bondType = InchiBondType.of(nativeAtom.bond_type[j]);
          InchiBondStereo bondStereo = InchiBondStereo.of(nativeAtom.bond_stereo[j]);
          inchiInput.addBond(new InchiBond(atom, neighbor, bondType, bondStereo));
        }
      }
      seenAtoms[i] = true;
    }
  }

  private static void nativeToJavaStereos(InchiInput inchiInput, tagINCHIStereo0D[] nativeStereos) {
    for (tagINCHIStereo0D nativeStereo : nativeStereos) {
      InchiAtom[] atoms = new InchiAtom[4];
      //idxToAtom will give null for -1 input (implicit hydrogen)
      for (int i = 0; i < 4; i++) {
        int idx = nativeStereo.neighbor[i];
        atoms[i] = idx >=0 ?  inchiInput.getAtom(idx) : null;
      }

      InchiAtom centralAtom = nativeStereo.central_atom >=0 ? inchiInput.getAtom(nativeStereo.central_atom) : null;
      InchiStereoType stereoType = InchiStereoType.of(nativeStereo.type);
      InchiStereoParity parity = InchiStereoParity.of(nativeStereo.parity);
      
      inchiInput.addStereo(new InchiStereo(atoms, centralAtom, stereoType, parity));
    }
  }

  private static InchiStatus getInchiStatus(int ret) {
    switch (ret) {
    case tagRetValGetINCHI.inchi_Ret_OKAY:/* Success; no errors or warnings*/
      return InchiStatus.SUCCESS;
    case tagRetValGetINCHI.inchi_Ret_EOF:/* no structural data has been provided */
    case tagRetValGetINCHI.inchi_Ret_WARNING:/* Success; warning(s) issued*/
      return InchiStatus.WARNING;
    case tagRetValGetINCHI.inchi_Ret_ERROR:/* Error: no InChI has been created */
    case tagRetValGetINCHI.inchi_Ret_FATAL:/* Severe error: no InChI has been created (typically, memory allocation failure) */
    case tagRetValGetINCHI.inchi_Ret_UNKNOWN:/* Unknown program error */
    case tagRetValGetINCHI.inchi_Ret_BUSY:/* Previous call to InChI has not returned yet*/
      return InchiStatus.ERROR;
    default:
      return InchiStatus.ERROR;
    }
  }

  private static String toString(byte[] cstr) {
    StringBuilder sb = new StringBuilder(cstr.length);
    for (int i = 0; i < cstr.length; i++) {
      char ch = (char) cstr[i];
      if (ch == '\0') {
        break;
      }
      sb.append(ch);
    }
    return sb.toString();
  }
  
  
  /**
   * Returns the version of the wrapped InChI C library
   * @return Version number String
   */
  public static String getInchiLibraryVersion() {
    try(InputStream is = JnaInchi.class.getResourceAsStream("jnainchi_build.props")) {
      Properties props = new Properties();
      props.load(is);
      return props.getProperty("inchi_version");
    }
    catch (Exception e) {
      return null;
    }
  }
  
  /**
   * Returns the version of the JNA-InChI Java library
   * @return Version number String
   */
  public static String getJnaInchiVersion() {
    try(InputStream is = JnaInchi.class.getResourceAsStream("jnainchi_build.props")) {
      Properties props = new Properties();
      props.load(is);
      return props.getProperty("jnainchi_version");
    }
    catch (Exception e) {
      return null;
    }
  }

  private static void checkLibrary() {
    if (libraryLoadingError != null) {
      throw new RuntimeException("Error loading InChI native code. Please check that the binaries for your platform (" + platform + ") have been included on the classpath.", libraryLoadingError);
    }
  }

}
