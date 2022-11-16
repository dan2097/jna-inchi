/**
 * This package provides functionality related to <a href="https://dx.doi.org/10.1186/s13321-018-0277-8">RInChI</a>.
 * <h2>Overview</h2>
 * <br>
 * To provide this functionality calls are placed to methods of the <a href="https://github.com/IUPAC-InChI/RInChI/">native RInChI library</a>
 * by means of a <a href="https://github.com/java-native-access/jna">JNA</a> wrapper.
 * <p>
 * The precompiled binaries (i.e., dll and so files) of the IUPAC RInChI native C++ code provide access to their
 * functionality to generate RInChI only by accepting MDL CTAB file text formats RXN and RDfile for inputting and
 * outputting reaction information. Direct access to the C++ internal data structures of the IUPAC RInChI library
 * is <i>not</i> available.
 * </p>
 * <p>
 * The RInChI functionality is provided in Java by the class {@link io.github.dan2097.jnarinchi.JnaRinchi}.
 * The classes {@link io.github.dan2097.jnarinchi.cheminfo.MDLReactionReader} and
 * {@link io.github.dan2097.jnarinchi.cheminfo.MDLReactionWriter} provide utilities for converting RinchiInput data
 * to file texts in RXN and RDFile format and vice versa.
 * </p>
 * <h2>Getting Started</h2>
 * <pre>
 * // load a RDfile into a String
 * String reactionText = ....;
 *
 * // calculate the RInChI from the reaction file
 * RinchiOutput rinchiOutput = JnaRinchi.fileTextToRinchi(reactionText);
 *
 * // get the RInChI and the associated RAuxInfo
 * String rinchi = rinchiOutput.getRinchi();
 * String rauxInfo = rinchiOutput.getAuxInfo();
 *
 * // calculate the long RInChI-Key
 * RinchiKeyOutput rinchiKeyOutput = JnaRinchi.fileTextToRinchiKey(reactionText, RinchiKeyType.LONG);
 * String longRinchiKey = rinchiKeyOutput.getRinchiKey();
 *
 * // decompose the RInChI into its constituent InChIs (and associated AuxInfo if any)
 * RinchiDecompositionOutput rinchiDecompositionOutput = JnaRinchi.decomposeRinchi(rinchi, rauxInfo);
 *
 * // the RinchiDecompositionOutput hands out arrays of inchis, auxinfo and ReactionComponentRole
 * String[] inchis = rinchiDecompositionOutput.getInchis();
 * String[] auxInfos = rinchiDecompositionOutput.getAuxInfos();
 * ReactionComponentRole[] roles = rinchiDecompositionOutput.getRoles();
 *
 * // the direction of the reaction as specified in the RInChI string can also be retrieved
 * ReactionDirection direction = rinchiDecompositionOutput.getDirection();
 * </pre>
 * <h2>Good to know</h2>
 * <h3>Aromaticity</h3>
 * <p>
 * The usage of "aromatic" bonds is strongly discouraged. Instead <b>Kekule</b> structures are <b>recommended</b>.
 * <h3>Implicit Hydrogen Atoms</h3>
 * <p>
 * The reaction components returned from the native RInChI library in the MDL RXN or RDFile format do not indicate the
 * number of implicit hydrogen atoms. The implicit valence (hydrogen atom count) for a given atom is determined by
 * taking into account the element, charge and explicit valence (sum of all bond orders) by using the MDL valence model.
 * </p>
 * <h2>Known Limitations</h2>
 * <h3>Stereochemical Information</h3>
 * <h4>Reaction to RInChI</h4>
 * <p>
 *  The native RInChI library supports tetrahedral chiral atoms, and double bond stereo configurations. The
 *  stereochemical information of allene atoms is not captured in RInChI. Generally, the stereo information
 *  for tetrahedral chiral atoms and double bonds is only taken into account by the native RInChI library
 *  if it can be inferred from the 2D or 3D coordinates of atoms of the reaction components.
 * </p>
 * <h4>RInChI to Reaction</h4>
 * <p>
 * When converting a (RInChI, RAuxInfo) pair to a chemical object of the type RinchiInput, the stereo information
 * is stored implicitly in the atom coordinates. This only works if the RAuxInfo contains 2D or 3D coordinates as
 * the RInChI itself does not store any coordinates.
 * </p>
 * <h3>Agents</h3>
 * <p>
 * The native RInChI library is able to consider agents if they are specified within an RDfile, that is,
 * in the counts line of an RXN record of an RDfile and as Molfiles of the respective RXN record. This is the why
 * RDfile is the preferred (and default, see {@link io.github.dan2097.jnarinchi.ReactionFileFormat#AUTO}) input format
 * to feed into the native RInChI library when consuming a reaction to producing a RInChI.
 * <br>
 * Please note that agents cannot be processed by the native RInChI library if the unofficial agent-extension of the
 * RXN V2000 format is used.
 * </p>
 * <h3>Radicals</h3>
 * <p>
 *    Radicals are supported by the InChI and RInChI standard as well as by <code>jna-inchi</code>.
 * </p>
 */
package io.github.dan2097.jnarinchi;