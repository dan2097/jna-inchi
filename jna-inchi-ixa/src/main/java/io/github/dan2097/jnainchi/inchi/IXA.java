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
package io.github.dan2097.jnainchi.inchi;

import com.sun.jna.Pointer;

/**
 * Based on IxaFunctions, this class generalizes and makes public the InChI
 * Extended API for use in modules that do not need to have any specific details
 * about what exactly a "com.sun.jna.Pointer" is.
 * 
 * It does not provide access to non-IXA calls from InchiLibrary.
 * 
 * In particular, when used in JavaScript (via java2script/SwingJS) in relation
 * to WASM, these "Pointers" will just be integers.
 * 
 * Note that the only valid use of the returned Pointers is as parameters to some
 * other IXA method. So there we turn them back into Pointer Pointers. This has
 * no consequence in JavaScript, since at runtime there is no class checking.
 * (The class checking is assumed correct since the associated Java will have
 * been sufficiently tested already.) The int value will simply be passed on to
 * WASM.
 * 
 * @author Bob Hanson
 *
 */
public class IXA {

	public static final Pointer ATOM_IMPLICIT_H = Pointer.createConstant(-1L);

	public static int ncalls;

	public static Pointer IXA_STATUS_Create() {
		ncalls++;
		return InchiLibrary.IXA_STATUS_Create();
	}

	public static void IXA_STATUS_Clear(Pointer hStatus) {
		ncalls++;
		InchiLibrary.IXA_STATUS_Clear(hStatus);
	}

	public static void IXA_STATUS_Destroy(Pointer hStatus) {
		ncalls++;
		InchiLibrary.IXA_STATUS_Destroy(hStatus);
	}

	public static boolean IXA_STATUS_HasError(Pointer hStatus) {
		ncalls++;
		return InchiLibrary.IXA_STATUS_HasError(hStatus);
	}

	public static boolean IXA_STATUS_HasWarning(Pointer hStatus) {
		ncalls++;
		return InchiLibrary.IXA_STATUS_HasWarning(hStatus);
	}

	public static int IXA_STATUS_GetCount(Pointer hStatus) {
		ncalls++;
		return InchiLibrary.IXA_STATUS_GetCount(hStatus);
	}

	public static int IXA_STATUS_GetSeverity(Pointer hStatus, int vIndex) {
		ncalls++;
		return InchiLibrary.IXA_STATUS_GetSeverity(hStatus, vIndex);
	}

	public static String IXA_STATUS_GetMessage(Pointer hStatus, int vIndex) {
		ncalls++;
		return InchiLibrary.IXA_STATUS_GetMessage(hStatus, vIndex);
	}

	public static Pointer IXA_MOL_Create(Pointer hStatus) {
		ncalls++;
		return InchiLibrary.IXA_MOL_Create(hStatus);
	}

	public static void IXA_MOL_Clear(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		InchiLibrary.IXA_MOL_Clear(hStatus, hMolecule);
	}

	public static void IXA_MOL_Destroy(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		InchiLibrary.IXA_MOL_Destroy(hStatus, hMolecule);
	}

	public static void IXA_MOL_ReadMolfile(Pointer hStatus, Pointer hMolecule, String pBytes) {
		ncalls++;
		InchiLibrary.IXA_MOL_ReadMolfile(hStatus, hMolecule, fromString(pBytes));
	}

	public static void IXA_MOL_ReadInChI(Pointer hStatus, Pointer hMolecule, String pInChI) {
		ncalls++;
		InchiLibrary.IXA_MOL_ReadInChI(hStatus, hMolecule, fromString(pInChI));
	}

	public static void IXA_MOL_SetChiral(Pointer hStatus, Pointer hMolecule, boolean vChiral) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetChiral(hStatus, hMolecule, vChiral);
	}

	public static boolean IXA_MOL_GetChiral(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetChiral(hStatus, hMolecule);
	}

	public static Pointer IXA_MOL_CreateAtom(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		return InchiLibrary.IXA_MOL_CreateAtom(hStatus, hMolecule);
	}

	public static void IXA_MOL_SetAtomElement(Pointer hStatus, Pointer hMolecule, Pointer vAtom, String pElement) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomElement(hStatus, hMolecule, vAtom,
				fromString(pElement));
	}

	public static void IXA_MOL_SetAtomAtomicNumber(Pointer hStatus, Pointer hMolecule, Pointer vAtom, int vAtomicNumber) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomAtomicNumber(hStatus, hMolecule, vAtom,
				vAtomicNumber);
	}

	public static void IXA_MOL_SetAtomMass(Pointer hStatus, Pointer hMolecule, Pointer vAtom, int vMassNumber) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomMass(hStatus, hMolecule, vAtom, vMassNumber);
	}

	public static void IXA_MOL_SetAtomCharge(Pointer hStatus, Pointer hMolecule, Pointer vAtom, int vCharge) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomCharge(hStatus, hMolecule, vAtom, vCharge);
	}

	public static void IXA_MOL_SetAtomRadical(Pointer hStatus, Pointer hMolecule, Pointer vAtom, int vRadical) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomRadical(hStatus, hMolecule, vAtom, vRadical);
	}

	public static void IXA_MOL_SetAtomHydrogens(Pointer hStatus, Pointer hMolecule, Pointer vAtom, int vHydrogenMassNumber,
			int vHydrogenCount) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomHydrogens(hStatus, hMolecule, vAtom,
				vHydrogenMassNumber, vHydrogenCount);
	}

	public static void IXA_MOL_SetAtomX(Pointer hStatus, Pointer hMolecule, Pointer vAtom, double vX) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomX(hStatus, hMolecule, vAtom, vX);
	}

	public static void IXA_MOL_SetAtomY(Pointer hStatus, Pointer hMolecule, Pointer vAtom, double vY) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomY(hStatus, hMolecule, vAtom, vY);
	}

	public static void IXA_MOL_SetAtomZ(Pointer hStatus, Pointer hMolecule, Pointer vAtom, double vZ) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetAtomZ(hStatus, hMolecule, vAtom, vZ);
	}

	public static Pointer IXA_MOL_CreateBond(Pointer hStatus, Pointer hMolecule, Pointer vAtom1, Pointer vAtom2) {
		ncalls++;
		return InchiLibrary.IXA_MOL_CreateBond(hStatus, hMolecule, vAtom1,
				vAtom2);
	}

	public static void IXA_MOL_SetBondType(Pointer hStatus, Pointer hMolecule, Pointer vBond, int vType) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetBondType(hStatus, hMolecule, vBond, vType);
	}

	public static void IXA_MOL_SetBondWedge(Pointer hStatus, Pointer hMolecule, Pointer vBond, Pointer vRefAtom,
			int vDirection) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetBondWedge(hStatus, hMolecule, vBond, vRefAtom,
				vDirection);
	}

	public static void IXA_MOL_SetDblBondConfig(Pointer hStatus, Pointer hMolecule, Pointer vBond, int vConfig) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetDblBondConfig(hStatus, hMolecule, vBond, vConfig);
	}

	public static Pointer IXA_MOL_CreateStereoTetrahedron(Pointer hStatus, Pointer hMolecule, Pointer vCentralAtom,
			Pointer vVertex1, Pointer vVertex2, Pointer vVertex3, Pointer vVertex4) {
		ncalls++;
		return InchiLibrary.IXA_MOL_CreateStereoTetrahedron(hStatus, hMolecule,
				vCentralAtom, vVertex1, vVertex2, vVertex3, vVertex4);
	}

	public static Pointer IXA_MOL_CreateStereoRectangle(Pointer hStatus, Pointer hMolecule, Pointer vCentralBond,
			Pointer vVertex1, Pointer vVertex2, Pointer vVertex3, Pointer vVertex4) {
		ncalls++;
		return InchiLibrary.IXA_MOL_CreateStereoRectangle(hStatus, hMolecule,
				vCentralBond, vVertex1, vVertex2, vVertex3, vVertex4);
	}

	public static Pointer IXA_MOL_CreateStereoAntiRectangle(Pointer hStatus, Pointer hMolecule, Pointer vCentralAtom,
			Pointer vVertex1, Pointer vVertex2, Pointer vVertex3, Pointer vVertex4) {
		ncalls++;
		return InchiLibrary.IXA_MOL_CreateStereoAntiRectangle(hStatus, hMolecule,
				vCentralAtom, vVertex1, vVertex2, vVertex3, vVertex4);
	}

	public static void IXA_MOL_SetStereoParity(Pointer hStatus, Pointer hMolecule, Pointer vStereo, int vParity) {
		ncalls++;
		InchiLibrary.IXA_MOL_SetStereoParity(hStatus, hMolecule, vStereo, vParity);
	}

	public static int IXA_MOL_ReserveSpace(Pointer hStatus, Pointer hMolecule, int num_atoms, int num_bonds,
			int num_stereos) {
		ncalls++;
		return InchiLibrary.IXA_MOL_ReserveSpace(hStatus, hMolecule, num_atoms, num_bonds,
				num_stereos);
	}

	// FIXME IXA_MOL_CreatePolymerUnit and IXA_MOL_SetPolymerUnit are missing from
	// Linux build
//  public static IXA_POLYMERUNITID IXA_MOL_CreatePolymerUnit(Pointer hStatus, Pointer hMolecule) {
//    return new IXA_POLYMERUNITID(InchiLibrary.IXA_MOL_CreatePolymerUnit(hStatus, hMolecule));
//  }
//  public static void IXA_MOL_SetPolymerUnit(Pointer hStatus, Pointer hMolecule, IXA_POLYMERUNITID vPunit, int vid, int vtype, int vsubtype, int vconn, int vlabel, int vna, int vnb, DoubleBuffer vxbr1, DoubleBuffer vxbr2, ByteBuffer vsmt, IntBuffer valist, IntBuffer vblist) {
//    InchiLibrary.IXA_MOL_SetPolymerUnit(hStatus, hMolecule, vPunit, vid, vtype, vsubtype, vConn, vlabel, vna, vnb, vxbr1, vxbr2, vsmt, valist, vblist);
//  } 

	public static int IXA_MOL_GetNumAtoms(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetNumAtoms(hStatus, hMolecule);
	}

	public static int IXA_MOL_GetNumBonds(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetNumBonds(hStatus, hMolecule);
	}

	public static Pointer IXA_MOL_GetAtomId(Pointer hStatus, Pointer hMolecule, int vAtomIndex) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomId(hStatus, hMolecule, vAtomIndex);
	}

	public static Pointer IXA_MOL_GetBondId(Pointer hStatus, Pointer hMolecule, int vBondIndex) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondId(hStatus, hMolecule, vBondIndex);
	}

	public static int IXA_MOL_GetAtomIndex(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomIndex(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetBondIndex(Pointer hStatus, Pointer hMolecule, Pointer vBond) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondIndex(hStatus, hMolecule, vBond);
	}

	public static int IXA_MOL_GetAtomNumBonds(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomNumBonds(hStatus, hMolecule, vAtom);
	}
	// FIXME IXA_MOL_GetPolymerUnitId and IXA_MOL_GetPolymerUnitIndex are missing
	// from Linux build
//  
//  public static IXA_POLYMERUNITID IXA_MOL_GetPolymerUnitId(Pointer hStatus, Pointer hMolecule, int vPolymerUnitIndex) {
//    return new IXA_POLYMERUNITID(InchiLibrary.IXA_MOL_GetPolymerUnitId(hStatus, hMolecule, vPolymerUnitIndex));
//  }
//
//  public static int IXA_MOL_GetPolymerUnitIndex(Pointer hStatus, Pointer hMolecule, IXA_POLYMERUNITID vPolymerUnit) {
//    return InchiLibrary.IXA_MOL_GetPolymerUnitIndex(hStatus, hMolecule, vPolymerUnit);
//  }

	public static Pointer IXA_MOL_GetAtomBond(Pointer hStatus, Pointer hMolecule, Pointer vAtom, int vBondIndex) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomBond(hStatus, hMolecule, vAtom, vBondIndex);
	}

	public static Pointer IXA_MOL_GetCommonBond(Pointer hStatus, Pointer hMolecule, Pointer vAtom1, Pointer vAtom2) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetCommonBond(hStatus, hMolecule, vAtom1,
				vAtom2);
	}

	public static Pointer IXA_MOL_GetBondAtom1(Pointer hStatus, Pointer hMolecule, Pointer vBond) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondAtom1(hStatus, hMolecule, vBond);
	}

	public static Pointer IXA_MOL_GetBondAtom2(Pointer hStatus, Pointer hMolecule, Pointer vBond) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondAtom2(hStatus, hMolecule, vBond);
	}

	public static Pointer IXA_MOL_GetBondOtherAtom(Pointer hStatus, Pointer hMolecule, Pointer vBond, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondOtherAtom(hStatus, hMolecule, vBond,
				vAtom);
	}

	public static String IXA_MOL_GetAtomElement(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomElement(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetAtomAtomicNumber(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomAtomicNumber(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetAtomMass(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomMass(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetAtomCharge(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomCharge(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetAtomRadical(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomRadical(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetAtomHydrogens(Pointer hStatus, Pointer hMolecule, Pointer vAtom,
			int vHydrogenMassNumber) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomHydrogens(hStatus, hMolecule, vAtom,
				vHydrogenMassNumber);
	}

	public static double IXA_MOL_GetAtomX(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomX(hStatus, hMolecule, vAtom);
	}

	public static double IXA_MOL_GetAtomY(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomY(hStatus, hMolecule, vAtom);
	}

	public static double IXA_MOL_GetAtomZ(Pointer hStatus, Pointer hMolecule, Pointer vAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetAtomZ(hStatus, hMolecule, vAtom);
	}

	public static int IXA_MOL_GetBondType(Pointer hStatus, Pointer hMolecule, Pointer vBond) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondType(hStatus, hMolecule, vBond);
	}

	public static int IXA_MOL_GetBondWedge(Pointer hStatus, Pointer hMolecule, Pointer vBond, Pointer vRefAtom) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetBondWedge(hStatus, hMolecule, vBond,
				vRefAtom);
	}

	public static int IXA_MOL_GetDblBondConfig(Pointer hStatus, Pointer hMolecule, Pointer vBond) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetDblBondConfig(hStatus, hMolecule, vBond);
	}

	public static int IXA_MOL_GetNumStereos(Pointer hStatus, Pointer hMolecule) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetNumStereos(hStatus, hMolecule);
	}

	public static Pointer IXA_MOL_GetStereoId(Pointer hStatus, Pointer hMolecule, int vStereoIndex) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoId(hStatus, hMolecule, vStereoIndex);
	}

	public static int IXA_MOL_GetStereoIndex(Pointer hStatus, Pointer hMolecule, Pointer vStereo) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoIndex(hStatus, hMolecule, vStereo);
	}

	public static int IXA_MOL_GetStereoTopology(Pointer hStatus, Pointer hMolecule, Pointer vStereo) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoTopology(hStatus, hMolecule, vStereo);
	}

	public static Pointer IXA_MOL_GetStereoCentralAtom(Pointer hStatus, Pointer hMolecule, Pointer vStereo) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoCentralAtom(hStatus, hMolecule, vStereo);
	}

	public static Pointer IXA_MOL_GetStereoCentralBond(Pointer hStatus, Pointer hMolecule, Pointer vStereo) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoCentralBond(hStatus, hMolecule, vStereo);
	}

	public static int IXA_MOL_GetStereoNumVertices(Pointer hStatus, Pointer hMolecule, Pointer vStereo) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoNumVertices(hStatus, hMolecule, vStereo);
	}

	public static Pointer IXA_MOL_GetStereoVertex(Pointer hStatus, Pointer hMolecule, Pointer vStereo, int vVertexIndex) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoVertex(hStatus, hMolecule, vStereo,
				vVertexIndex);
	}

	public static int IXA_MOL_GetStereoParity(Pointer hStatus, Pointer hMolecule, Pointer vStereo) {
		ncalls++;
		return InchiLibrary.IXA_MOL_GetStereoParity(hStatus, hMolecule, vStereo);
	}

	public static Pointer IXA_INCHIBUILDER_Create(Pointer hStatus) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_Create(hStatus);
	}

	public static void IXA_INCHIBUILDER_SetMolecule(Pointer hStatus, Pointer hInChIBuilder, Pointer hMolecule) {
		ncalls++;
		InchiLibrary.IXA_INCHIBUILDER_SetMolecule(hStatus, hInChIBuilder, hMolecule);
	}

	public static String IXA_INCHIBUILDER_GetInChI(Pointer hStatus, Pointer hInChIBuilder) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_GetInChI(hStatus, hInChIBuilder);
	}

	public static String IXA_INCHIBUILDER_GetInChIEx(Pointer hStatus, Pointer hBuilder) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_GetInChIEx(hStatus, hBuilder);
	}

	public static String IXA_INCHIBUILDER_GetAuxInfo(Pointer hStatus, Pointer hInChIBuilder) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_GetAuxInfo(hStatus, hInChIBuilder);
	}

	public static String IXA_INCHIBUILDER_GetLog(Pointer hStatus, Pointer hInChIBuilder) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_GetLog(hStatus, hInChIBuilder);
	}

	public static void IXA_INCHIBUILDER_Destroy(Pointer hStatus, Pointer hInChIBuilder) {
		ncalls++;
		InchiLibrary.IXA_INCHIBUILDER_Destroy(hStatus, hInChIBuilder);
	}

	public static void IXA_INCHIBUILDER_SetOption(Pointer hStatus, Pointer hInChIBuilder, int vOption, boolean vValue) {
		ncalls++;
		InchiLibrary.IXA_INCHIBUILDER_SetOption(hStatus, hInChIBuilder, vOption, vValue);
	}

	public static void IXA_INCHIBUILDER_SetOption_Stereo(Pointer hStatus, Pointer hInChIBuilder, int vValue) {
		ncalls++;
		InchiLibrary.IXA_INCHIBUILDER_SetOption_Stereo(hStatus, hInChIBuilder, vValue);
	}

	public static void IXA_INCHIBUILDER_SetOption_Timeout(Pointer hStatus, Pointer hInChIBuilder, int vValue) {
		ncalls++;
		InchiLibrary.IXA_INCHIBUILDER_SetOption_Timeout(hStatus, hInChIBuilder, vValue);
	}

	public static void IXA_INCHIBUILDER_SetOption_Timeout_MilliSeconds(Pointer hStatus, Pointer hInChIBuilder,
			long vValue) {
		ncalls++;
		InchiLibrary.IXA_INCHIBUILDER_SetOption_Timeout_MilliSeconds(hStatus, hInChIBuilder,
				vValue);
	}

	public static boolean IXA_INCHIBUILDER_CheckOption(Pointer hStatus, Pointer hInChIBuilder, int vOption) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_CheckOption(hStatus, hInChIBuilder, vOption);
	}

	public static boolean IXA_INCHIBUILDER_CheckOption_Stereo(Pointer hStatus, Pointer hInChIBuilder, int vValue) {
		ncalls++;
		return InchiLibrary.IXA_INCHIBUILDER_CheckOption_Stereo(hStatus, hInChIBuilder, vValue);
	}

	// BH a bit of an overkill to use a long for a millisecond timeout!!!
	
	// FIXME IXA_INCHIBUILDER_GetOption_Timeout_MilliSeconds is missing from Linux
	// build
//  public static long IXA_INCHIBUILDER_GetOption_Timeout_MilliSeconds(Pointer hStatus, Pointer hInChIBuilder) {
//    return InchiLibrary.IXA_INCHIBUILDER_GetOption_Timeout_MilliSeconds(hStatus, hInChIBuilder);
//  }

	public static Pointer IXA_INCHIKEYBUILDER_Create(Pointer hStatus) {
		ncalls++;
		return InchiLibrary.IXA_INCHIKEYBUILDER_Create(hStatus);
	}

	public static void IXA_INCHIKEYBUILDER_SetInChI(Pointer hStatus, Pointer hInChIKeyBuilder, String pInChI) {
		ncalls++;
		InchiLibrary.IXA_INCHIKEYBUILDER_SetInChI(hStatus, hInChIKeyBuilder, fromString(pInChI));
	}

	public static String IXA_INCHIKEYBUILDER_GetInChIKey(Pointer hStatus, Pointer hInChIKeyBuilder) {
		ncalls++;
		return InchiLibrary.IXA_INCHIKEYBUILDER_GetInChIKey(hStatus, hInChIKeyBuilder);
	}

	public static void IXA_INCHIKEYBUILDER_Destroy(Pointer hStatus, Pointer hInChIKeyBuilder) {
		ncalls++;
		InchiLibrary.IXA_INCHIKEYBUILDER_Destroy(hStatus, hInChIKeyBuilder);
	}

	private static byte[] fromString(String jstr) {
		/**
		 * WASM does not need to do this conversion.
		 * 
		 * @j2sNative
		 * 
		 * 			return jstr
		 */
		{
			int strLen = jstr.length();
			byte[] cstr = new byte[strLen + 1];
			for (int i = 0; i < strLen; i++) {
				cstr[i] = (byte) jstr.charAt(i);
			}
			cstr[strLen] = '\0';
			return cstr;
		}
	}

}
