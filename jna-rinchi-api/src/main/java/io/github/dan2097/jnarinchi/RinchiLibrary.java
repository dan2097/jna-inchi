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

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.PointerByReference;


public class RinchiLibrary implements Library 	
{
	public static final String JNA_LIBRARY_NAME = "rinchi";
	public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(RinchiLibrary.JNA_LIBRARY_NAME);

	
	static {
		Native.register(RinchiLibrary.class, RinchiLibrary.JNA_NATIVE_LIB);
	}
	

	public static native String rinchilib_latest_err_msg();
	

	public static native int rinchilib_rinchi_from_file_text(
			String input_format, 
			String in_file_text, 
			boolean in_force_equilibrium,
			PointerByReference out_rinchi_string_p,
			PointerByReference out_rinchi_auxinfo_p);
	
	public static native int rinchilib_rinchikey_from_file_text(
			String input_format, 
			String in_file_text,
			String key_type,
			boolean in_force_equilibrium,
			PointerByReference out_rinchi_key_p
			);

	public static native int rinchilib_file_text_from_rinchi(
			String rinchi_string,
			String rinchi_auxinfo, 
			String output_format, 
			PointerByReference out_file_text_p);

	public static native int rinchilib_inchis_from_rinchi(
			String rinchi_string,
			String rinchi_auxinfo,
			PointerByReference out_inchis_text_p
			);
	
	public static native int rinchilib_rinchikey_from_rinchi(
			String rinchi_string, 
			String key_type, 
			PointerByReference out_rinchi_key_p);

	
}
