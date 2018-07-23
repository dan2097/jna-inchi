package com.github.dan2097.jnainchi;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.dan2097.jnainchi.InchiOptions.InchiOptionsBuilder;

import inchi.InchiLibrary;
import inchi.tagINCHIStereo0D;
import inchi.tagINCHI_Input;
import inchi.tagINCHI_Output;
import inchi.tagInchiAtom;

public class JnaInchi {
  
  public JnaInchi() {
  }
  
  public InchiOutput toInchi(InchiInput inchiInput) {
    return toInchi(inchiInput, new InchiOptionsBuilder().build());
  }
  
  public InchiOutput toInchi(InchiInput inchiInput, InchiOptions options) {
    tagINCHI_Input nativeInput = inputToNative(inchiInput, options);
    tagINCHI_Output nativeOutput = new tagINCHI_Output();
    try {
      InchiStatus ret = InchiStatus.fromInchiRetCode(InchiLibrary.GetINCHI(nativeInput, nativeOutput));
      return new InchiOutput(nativeOutput, ret);
    }
    finally {
      InchiLibrary.FreeINCHI(nativeOutput);
    }
  }
  
  private tagINCHI_Input inputToNative(InchiInput inchiInput, InchiOptions options) {
    try {
      List<InchiAtom> atoms = inchiInput.getAtoms();
      int atomCount = atoms.size();
      if (atomCount > Short.MAX_VALUE) {
        throw new IllegalStateException("InChI is limited to 32767 atoms, input contained " + atomCount + " atoms");
      }
      List<InchiBond> bonds = inchiInput.getBonds();
      Map<InchiAtom, List<InchiBond>> atomToBonds = new HashMap<>();
      Map<InchiAtom, Short> atomToIdx = new HashMap<>();
      for (short i = 0; i < atomCount; i++) {
        InchiAtom atom = atoms.get(i);
        atomToBonds.put(atom, new ArrayList<InchiBond>());
        atomToIdx.put(atom, i);
      }
      for (InchiBond inchiBond : bonds) {
        List<InchiBond> bondsToAtom = atomToBonds.get(inchiBond.getStart());
        if (bondsToAtom == null) {
          throw new IllegalStateException("Bond referenced an atom that was not part of the InchiInput");
        }
        bondsToAtom.add(inchiBond);
      }

      tagInchiAtom nativeAtomReference = new tagInchiAtom();
      tagInchiAtom[] nativeAtoms = new tagInchiAtom[atomCount];
      nativeAtomReference.toArray(nativeAtoms);
      for (int i = 0; i < atomCount; i++) {
        InchiAtom atom = atoms.get(i);
        tagInchiAtom nativeAtom = nativeAtoms[i];
        List<InchiBond> bondsForAtom = atomToBonds.get(atom);
        int bondCount = bondsForAtom.size();
        if (bondCount > 20) {
          throw new IllegalArgumentException("Atom had more 20 bonds");
        }
        for (int j = 0; j < bondCount; j++) {
          InchiBond bond = bondsForAtom.get(j);
          nativeAtom.neighbor[j] = atomToIdx.get(bond.getOther(atom));
          nativeAtom.bond_type[j] = bond.getType().getCode();
          //TODO nativeAtom.bond_stereo
        }

        nativeAtom.x = atom.getX();
        nativeAtom.y = atom.getY();
        nativeAtom.z = atom.getZ();
        
        byte[] el = atom.getElName().getBytes("UTF-8");
        if (el.length > 5) {
          throw new IllegalArgumentException("Element name was too long: " + atom.getElName());
        }
        for (int j = 0; j < el.length; j++) {
          nativeAtom.elname[j] = el[j];
        }      
        nativeAtom.num_bonds = (short) bondsForAtom.size();
        nativeAtom.num_iso_H[0] = (byte) atom.getImplicitHydrogen();//The other positions in this array can be used for implicit protium/deuterium/tritium
        nativeAtom.isotopic_mass = (short) atom.getIsotopicMass();
        nativeAtom.radical = (byte) atom.getRadical().getCode();
        nativeAtom.charge = (byte) atom.getCharge();
      }
      
      List<InchiStereo> stereos = inchiInput.getStereos();
      int stereoCount = stereos.size();
      if (stereoCount > Short.MAX_VALUE) {
        throw new IllegalArgumentException("Too many stereochemistry elements in input");
      }
      
      tagINCHIStereo0D nativeStereoReference = new tagINCHIStereo0D();
      if (stereoCount > 0) {
        tagINCHIStereo0D[] nativeStereos = new tagINCHIStereo0D[stereoCount];
        nativeStereoReference.toArray(nativeStereos);
        for (int i = 0; i < stereoCount; i++) {
          InchiStereo stereo = stereos.get(i);
          tagINCHIStereo0D nativeStereo = nativeStereos[i];
          InchiAtom[] stereoAtoms = stereo.getAtoms();
          for (int j = 0; j < 4; j++) {
            nativeStereo.neighbor[j] = atomToIdx.get(stereoAtoms[j]);
          }
          nativeStereo.central_atom = stereo.getCentralAtom() != null ? atomToIdx.get(stereo.getCentralAtom()) : InchiLibrary.NO_ATOM;
          nativeStereo.type = stereo.getType().getCode();
          nativeStereo.parity = stereo.getParity().getCode();
        }
      }
      return new tagINCHI_Input(nativeAtomReference, nativeStereoReference, options.toString(), (short) atomCount, (short) stereoCount);
    }
    catch (UnsupportedEncodingException e) {
      //Broken VM
      throw new RuntimeException(e);
    }
  }

  public InchiOutput molToInchi(String molText) {
    return molToInchi(molText, new InchiOptionsBuilder().build());
  }
  
  public InchiOutput molToInchi(String molText, InchiOptions options) {
    tagINCHI_Output nativeOutput = new tagINCHI_Output();
    try {
      InchiStatus ret = InchiStatus.fromInchiFromMolRetCode(InchiLibrary.MakeINCHIFromMolfileText(molText, options.toString(), nativeOutput));
      return new InchiOutput(nativeOutput, ret);
    }
    finally {
      InchiLibrary.FreeINCHI(nativeOutput);
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
