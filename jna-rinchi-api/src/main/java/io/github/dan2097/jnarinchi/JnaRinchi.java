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
import java.util.Properties;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import io.github.dan2097.jnarinchi.RinchiOptions.ReactionFileFormat;



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
	
	
	public static RinchiOutput fileTextToRinchi(String reactFileText) {
		return fileTextToRinchi(ReactionFileFormat.AUTO, reactFileText, new RinchiOptions());
	}
	
	public static RinchiOutput fileTextToRinchi(String reactFileText, RinchiOptions options) {
		return fileTextToRinchi(ReactionFileFormat.AUTO, reactFileText, options);
	}
	
	public static RinchiOutput fileTextToRinchi(ReactionFileFormat fileFormat, String reactFileText, RinchiOptions options) {
		checkLibrary();
		
		PointerByReference out_rinchi_string_p = new PointerByReference();
        PointerByReference out_rinchi_auxinfo_p = new PointerByReference();
        
        int errCode = RinchiLibrary.rinchilib_rinchi_from_file_text(fileFormat.toString(), reactFileText, 
        		options.isForceEquilibrium(), out_rinchi_string_p, out_rinchi_auxinfo_p);        
        
        if (errCode != 0)
        {
        	String errMsg = RinchiLibrary.rinchilib_latest_err_msg();
        	return new RinchiOutput("", "", RinchiStatus.ERROR, errCode, errMsg);
        }      
                
        Pointer p = out_rinchi_string_p.getValue();
        String rinchi = p.getString(0);
        p = out_rinchi_auxinfo_p.getValue();
        String auxInfo = p.getString(0);
        
        return new RinchiOutput(rinchi, auxInfo, RinchiStatus.ERROR, 0, "");
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
