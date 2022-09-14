/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
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
package io.github.dan2097.jnarinchi;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;


public class JnaRinchi 
{
	private static final String platform;
	private static final Throwable libraryLoadingError;
	static {
		Throwable t = null;
		String p = null;
		try {
			p = Platform.RESOURCE_PREFIX;
			RinchiLibrary.JNA_NATIVE_LIB.getName();
		}
		catch (Throwable e) { 
			t = e;
		}
		platform = p;
		libraryLoadingError = t;
	}

	//Taken from native RInChI C++ code
	private static final String RINCHI_DECOMPOSE_LINE_SEPARATOR = "\n"; 
	private static final String RINCHI_DECOMPOSE_DIRECTION_SHORT_DESIGNATION = "D";
	private static final int ERROR_CODE_DECOMPOSE_FROM_LINES = -1;

	
	public static RinchiOutput toRinchi(RinchiInput rInp) {
		return toRinchi(rInp, RinchiOptions.DEFAULT_OPTIONS);
	}
	
	public static RinchiOutput toRinchi(RinchiInput rInp, RinchiOptions options) {
		//Converting RinchiInput to RXN/RDFile
		FileTextUtils ftUtils = new FileTextUtils(); 
		ftUtils.setFormat(ReactionFileFormat.RD);
		String fileText = ftUtils.rinchiInputToFileText(rInp);
		if (!ftUtils.getErrors().isEmpty()) {
			return new RinchiOutput("", "", RinchiStatus.ERROR, -1, 
					"Unable to convert RinchiInput to RDFile.\n" + ftUtils.getAllErrors());
		}
		return fileTextToRinchi(fileText, options);
	}
	
	public static RinchiInputFromRinchiOutput getRinchiInputFromRinchi(String rinchi, String auxInfo) {
		return getRinchiInputFromRinchi(rinchi, auxInfo, false);
	}

	public static RinchiInputFromRinchiOutput getRinchiInputFromRinchi(String rinchi, String auxInfo, 
			boolean guessTetrahedralChiralityFromBondsInfo) {
		FileTextOutput ftOut = rinchiToFileText(rinchi, auxInfo, ReactionFileFormat.RD);
		if (ftOut.getStatus() != FileTextStatus.SUCCESS) 
			return new RinchiInputFromRinchiOutput(null, RinchiStatus.ERROR, -1, ftOut.getErrorMessage());
		
		//Converting RXN/RDFile to RinchiInput 
		FileTextUtils ftUtils = new FileTextUtils();
		ftUtils.setGuessTetrahedralChiralityFromBondsInfo(guessTetrahedralChiralityFromBondsInfo);
		ftUtils.setFormat(ReactionFileFormat.RD);
		RinchiInput rInp = ftUtils.fileTextToRinchiInput(ftOut.getReactionFileText());
		if (rInp == null) 
			return new RinchiInputFromRinchiOutput(null, RinchiStatus.ERROR, -1, ftUtils.getAllErrors());
		
		return new RinchiInputFromRinchiOutput(rInp, RinchiStatus.SUCCESS, 0, "");
	}
	
	public static RinchiOutput fileTextToRinchi(String reactFileText) {
		return fileTextToRinchi(ReactionFileFormat.AUTO, reactFileText, RinchiOptions.DEFAULT_OPTIONS);
	}

	public static RinchiOutput fileTextToRinchi(String reactFileText, RinchiOptions options) {
		return fileTextToRinchi(ReactionFileFormat.AUTO, reactFileText, options);
	}

	public static RinchiOutput fileTextToRinchi(ReactionFileFormat fileFormat, String reactFileText, RinchiOptions options) {
		checkLibrary();

		PointerByReference out_rinchi_string_p = new PointerByReference();
		PointerByReference out_rinchi_auxinfo_p = new PointerByReference();
		
		boolean forceEq = options.getFlags().contains(RinchiFlag.ForceEquilibrium);
		int errCode = RinchiLibrary.rinchilib_rinchi_from_file_text(fileFormat.toString(), reactFileText, 
				forceEq, out_rinchi_string_p, out_rinchi_auxinfo_p);        

		if (errCode != 0)
		{
			String errMsg = RinchiLibrary.rinchilib_latest_err_msg();
			return new RinchiOutput("", "", RinchiStatus.ERROR, errCode, errMsg);
		}      

		Pointer p = out_rinchi_string_p.getValue();
		String rinchi = p.getString(0);
		p = out_rinchi_auxinfo_p.getValue();
		String auxInfo = p.getString(0);        
		return new RinchiOutput(rinchi, auxInfo, RinchiStatus.SUCCESS, 0, "");
	}

	public static RinchiKeyOutput fileTextToRinchiKey(String reactFileText, RinchiKeyType keyType) {
		return fileTextToRinchiKey(ReactionFileFormat.AUTO, reactFileText, keyType, RinchiOptions.DEFAULT_OPTIONS);
	}

	public static RinchiKeyOutput fileTextToRinchiKey(String reactFileText, RinchiKeyType keyType, RinchiOptions options) {
		return fileTextToRinchiKey(ReactionFileFormat.AUTO, reactFileText, keyType, options);
	}

	public static RinchiKeyOutput fileTextToRinchiKey(ReactionFileFormat fileFormat, String reactFileText, RinchiKeyType keyType, RinchiOptions options) {
		checkLibrary();

		PointerByReference out_rinchi_key = new PointerByReference();
		boolean forceEq = options.getFlags().contains(RinchiFlag.ForceEquilibrium);
		int errCode = RinchiLibrary.rinchilib_rinchikey_from_file_text(fileFormat.toString(), reactFileText, 
				keyType.getShortDeignation(), forceEq, out_rinchi_key);
		if (errCode != 0)
		{  
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			return new RinchiKeyOutput("", keyType, RinchiKeyStatus.ERROR, errCode, err);
		}  

		Pointer p = out_rinchi_key.getValue();
		String rinchi_key = p.getString(0);
		return new RinchiKeyOutput(rinchi_key, keyType, RinchiKeyStatus.SUCCESS, 0, "");
	}


	public static FileTextOutput rinchiToFileText(String rinchi, String auxInfo, ReactionFileFormat fileFormat) {
		checkLibrary();

		PointerByReference out_file_text_p = new PointerByReference();
		int errCode = RinchiLibrary.rinchilib_file_text_from_rinchi(rinchi, auxInfo, fileFormat.toString(), out_file_text_p);
		if (errCode != 0)
		{  
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			return new FileTextOutput("", fileFormat, FileTextStatus.ERROR, errCode, err);
		}  

		Pointer p = out_file_text_p.getValue();
		String reactFileText = p.getString(0);
		return new FileTextOutput(reactFileText, fileFormat, FileTextStatus.SUCCESS, 0, "");
	}


	public static RinchiKeyOutput rinchiToRinchiKey(RinchiKeyType keyType, String rinchi) {
		checkLibrary();

		PointerByReference out_rinchi_key = new PointerByReference();        
		int errCode = RinchiLibrary.rinchilib_rinchikey_from_rinchi(rinchi, keyType.getShortDeignation(), out_rinchi_key);
		if (errCode != 0)
		{  
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			return new RinchiKeyOutput("", keyType, RinchiKeyStatus.ERROR, errCode, err);
		}      

		Pointer p = out_rinchi_key.getValue();
		String rinchi_key = p.getString(0);
		return new RinchiKeyOutput(rinchi_key, keyType, RinchiKeyStatus.SUCCESS, 0, "");
	}
	
	public static RinchiDecompositionOutput decomposeRinchi(String rinchi) {
		return decomposeRinchi(rinchi, "");
	}


	public static RinchiDecompositionOutput decomposeRinchi(String rinchi, String auxInfo) {
		PointerByReference out_inchis_text_p = new PointerByReference();
		int errCode = RinchiLibrary.rinchilib_inchis_from_rinchi(rinchi, auxInfo, out_inchis_text_p);

		if (errCode != 0)
		{	
			String err = RinchiLibrary.rinchilib_latest_err_msg();
			return new RinchiDecompositionOutput(ReactionDirection.FORWARD, null, null, null, 
					RinchiDecompositionStatus.ERROR, errCode, err);
		}

		Pointer p = out_inchis_text_p.getValue();
		String s = p.getString(0);
		RinchiDecompositionOutput rdo = parseNativeOutInchisText(s);
		return rdo;
	}

		
	private static RinchiDecompositionOutput parseNativeOutInchisText(String outText) {
		/*
		 * Output from RInChI native library, function rinchilib_inchis_from_rinchi(), 
		 * is a text string with multiple lines in the following format:
		 * 
		 * D:<direction>
		 * N:<n1>,<n2>,<n3>
		 * R:<inchi>
		 * R:<auxInfo>
		 * ...
		 * P:<inchi>
		 * p:<auxInfo>
		 * ...
		 * A:<inchi>
		 * A:<auxInfo>
		 * ...
		 */

		String lines[] = outText.split(RINCHI_DECOMPOSE_LINE_SEPARATOR);
		StringBuffer errorBuffer = new StringBuffer();
		
		//Check number of lines and determine the number of components
		int nLines = lines.length;
		int nComponents = -1;
		if ( (nLines < 2) || (nLines % 2) != 0) {
			errorBuffer.append("Incorrect number of lines. Expected even number\n");
		}	
		else
			nComponents = (nLines - 2) / 2;
		
		String[] inchis = new String[nComponents]; 
		String[] auxInfos = new String[nComponents];
		ReactionComponentRole[] roles = new ReactionComponentRole[nComponents]; 
				
		//Handle reaction direction from first line
		ReactionDirection direction = null;
		if (nLines > 0) {
			if (lines[0].startsWith(RINCHI_DECOMPOSE_DIRECTION_SHORT_DESIGNATION + ":") || lines[0].length() < 3) {
				String dirStr = lines[0].substring(2);
				direction = ReactionDirection.getDirectionFromShortDesignation(dirStr);
				if (direction == null)
					errorBuffer.append("Incorrect deirection definition in the first line: " + lines[0] + "\n");
			}
			else
				errorBuffer.append("Incorrect first line: has to start with " 
						+ RINCHI_DECOMPOSE_DIRECTION_SHORT_DESIGNATION + ":" + "\n");
		}
		else
			errorBuffer.append("No lines avalable!\n");
		
		//Iterate all reaction components
		if (nComponents > 0)
			for (int i = 0; i < nComponents; i++) {
				String rinchiLine = lines[2+2*i];
				String auxInfoLine = lines[2+2*i+1];
				
				boolean flagRinciLineOK = false;
				ReactionComponentRole role = null;
				if (rinchiLine.length() < 2)
					errorBuffer.append("Incorrect RInChI component line: " + rinchiLine + "\n");
				else {
					role = ReactionComponentRole.getRoleFromShortDesignation(rinchiLine.substring(0,1));
					if (role == null) 
						errorBuffer.append("Incorrect RInChI component line: incorrect role: " + rinchiLine + "\n");
					else {
						inchis[i] = rinchiLine.substring(2);
						flagRinciLineOK = true;
					}	
				}
				
				if (flagRinciLineOK) {
					if (auxInfoLine.length() < 2)
						errorBuffer.append("Incorrect AuxInfo component line: " + auxInfoLine + "\n");
					else {
						ReactionComponentRole role2 = ReactionComponentRole.getRoleFromShortDesignation(auxInfoLine.substring(0,1));
						if (role2 == null || (role != role2) )
							errorBuffer.append("Incorrect AuxInfo component line: incorrect role: " + auxInfoLine + "\n");
						else {
							auxInfos[i] = auxInfoLine.substring(2);
							roles[i] = role;
						}	
					}
				}
			}
				
		String err = errorBuffer.toString();
		if (err.isEmpty())
			return new RinchiDecompositionOutput(direction, inchis, auxInfos, roles, 
					RinchiDecompositionStatus.SUCCESS, 0, "");
		else {
			//Generally this should never happen. Otherwise, it is a bug in RInChI native C++ code
			return new RinchiDecompositionOutput(direction, null, null, null, 
					RinchiDecompositionStatus.ERROR, ERROR_CODE_DECOMPOSE_FROM_LINES, err);
		}	
	}


	/**
	 * Returns the version of the wrapped RInChI C library
	 * @return Version number String
	 */
	public static String getRinchiLibraryVersion() {
		try(InputStream is = JnaRinchi.class.getResourceAsStream("jnarinchi_build.props")) {
			Properties props = new Properties();
			props.load(is);
			return props.getProperty("rinchi_version");
		}
		catch (Exception e) {
			return null;
		}
	}

	/**
	 * Returns the version of the JNA-RInChI Java library
	 * @return Version number String
	 */
	public static String getJnaRinchiVersion() {
		try(InputStream is = JnaRinchi.class.getResourceAsStream("jnarinchi_build.props")) {
			Properties props = new Properties();
			props.load(is);
			return props.getProperty("jnarinchi_version");
		}
		catch (Exception e) {
			return null;
		}
	}

	private static void checkLibrary() {
		if (libraryLoadingError != null) {
			throw new RuntimeException("Error loading RInChI native code. Please check that the binaries for your platform (" + platform + ") have been included on the classpath.", libraryLoadingError);
		}
	}
}
