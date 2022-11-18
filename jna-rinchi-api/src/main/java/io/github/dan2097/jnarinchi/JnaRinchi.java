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

import java.io.InputStream;
import java.util.Properties;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import io.github.dan2097.jnarinchi.cheminfo.MDLReactionReader;
import io.github.dan2097.jnarinchi.cheminfo.MDLReactionWriter;

/**
 * Provides access to the native RInChI library functionality via Java code.
 * This class wraps around the native RInChI library using the JNA native interface
 * implemented in {@link RinchiLibrary}.
 *
 * @author Nikolay Kochev
 * @see RinchiLibrary
 */
public class JnaRinchi {
    private static final String PROPERTY_KEY_RINCHI_VERSION = "rinchi_version";
    private static final String PROPERTY_KEY_JNARINCHI_VERSION = "jnarinchi_version";
    private static final String PROPERTY_FILE_NAME = "jnarinchi_build.props";

    private static final String platform;
    private static final Throwable libraryLoadingError;

    static {
        Throwable t = null;
        String p = null;
        try {
            p = Platform.RESOURCE_PREFIX;
            RinchiLibrary.JNA_NATIVE_LIB.getName();
        } catch (Throwable e) {
            t = e;
        }
        platform = p;
        libraryLoadingError = t;
    }

    /** The RInChI C++ library does not consider platform-dependent line endings, but only uses '\n'. */
    private static final String RINCHI_DECOMPOSE_LINE_SEPARATOR = "\n";
    private static final String RINCHI_DECOMPOSE_DIRECTION_SHORT_DESIGNATION = "D";
    private static final int ERROR_CODE_DECOMPOSE_FROM_LINES = -1;

    /**
     * Converts a reaction represented as a RinchiInput object into RInChI and RAuxInfo.
     * The output object of type RinchiOutput contains the generation status, error messages if any,
     * RInChI and RAuxInfo.
     * <br>
     * Default RInChI generation options are applied.
     *
     * @param rinchiInput input reaction as RinchiInput object
     * @return result RinchiOutput object
     * @see #toRinchi(RinchiInput, RinchiOptions)
     */
    public static RinchiOutput toRinchi(RinchiInput rinchiInput) {
        return toRinchi(rinchiInput, RinchiOptions.DEFAULT_OPTIONS);
    }

    /**
     * Converts a reaction represented as a RinchiInput object into RInChI and RAuxInfo.
     * The output object of type RinchiOutput contains the generation status, error messages if any,
     * RInChI and RAuxInfo.
     * <br>
     * RInChI generation is customized via {@link RinchiOptions}.
     *
     * @param rinchiInput input reaction as RinchiInput object
     * @param options RInChI generation options
     * @return result RinchiOutput object
     * @see #toRinchi(RinchiInput)
     */
    public static RinchiOutput toRinchi(RinchiInput rinchiInput, RinchiOptions options) {
        //Converting RinchiInput to RXN/RDFile
        MDLReactionWriter mdlWriter = new MDLReactionWriter();
        mdlWriter.setFormat(ReactionFileFormat.RD);
        String fileText = mdlWriter.rinchiInputToFileText(rinchiInput);

        if (!mdlWriter.getErrors().isEmpty()) {
            return new RinchiOutput("", "", Status.ERROR, -1,
                    "Unable to convert RinchiInput to RDFile.\n" + mdlWriter.getAllErrors());
        }

        return fileTextToRinchi(fileText, options);
    }

    /**
     * Converts a reaction represented by RInChI and RAuxInfo into a {@link RinchiInputFromRinchiOutput} object.
     * The output is an object of type RinchiInputFromRinchiOutput and
     * contains the generation status, error messages if any and RinchiInput
     * RInChI and RAuxInfo must not be <code>null</code>.
     * <br>
     * If RAuxInfo is not present an empty string (i.e., "") should be given as a value for <code>auxInfo</code>.
     * <p>
     * The native RInChI library stores chirality information only via 2D or 3D coordinates (if present in RAuxInfo).
     * Stereo parity of the chiral atoms is not set within the output MDL RXN/RDFile text.
     * That is way the resulting RinchiInput objects do not contain stereo elements for tetrahedral chirality.
     * This information needs to be extracted from the atom coordinates via a third party software.
     * </p>
     * @param rinchi input RInChi string
     * @param auxInfo input RAuxInfo string
     * @return result RinchiInputFromRinchiOutput object
     * @see #getRinchiInputFromRinchi(String, String, boolean)
     */
    public static RinchiInputFromRinchiOutput getRinchiInputFromRinchi(String rinchi, String auxInfo) {
        return getRinchiInputFromRinchi(rinchi, auxInfo, false);
    }

    /**
     * Converts a reaction represented by RInChI and RAuxInfo into a {@link RinchiInputFromRinchiOutput} object.
     * The output is an object of type RinchiInputFromRinchiOutput and
     * contains the generation status, error messages if any and RinchiInput
     * RInChI and RAuxInfo must not be <code>null</code>.
     * <br>
     * If RAuxInfo is not present an empty string (i.e., "") should be given as a value for <code>auxInfo</code>.
     * <p>
     * The native RInChI library stores chirality information only via 2D or 3D coordinates (if present in RAuxInfo).
     * Stereo parity of the chiral atoms is not set within the output MDL RXN/RDFile text.
     * That is way the resulting RinchiInput objects do not contain stereo elements for tetrahedral chirality.
     * This information needs to be extracted from the atom coordinates via a third party software.
     * </p>
     * <p>
     * If the value of <code>guessTetrahedralChiralityFromBondsInfo</code> is <code>true</code> stereo elements with
     * UNDEFINED parity could be guessed from the MDL bond line attributes. This functionality can only be used to
     * indicate the presence of chiral atoms but the parity (exact chirality) must still be calculated from the coordinates.
     * </p>
     * @param rinchi input RInChi string
     * @param auxInfo input RAuxInfo string
     * @param guessTetrahedralChiralityFromBondsInfo flag for guessing chiral stereo elements from bond attributes
     * @return result RinchiInputFromRinchiOutput object
     * @see #getRinchiInputFromRinchi(String, String)
     */
    public static RinchiInputFromRinchiOutput getRinchiInputFromRinchi(String rinchi, String auxInfo,
                                                                       boolean guessTetrahedralChiralityFromBondsInfo) {
        FileTextOutput ftOut = rinchiToFileText(rinchi, auxInfo, ReactionFileFormat.RD);
        if (ftOut.getStatus() != Status.SUCCESS)
            return new RinchiInputFromRinchiOutput(null, Status.ERROR, -1, ftOut.getErrorMessage());

        //Converting RXN/RDFile to RinchiInput
        MDLReactionReader mdlReader = new MDLReactionReader();
        mdlReader.setGuessTetrahedralChiralityFromBondsInfo(guessTetrahedralChiralityFromBondsInfo);
        mdlReader.setFormat(ReactionFileFormat.RD);
        RinchiInput rInp = mdlReader.fileTextToRinchiInput(ftOut.getReactionFileText());

        if (rInp == null)
            return new RinchiInputFromRinchiOutput(null, Status.ERROR, -1, mdlReader.getAllErrors());

        return new RinchiInputFromRinchiOutput(rInp, Status.SUCCESS, 0, "");
    }

    /**
     * Converts a reaction represented in MDL RXN or RDFile format into RInChI and RAuxInfo.
     * The output object of type RinchiOutput contains the generation status, error messages if any,
     * RInChI and RAuxInfo.
     * <br>
     * The file format is automatically recognized.
     * Default generation options are applied.
     *
     * @param reactionTextFile reaction represented in RXN or RDFile format
     * @return result RinchiOutput object
     * @see #fileTextToRinchi(String, RinchiOptions)
     * @see #fileTextToRinchi(String, RinchiOptions, ReactionFileFormat)
     */
    public static RinchiOutput fileTextToRinchi(String reactionTextFile) {
        return fileTextToRinchi(reactionTextFile, RinchiOptions.DEFAULT_OPTIONS, ReactionFileFormat.AUTO);
    }

    /**
     * Converts a reaction represented in MDL RXN or RDFile format into RInChI and RAuxInfo.
     * The output object of type RinchiOutput contains the generation status, error messages if any,
     * RInChI and RAuxInfo.
     * <br>
     * The file format is automatically recognized.
     * RInChI generation is customized via RinchiOptions.
     *
     * @param reactionTextFile reaction represented in RXN or RDFile format
     * @param options RInChI generation options
     * @return result RinchiOutput object
     * @see #fileTextToRinchi(String)
     * @see #fileTextToRinchi(String, RinchiOptions, ReactionFileFormat)
     */
    public static RinchiOutput fileTextToRinchi(String reactionTextFile, RinchiOptions options) {
        return fileTextToRinchi(reactionTextFile, options, ReactionFileFormat.AUTO);
    }

    /**
     * Converts a reaction represented in MDL RXN or RDFile format into RInChI and RAuxInfo.
     * The output object of type RinchiOutput contains the generation status, error messages if any,
     * RInChI and RAuxInfo.
     * <br>
     * The file format is configured with the argument <code>fileFormat</code>.
     * RInChI generation is customized via RinchiOptions.
     *
     * @param reactionFileText reaction represented in RXN or RDFile format
     * @param options RInChI generation options
     * @param fileFormat the MDL file format for reaction representation: {@link ReactionFileFormat#RXN}, {@link ReactionFileFormat#RD} or {@link ReactionFileFormat#AUTO}
     * @return result RinchiOutput object
     * @see #fileTextToRinchi(String)
     * @see #fileTextToRinchi(String, RinchiOptions)
     */
    public static RinchiOutput fileTextToRinchi(String reactionFileText, RinchiOptions options, ReactionFileFormat fileFormat) {
        checkLibrary();
        requireNonNull(reactionFileText, "reactionFileText");
        requireNonNull(options, "options");
        requireNonNull(fileFormat, "fileFormat");

        PointerByReference out_rinchi_string_p = new PointerByReference();
        PointerByReference out_rinchi_auxinfo_p = new PointerByReference();

        boolean forceEq = options.getFlags().contains(RinchiFlag.ForceEquilibrium);
        int errCode = RinchiLibrary.rinchilib_rinchi_from_file_text(fileFormat.toString(), reactionFileText,
                forceEq, out_rinchi_string_p, out_rinchi_auxinfo_p);

        if (errCode != 0) {
            String errMsg = RinchiLibrary.rinchilib_latest_err_msg();
            return new RinchiOutput("", "", Status.ERROR, errCode, errMsg);
        }

        Pointer p = out_rinchi_string_p.getValue();
        String rinchi = p.getString(0);
        p = out_rinchi_auxinfo_p.getValue();
        String auxInfo = p.getString(0);

        return new RinchiOutput(rinchi, auxInfo, Status.SUCCESS, 0, "");
    }

    /**
     * Converts a reaction represented in MDL RXN or RDFile format into RInChIKey.
     * The output object of type RinchiKeyOutput contains the generation status, error messages if any,
     * and RInChIKey.
     * <br>
     * RInChIKey could be of type: {@link RinchiKeyType#LONG}, {@link RinchiKeyType#SHORT} or {@link RinchiKeyType#WEB}.
     * The file format is automatically recognized.
     * Default generation options are applied.
     *
     * @param reactionTextFile reaction represented in RXN or RDFile format
     * @param keyType RInChI-Key type
     * @return result RinchiKeyOutput object
     * @see #fileTextToRinchiKey(String, RinchiKeyType, RinchiOptions)
     * @see #fileTextToRinchiKey(String, RinchiKeyType, RinchiOptions, ReactionFileFormat)
     */
    public static RinchiKeyOutput fileTextToRinchiKey(String reactionTextFile, RinchiKeyType keyType) {
        return fileTextToRinchiKey(reactionTextFile, keyType, RinchiOptions.DEFAULT_OPTIONS, ReactionFileFormat.AUTO);
    }

    /**
     * Converts a reaction represented in MDL RXN or RDFile format into RInChIKey.
     * The output object of type RinchiKeyOutput contains the generation status, error messages if any,
     * and RInChIKey.
     * <br>
     * RInChIKey could be of type: {@link RinchiKeyType#LONG}, {@link RinchiKeyType#SHORT} or {@link RinchiKeyType#WEB}.
     * The file format is automatically recognized.
     * RInChIKey generation is customized via RinchiOptions.
     *
     * @param reactionFileText reaction represented in RXN or RDFile format
     * @param keyType RInChI-Key type
     * @param options RInChI/RInChIKey generation options
     * @return result RinchiKeyOutput object
     * @see #fileTextToRinchiKey(String, RinchiKeyType)
     * @see #fileTextToRinchiKey(String, RinchiKeyType, RinchiOptions, ReactionFileFormat)
     */
    public static RinchiKeyOutput fileTextToRinchiKey(String reactionFileText, RinchiKeyType keyType, RinchiOptions options) {
        return fileTextToRinchiKey(reactionFileText, keyType, options, ReactionFileFormat.AUTO);
    }

    /**
     * Converts a reaction represented in MDL RXN or RDFile format into RInChIKey.
     * The output object of type RinchiKeyOutput contains the generation status, error messages if any,
     * and RInChIKey.
     * <br>
     * RInChIKey could be of type: {@link RinchiKeyType#LONG}, {@link RinchiKeyType#SHORT} or {@link RinchiKeyType#WEB}.
     * The file format is configured with the argument <code>fileFormat</code>.
     * RInChIKey generation is customized via RinchiOptions.
     *
     * @param reactionFileText reaction represented in RXN or RDFile format
     * @param keyType          RInChI-Key type
     * @param options          RInChI/RInChIKey generation options
     * @param fileFormat       the MDL file format for reaction representation: {@link ReactionFileFormat#RXN}, {@link ReactionFileFormat#RD} or {@link ReactionFileFormat#AUTO}
     * @return result RinchiKeyOutput object
     * @see #fileTextToRinchiKey(String, RinchiKeyType)
     * @see #fileTextToRinchiKey(String, RinchiKeyType, RinchiOptions)
     */
    public static RinchiKeyOutput fileTextToRinchiKey(String reactionFileText, RinchiKeyType keyType, RinchiOptions options, ReactionFileFormat fileFormat) {
        checkLibrary();
        requireNonNull(reactionFileText, "reactionFileText");
        requireNonNull(keyType, "keyType");
        requireNonNull(options, "options");
        requireNonNull(fileFormat, "fileFormat");

        PointerByReference out_rinchi_key = new PointerByReference();
        boolean forceEq = options.getFlags().contains(RinchiFlag.ForceEquilibrium);
        int errCode = RinchiLibrary.rinchilib_rinchikey_from_file_text(fileFormat.toString(), reactionFileText,
                keyType.getShortDesignation(), forceEq, out_rinchi_key);

        if (errCode != 0) {
            String err = RinchiLibrary.rinchilib_latest_err_msg();
            return new RinchiKeyOutput("", keyType, Status.ERROR, errCode, err);
        }

        Pointer p = out_rinchi_key.getValue();
        String rinchi_key = p.getString(0);

        return new RinchiKeyOutput(rinchi_key, keyType, Status.SUCCESS, 0, "");
    }

    /**
     * Converts RInChI and RAuxInfo into a reaction, represented in MDL RXN or RDFile format.
     * The output object of type FileTextOutput contains the conversion status, error messages if any,
     * and reaction text file.
     * <br>
     * The reaction file format is specified by the user. Possible values are {@link ReactionFileFormat#RXN},
     * {@link ReactionFileFormat#RD} and {@link ReactionFileFormat#AUTO}.
     *
     * @param rinchi input RInChi string
     * @param auxInfo input RAuxInfo string
     * @param fileFormat the MDL file format for reaction representation: RXN or RDFile (AUTO acts as RDFile)
     * @return resultant FileTextOutput object
     */
    public static FileTextOutput rinchiToFileText(String rinchi, String auxInfo, ReactionFileFormat fileFormat) {
        checkLibrary();
        requireNonNull(rinchi, "rinchi");
        requireNonNull(auxInfo, "auxInfo");
        requireNonNull(fileFormat, "fileFormat");

        PointerByReference out_file_text_p = new PointerByReference();
        int errCode = RinchiLibrary.rinchilib_file_text_from_rinchi(rinchi, auxInfo, fileFormat.toString(), out_file_text_p);

        if (errCode != 0) {
            String err = RinchiLibrary.rinchilib_latest_err_msg();
            return new FileTextOutput("", fileFormat, Status.ERROR, errCode, err);
        }

        Pointer p = out_file_text_p.getValue();
        String reactFileText = p.getString(0);

        return new FileTextOutput(reactFileText, fileFormat, Status.SUCCESS, 0, "");
    }

    /**
     * Generates a RInChIKey from a RInChI string.
     * The output object of type RinchiKeyOutput contains the generation status, error messages if any,
     * and RInChIKey.
     * <br>
     * RInChIKey could be of type: {@link RinchiKeyType#LONG}, {@link RinchiKeyType#SHORT} or {@link RinchiKeyType#WEB}.
     *
     * @param keyType RInChI-Key type
     * @param rinchi input RInChi string
     * @return result RinchiKeyOutput object
     */
    public static RinchiKeyOutput rinchiToRinchiKey(RinchiKeyType keyType, String rinchi) {
        checkLibrary();
        requireNonNull(keyType, "keyType");
        requireNonNull(rinchi, "rinchi");

        PointerByReference out_rinchi_key = new PointerByReference();
        int errCode = RinchiLibrary.rinchilib_rinchikey_from_rinchi(rinchi, keyType.getShortDesignation(), out_rinchi_key);

        if (errCode != 0) {
            String err = RinchiLibrary.rinchilib_latest_err_msg();
            return new RinchiKeyOutput("", keyType, Status.ERROR, errCode, err);
        }

        Pointer p = out_rinchi_key.getValue();
        String rinchi_key = p.getString(0);

        return new RinchiKeyOutput(rinchi_key, keyType, Status.SUCCESS, 0, "");
    }

    /**
     * Splits a RInChI into individual components, that is a list of InChIs.
     * The output object of type RinchiDecompositionOutput contains the decomposition status, error messages if any,
     * an array of InChIs together with their roles as components in the reaction, and the RInChI reaction direction.
     *
     * @param rinchi input RInChi string
     * @return result RinchiDecompositionOutput object
     * @see #decomposeRinchi(String, String)
     */
    public static RinchiDecompositionOutput decomposeRinchi(String rinchi) {
        return decomposeRinchi(rinchi, "");
    }

    /**
     * Splits a RInChI into individual components, that is a list of InChIs.
     * The output object of type RinchiDecompositionOutput contains the decomposition status, error messages if any,
     * an array of InChIs together with their roles as components in the reaction, an array of AuxInfos, and the
     * RInChI reaction direction.
     *
     * @param rinchi input RInChi string
     * @param auxInfo input RAuxInfo string
     * @return result RinchiDecompositionOutput object
     * @see #decomposeRinchi(String)
     */
    public static RinchiDecompositionOutput decomposeRinchi(String rinchi, String auxInfo) {
        checkLibrary();
        requireNonNull(rinchi, "rinchi");
        requireNonNull(auxInfo, "auxInfo");

        PointerByReference out_inchis_text_p = new PointerByReference();
        int errCode = RinchiLibrary.rinchilib_inchis_from_rinchi(rinchi, auxInfo, out_inchis_text_p);

        if (errCode != 0) {
            String err = RinchiLibrary.rinchilib_latest_err_msg();
            return new RinchiDecompositionOutput(ReactionDirection.FORWARD, null, null, null,
                    Status.ERROR, errCode, err);
        }

        Pointer p = out_inchis_text_p.getValue();
        String s = p.getString(0);

        return parseNativeOutInchisText(s);
    }

    /**
     * Returns the version of the wrapped RInChI C library.
     * @return version number string or <code>null</code> if there is an error when retrieving the version number
     */
    public static String getRinchiLibraryVersion() {
        return getVersion(PROPERTY_KEY_RINCHI_VERSION);
    }

    /**
     * Returns the version of the JNA-RInChI Java library.
     * @return version number string or <code>null</code> if there is an error when retrieving the version number
     */
    public static String getJnaRinchiVersion() {
        return getVersion(PROPERTY_KEY_JNARINCHI_VERSION);
    }

    private static String getVersion(String propertyKey) {
        try (InputStream is = JnaRinchi.class.getResourceAsStream(PROPERTY_FILE_NAME)) {
            Properties props = new Properties();
            props.load(is);
            return props.getProperty(propertyKey);
        } catch (Exception e) {
            return null;
        }
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

        String[] lines = outText.split(RINCHI_DECOMPOSE_LINE_SEPARATOR);
        StringBuilder errorBuffer = new StringBuilder();

        //Check number of lines and determine the number of components
        int nLines = lines.length;
        int nComponents = -1;
        if ((nLines < 2) || (nLines % 2) != 0) {
            errorBuffer.append("Incorrect number of lines. Expected even number\n");
        } else
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
                    errorBuffer.append("Incorrect direction definition in the first line: ").append(lines[0]).append("\n");
            } else
                errorBuffer.append("Incorrect first line: has to start with ").append(RINCHI_DECOMPOSE_DIRECTION_SHORT_DESIGNATION)
                        .append(":").append("\n");
        } else
            errorBuffer.append("No lines available!\n");

        //Iterate all reaction components
        if (nComponents > 0)
            for (int i = 0; i < nComponents; i++) {
                String rinchiLine = lines[2 + 2 * i];
                String auxInfoLine = lines[2 + 2 * i + 1];

                boolean flagRinchiLineOK = false;
                ReactionComponentRole role = null;
                if (rinchiLine.length() < 2)
                    errorBuffer.append("Incorrect RInChI component line: ").append(rinchiLine).append("\n");
                else {
                    role = ReactionComponentRole.getRoleFromShortDesignation(rinchiLine.substring(0, 1));
                    if (role == null)
                        errorBuffer.append("Incorrect RInChI component line: incorrect role: ").append(rinchiLine).append("\n");
                    else {
                        inchis[i] = rinchiLine.substring(2);
                        flagRinchiLineOK = true;
                    }
                }

                if (flagRinchiLineOK) {
                    if (auxInfoLine.length() < 2)
                        errorBuffer.append("Incorrect AuxInfo component line: ").append(auxInfoLine).append("\n");
                    else {
                        ReactionComponentRole role2 = ReactionComponentRole.getRoleFromShortDesignation(auxInfoLine.substring(0, 1));
                        if (role2 == null || (role != role2))
                            errorBuffer.append("Incorrect AuxInfo component line: incorrect role: ").append(auxInfoLine).append("\n");
                        else {
                            auxInfos[i] = auxInfoLine.substring(2);
                            roles[i] = role;
                        }
                    }
                }
            }

        String err = errorBuffer.toString();
        if (err.isEmpty())
            try {
                return new RinchiDecompositionOutput(direction, inchis, auxInfos, roles,
                        Status.SUCCESS, 0, "");
            } catch (IllegalArgumentException exception) {
                // we end up here if the number of InChIs, auxiliary information and reaction component role is not equal
                return new RinchiDecompositionOutput(direction, null, null, null,
                        Status.ERROR, ERROR_CODE_DECOMPOSE_FROM_LINES, exception.getMessage());
            }
        else {
            //Generally this should never happen. Otherwise, it is a bug in RInChI native C++ code
            return new RinchiDecompositionOutput(direction, null, null, null,
                    Status.ERROR, ERROR_CODE_DECOMPOSE_FROM_LINES, err);
        }
    }

    private static void requireNonNull(Object object, String argumentName) {
        if (object == null) {
            throw new IllegalArgumentException("The argument '" + argumentName + "' must not be null.");
        }
    }

    private static void checkLibrary() {
        if (libraryLoadingError != null) {
            throw new RuntimeException("Error loading RInChI native code. Please check that the binaries for your platform (" + platform + ") have been included on the classpath.", libraryLoadingError);
        }
    }
}
