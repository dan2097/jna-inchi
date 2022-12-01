/**
 * JNA-RInChI - Library for calling RInChI from Java
 * Copyright © 2022 Nikolay Kochev
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.dan2097.jnarinchi;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.ptr.PointerByReference;

/**
 * JNA Wrapper for library <b>rinchi</b>.
 *
 * @author nick
 */
public class RinchiLibrary implements Library {
    public static final String JNA_LIBRARY_NAME = "rinchi";
    public static final NativeLibrary JNA_NATIVE_LIB = NativeLibrary.getInstance(RinchiLibrary.JNA_LIBRARY_NAME);

    static {
        Native.register(RinchiLibrary.class, RinchiLibrary.JNA_NATIVE_LIB);
    }

    /**
     * Returns the latest error message of the native rinchi library.
     *
     * @return the string of error message
     */
    public static native String rinchilib_latest_err_msg();

    /**
     * Generates RInChI and RAuxInfo from a text file in MDL RXN/RDFile format.
     * <br>
     * Original signature:
     * <code>int rinchilib_rinchi_from_file_text(
     * const char* input_format, const char* in_file_text, bool in_force_equilibrium,
     * const char** out_rinchi_string, const char** out_rinchi_auxinfo)</code><br>
     *
     * @param input_format reaction file format as a string ("RXN", "RD" or "AUTO")
     * @param in_file_text input reaction as a text in MDL RXN/RDFile format
     * @param in_force_equilibrium determines whether to force equilibrium in RInChI generation
     * @param out_rinchi_string_p the result RInChI wrapped by a PointerByReference object
     * @param out_rinchi_auxinfo_p the result RAuxInfo wrapped by a PointerByReference object
     * @return the exit code
     */
    public static native int rinchilib_rinchi_from_file_text(
            String input_format,
            String in_file_text,
            boolean in_force_equilibrium,
            PointerByReference out_rinchi_string_p,
            PointerByReference out_rinchi_auxinfo_p);

    /**
     * Generates RInChIKey from a text file in MDL RXN/RDFile format.
     * <br>
     * Original signature:
     * <code> int rinchilib_rinchikey_from_file_text(const char* input_format, const char* in_file_text,
     * const char* key_type, bool in_force_equilibrium, const char** out_rinchi_key) </code><br>
     *
     * @param input_format reaction file format as a string ("RXN", "RD" or "AUTO")
     * @param in_file_text input reaction as a text in MDL RXN/RDFile format
     * @param key_type RInChIKey type as a string ("L", "S" or "W")
     * @param in_force_equilibrium determines whether to force equilibrium in RInChI generation
     * @param out_rinchi_key_p the result RInChIKey wrapped by a PointerByReference object
     * @return the exit code
     */
    public static native int rinchilib_rinchikey_from_file_text(
            String input_format,
            String in_file_text,
            String key_type,
            boolean in_force_equilibrium,
            PointerByReference out_rinchi_key_p
    );

    /**
     * Converts RInChI and RAuxInfo into a reaction represented as text file in MDL RXN/RDFile format.
     * <br>
     * Original signature:
     * <code> int rinchilib_file_text_from_rinchi(const char* rinchi_string, const char* rinchi_auxinfo,
     * const char* output_format, const char** out_file_text) </code><br>
     *
     * @param rinchi_string RInChI string
     * @param rinchi_auxinfo RAuxInfo  string
     * @param output_format reaction file format as a string ("RXN", "RD" or "AUTO")
     * @param out_file_text_p the result reaction file text wrapped by a PointerByReference object
     * @return the exit code
     */
    public static native int rinchilib_file_text_from_rinchi(
            String rinchi_string,
            String rinchi_auxinfo,
            String output_format,
            PointerByReference out_file_text_p);

    /**
     * Extracts InChIs and AuxInfo for all reaction components from a RInChI and RAuxInfo strings.
     * <br>
     * Original signature:
     * <code> int rinchilib_inchis_from_rinchi(const char* rinchi_string, const char* rinchi_auxinfo,
     * const char** out_inchis_text) </code><br>
     *
     * The result from this function must be parsed as it is a single multi-line string
     * containing reaction direction, inchi, auxinfo and role for every component.
     *
     * @param rinchi_string RInChI string
     * @param rinchi_auxinfo RAuxInfo  string
     * @param out_inchis_text_p output information wrapped by a PointerByReference object
     * @return the exit code
     */
    public static native int rinchilib_inchis_from_rinchi(
            String rinchi_string,
            String rinchi_auxinfo,
            PointerByReference out_inchis_text_p
    );

    /**
     * Generates RInChIKey from a RInChI string.
     * <br>
     * Original signature:
     * <code> int rinchilib_rinchikey_from_rinchi(const char* rinchi_string, const char* key_type,
     * const char** out_rinchi_key) </code><br>
     *
     * @param rinchi_string RInChI string
     * @param key_type RInChIKey type as a string ("L", "S" or "W")
     * @param out_rinchi_key_p the result RInChIKey wrapped by a PointerByReference object
     * @return the exit code
     */
    public static native int rinchilib_rinchikey_from_rinchi(
            String rinchi_string,
            String key_type,
            PointerByReference out_rinchi_key_p);
}
