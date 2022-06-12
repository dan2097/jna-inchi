package io.github.dan2097.jnarinchi;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.PointerByReference;


public class RinchiLibrary implements Library 	
{
	public static final String JNA_LIBRARY_NAME = "librinchi";
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

	public static native int rinchilib_rinchikey_from_rinchi(
			String rinchi_string, 
			String key_type, 
			PointerByReference out_rinchi_key_p);

	public static native int rinchilib_file_text_from_rinchi(
			String rinchi_string,
			String rinchi_auxinfo, 
			String output_format, 
			PointerByReference out_file_text_p);

}
