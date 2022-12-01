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
package io.github.dan2097.jnarinchi.cheminfo;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondStereo;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnainchi.InchiRadical;
import io.github.dan2097.jnainchi.InchiStereo;
import io.github.dan2097.jnainchi.InchiStereoParity;
import io.github.dan2097.jnarinchi.ReactionComponentRole;
import io.github.dan2097.jnarinchi.ReactionFileFormat;
import io.github.dan2097.jnarinchi.RinchiInput;
import io.github.dan2097.jnarinchi.RinchiInputComponent;

/**
 * Reads a reaction from an MDL RXN or RDFile file format.
 * <p>
 * If an error occurs during reading a {@link MdlReactionReaderException} is thrown. This exception
 * can then be queried for a list of all errors with {@link MdlReactionReaderException#getErrors()}
 * or a single string where individual errors are separated by a newline character with
 * {@link MdlReactionReaderException#getAllErrors()}.
 * </p>
 * <p>
 * By default, i.e. if the constructor {@link #MdlReactionReader()} without any arguments is used,
 * the expected format is set to {@link ReactionFileFormat#AUTO} so that the file format
 * RXN or RDFile is automatically recognized from the MDL file text header.
 * If one of the RXN or RDFile formats is to be specifically expected, the format can be
 * enforced by {@link #MdlReactionReader(ReactionFileFormat, boolean)}.
 * </p>
 * <p>
 * Option/flag <code>guessTetrahedralChiralityFromBondsInfo</code> allows
 * to infer the presence of Tetrahedral stereos using only the bond stereo information
 * (e.g. UP/DOWN setting), but the created stereo objects are of type UNDEFINED.
 * The correct recognition of the absolute stereo configuration (if not given via atom attributes)
 * should be done by using the 2D or 3D coordinates.
 * The default setting of the flag {@link #guessTetrahedralChiralityFromBondsInfo} is <code>false</code>.
 * </p>
 * @author nick
 */
public class MdlReactionReader {
  
    private final boolean guessTetrahedralChiralityFromBondsInfo;
    private final ReactionFileFormat expectedFormat;

    /**
     * Instantiates a new MDLReactionReader with default settings.
     * <br>
     * Default settings are
     * <ul>
     *     <li>expected file format {@link ReactionFileFormat#AUTO}</li>
     *     <li>guess tetrahedral chirality from bonds information set to <code>true</code></li>
     * </ul>
     * @see MdlReactionReader (ReactionFileFormat, boolean)
     */
    public MdlReactionReader() {
      this(ReactionFileFormat.AUTO, false);
    }

    /**
     * Instantiates a new MDLReactionReader with the specified settings.
     * @param expectedFormat the expected {@link ReactionFileFormat reaction file format}
     * @param guessTetrahedralChiralityFromBondsInfo indicates whether to guess tetrahedral chirality from bonds information
     * @see MdlReactionReader ()
     */
    public MdlReactionReader(ReactionFileFormat expectedFormat, boolean guessTetrahedralChiralityFromBondsInfo) {
      this.expectedFormat = expectedFormat;
      this.guessTetrahedralChiralityFromBondsInfo = guessTetrahedralChiralityFromBondsInfo;
    }
    
    /**
     * Gets the reaction file format: RXN or RDFile (also AUTO can be used).
     */
    public ReactionFileFormat getExpectedFormat() {
        return expectedFormat;
    }
    
    /**
     * Gets the value of flag guessTetrahedralChiralityFromBondsInfo.
     */
    public boolean isGuessTetrahedralChiralityFromBondsInfo() {
        return guessTetrahedralChiralityFromBondsInfo;
    }
    
    /**
     * Converts a reaction represented as MDL RXN/RDFile format text to RinchiInput object.
     *
     * @param inputString reaction represented as a string (RXN/RDFile format)
     * @return RinchiInput object or <code>null</code> if an error occurs
     * @throws MdlReactionReaderException raised if an error is encountered during reading and parsing of the reaction file
     * @see #fileTextToRinchiInput(BufferedReader)
     */
    public RinchiInput fileTextToRinchiInput(String inputString) throws MdlReactionReaderException {
        BufferedReader reader = new BufferedReader(new StringReader(inputString));
        return fileTextToRinchiInput(reader);
    }

    /**
     * Converts a reaction represented as MDL RXN/RDFile format text to RinchiInput object.
     * Input reaction information is taken from a buffered reader.
     *
     * @param inputReader buffered input reader
     * @return RinchiInput object or <code>null</code> if an error occurs
     * @throws MdlReactionReaderException raised if an error is encountered during reading and parsing of the reaction file
     * @see #fileTextToRinchiInput(String)
     */
    public RinchiInput fileTextToRinchiInput(BufferedReader inputReader) throws MdlReactionReaderException {
        return new MdlReactionReaderInstance(inputReader).toRinchiInput();
    }
    
    private class MdlReactionReaderInstance {
    
        //Reading work variables
        private final List<String> errors = new ArrayList<>();
        private final RinchiInput rInput = new RinchiInput();
        private final BufferedReader inputReader;
    
        private int curLineNum = 0;
        private int numOfReagentsToRead = 0;
        private int numOfProductsToRead = 0;
        private int numOfAtomsToRead = 0;
        private int numOfBondsToRead = 0;
        private String errorComponentContext = "";
    
        MdlReactionReaderInstance(BufferedReader inputReader) {
            this.inputReader = inputReader;
        }

        RinchiInput toRinchiInput() throws MdlReactionReaderException {
            try {
                iterateInputLines();
                inputReader.close();
            } catch (Exception x) {
                errors.add("Error on reading or closing input reader: " + x.getMessage());
            }
            
            if (!errors.isEmpty()) {
                throw new MdlReactionReaderException(errors);
            }
            
            return rInput;
        }

        private String readLine() {
            String line = null;
            curLineNum++;
            try {
                line = inputReader.readLine();
            } catch (Exception x) {
                errors.add("Unable to read line " + curLineNum + ": " + x.getMessage());
            }
            return line;
        }
    
        private int iterateInputLines() {
            //Handle file header/headers according to format
            final ReactionFileFormat format;
            switch (expectedFormat) {
                case AUTO:
                    format = readAutoFileHeader();
                    break;
                case RXN:
                    readRxnFileHeader(true);
                    format = ReactionFileFormat.RXN;
                    break;
                case RD:
                    readRdFileHeader(true);
                    readRxnFileHeader(true);
                    format = ReactionFileFormat.RD;
                    break;
                default:
                    throw new IllegalStateException("Unexpected reaction format for expectedFormat");
            }
            if (!errors.isEmpty())
                return errors.size();
    
            readRxnCountLine();
            if (!errors.isEmpty())
                return errors.size();
    
            //Reading reagents
            for (int i = 0; i < numOfReagentsToRead; i++) {
                RinchiInputComponent ric = readMdlMolecule(true);
                errorComponentContext = "Reading reagent #" + (i + 1) + " ";
                if (ric != null) {
                    MoleculeUtils.setImplicitHydrogenAtoms(ric);
                    ric.setRole(ReactionComponentRole.REAGENT);
                    rInput.addComponent(ric);
                } else
                    return errors.size();
            }
    
            //Reading products
            for (int i = 0; i < numOfProductsToRead; i++) {
                RinchiInputComponent ric = readMdlMolecule(true);
                errorComponentContext = "Reading product #" + (i + 1) + " ";
                if (ric != null) {
                    MoleculeUtils.setImplicitHydrogenAtoms(ric);
                    ric.setRole(ReactionComponentRole.PRODUCT);
                    rInput.addComponent(ric);
                } else
                    return errors.size();
            }
    
            if (format == ReactionFileFormat.RD) {
                iterateAgentsDataLines();
            }
    
            return 0;
        }
    
        private void iterateAgentsDataLines() {
            String line;
            int nAgents = 0;
            while ((line = readLine()) != null) {
                if (line.startsWith("$DATUM ")) {
                    String line1 = line.substring(7).trim();
                    if (line1.startsWith("$MFMT")) {
                        errorComponentContext = "Reading agent #" + (nAgents + 1) + " ";
                        RinchiInputComponent ric = readMdlMolecule(false);
                        if (ric != null) {
                            MoleculeUtils.setImplicitHydrogenAtoms(ric);
                            ric.setRole(ReactionComponentRole.AGENT);
                            rInput.addComponent(ric);
                        }
                        nAgents++;
                    }
                }
            }
        }
    
        private ReactionFileFormat readAutoFileHeader() {
            String line = readLine();
            if (line == null || (!line.startsWith("$RDFILE") && !line.startsWith("$RXN"))) {
                errors.add("RXN/RDFile Header: Line " + curLineNum + " is missing or does not start with $RXN or $RDFILE");
                return ReactionFileFormat.AUTO;
            }
    
            if (line.startsWith("$RDFILE")) {
                readRdFileHeader(false);
                readRxnFileHeader(true);
                return ReactionFileFormat.RD;
            } else {
                //line starts With "$RXN"
                readRxnFileHeader(false);
                return ReactionFileFormat.RXN;
            }
        }
    
        private void readRdFileHeader(boolean readRdFileLine) {
            String line = readLine();
            if (readRdFileLine) {
                if (line == null || !line.startsWith("$RDFILE")) {
                    errors.add("RDFile Header: Line " + curLineNum + " is missing or does not start with $RDFILE");
                    return;
                }
                line = readLine();
            }
    
            if (line == null || !line.startsWith("$DATM")) {
                errors.add("RDFile Header: Line " + curLineNum + " is missing or does not start with $DATM");
                return;
            }
            line = readLine();
            if (line == null || !line.startsWith("$RFMT")) {
                errors.add("RDFile Header: Line " + curLineNum + " is missing or does not start with $RFMT");
            }
        }
    
        private void readRxnFileHeader(boolean readRxnLine) {
            String line = readLine();
            if (readRxnLine) {
                if (line == null || !line.startsWith("$RXN")) {
                    errors.add("RXN Header: Line " + curLineNum + " is missing or does not start with $RXN");
                    return;
                }
                line = readLine();
            }
    
            //Header Line 2 reaction name
            if (line == null) {
                errors.add("RXN Header (reaction name): Line " + curLineNum + " is missing");
                return;
            }
            line = readLine(); //Header Line 3 user name, program, date
            if (line == null) {
                errors.add("RXN Header (user name, progra, date,...): Line " + curLineNum + " is missing");
                return;
            }
            line = readLine(); //Header Line 4 comment
            if (line == null) {
                errors.add("RXN Header (comment or blank): Line " + curLineNum + " is missing");
            }
        }
    
        private void readRxnCountLine() {
            //Read RXN count line: rrrppp
            String line = readLine();
            if (line == null) {
                errors.add("RXN counts Line " + curLineNum + " is missing !");
                return;
            }
            Integer rrr = readInteger(line, 0, 3);
            if (rrr == null || rrr < 0) {
                errors.add("RXN counts (rrrppp) Line  " + curLineNum + " : incorrect number of reagents (rrr part): " + line);
                return;
            } else
                numOfReagentsToRead = rrr;
            Integer ppp = readInteger(line, 3, 3);
            if (ppp == null || ppp < 0) {
                errors.add("RXN counts (rrrppp) Line  " + curLineNum + " : incorrect number of reagents (ppp part): " + line);
            } else {
                numOfProductsToRead = ppp;
            }
        }
    
        private void readMolHeader(boolean readMolLine) {
            String line;
            if (readMolLine) {
                line = readLine();
                if (line == null || !line.startsWith("$MOL")) {
                    errors.add(errorComponentContext + "MOL Start section in Line "
                            + curLineNum + " is missing or does not start with $MOL" + " --> " + line);
                    return;
                }
            }
            line = readLine();
            if (line == null) {
                errors.add(errorComponentContext + "MOL Header (line 1) in Line"
                        + curLineNum + " is missing");
                return;
            }
            line = readLine();
            if (line == null) {
                errors.add(errorComponentContext + "MOL Header (line 2) in Line"
                        + curLineNum + " is missing");
                return;
            }
            line = readLine();
            if (line == null) {
                errors.add(errorComponentContext + "MOL Header (line 3) in Line"
                        + curLineNum + " is missing");
            }
        }
    
        private void readMolCountsLine() {
            //MOL Counts line: aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
            String line = readLine();
            if (line == null) {
                errors.add("MOL counts Line " + curLineNum + " is missing !");
                return;
            }
            Integer aaa = readInteger(line, 0, 3);
            if (aaa == null || aaa < 0) {
                errors.add("MOL counts (aaabbblll...) Line  " + curLineNum
                        + " : incorrect number of atoms (aaa part): " + line);
                return;
            } else
                numOfAtomsToRead = aaa;
            Integer bbb = readInteger(line, 3, 3);
            if (bbb == null || bbb < 0) {
                errors.add("MOL counts (aaabbblll...) Line  " + curLineNum
                        + " : incorrect number of bonds (bbb part): " + line);
            } else {
                numOfBondsToRead = bbb;
            }
        }
    
        private void readMolCtabBlock(RinchiInputComponent ric) {
            Map<InchiAtom, InchiStereoParity> parities = new HashMap<>();
    
            for (int i = 0; i < numOfAtomsToRead; i++) {
                readMolAtomLine(i, ric, parities);
                if (!errors.isEmpty())
                    return;
            }
            for (int i = 0; i < numOfBondsToRead; i++) {
                readMolBondLine(i, ric);
                if (!errors.isEmpty())
                    return;
            }
    
            if (!parities.isEmpty())
                for (Map.Entry<InchiAtom, InchiStereoParity> e : parities.entrySet()) {
                    InchiStereo stereo = StereoUtils.createTetrahedralStereo(ric, e.getKey(), e.getValue());
                    if (stereo != null)
                        ric.addStereo(stereo);
                }
    
            if (guessTetrahedralChiralityFromBondsInfo)
                StereoUtils.guessUndefinedTetrahedralStereosBasedOnBondInfo(ric, parities.keySet());
        }
    
        private void readMolAtomLine(int atomIndex, RinchiInputComponent ric, Map<InchiAtom, InchiStereoParity> parities) {
            //Read MDL atom line
            //xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee
            String line = readLine();
            if (line == null) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " is missing !");
                return;
            }
            Double coordX = readMdlCoordinate(line, 0);
            if (coordX == null) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " coordinate x error --> " + line);
                return;
            }
            Double coordY = readMdlCoordinate(line, 10);
            if (coordY == null) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " coordinate y error --> " + line);
                return;
            }
            Double coordZ = readMdlCoordinate(line, 20);
            if (coordZ == null) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " coordinate z error --> " + line);
                return;
            }
            String atSymbol = 34 > line.length() ? null : line.substring(30, 34).trim();
            if (atSymbol == null) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " atom symbol error --> " + line);
                return;
            }
    
            //Check atom symbol
            int atNum = PeriodicTable.getAtomicNumberFromElementSymbol(atSymbol);
            if (atNum == -1) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " atom symbol error --> " + line);
                return;
            }
    
            //Check old CTAB charge style
            Integer chCode = readInteger(line, 36, 3);
            if (chCode == null || chCode < 0 || chCode > 7) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " atom charge coding error --> " + line);
                return;
            }
            int charge = getChargeFromOldCtabCoding(chCode);
    
            InchiAtom atom = new InchiAtom(atSymbol, coordX, coordY, coordZ);
            ric.addAtom(atom);
    
            if (charge != 0)
                atom.setCharge(charge); //M  CHG molecule property takes precedence if present
    
            //Handle special case for doublet radical
            if (chCode == 4)
                atom.setRadical(InchiRadical.DOUBLET);
    
            //sss stereo parity
            Integer parityCode = readInteger(line, 39, 3);
            if (parityCode == null || parityCode < 0 || parityCode > 3) {
                errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1)
                        + " in Line " + curLineNum + " atom parity coding error --> " + line);
                return;
            }
            InchiStereoParity parity = getParity(parityCode);
            if (parity != null)
                parities.put(atom, parity);
        }
    
        private InchiStereoParity getParity(int parityCode) {
            switch (parityCode) {
                case 1:
                    return InchiStereoParity.ODD;
                case 2:
                    return InchiStereoParity.EVEN;
                case 3:
                    return InchiStereoParity.UNKNOWN;
                default:
                    return null;
            }
        }
    
        private void readMolBondLine(int bondIndex, RinchiInputComponent ric) {
            //Read MDL bond line
            //111222tttsssxxxrrrccc
            String line = readLine();
            if (line == null) {
                errors.add(errorComponentContext + "MOL bond # " + (bondIndex + 1)
                        + " in Line " + curLineNum + " is missing !");
                return;
            }
            Integer a1 = readInteger(line, 0, 3);
            if (a1 == null || a1 < 0 || a1 > numOfAtomsToRead) {
                errors.add("MOL counts (111222ttt...) Line  " + curLineNum
                        + " : incorrect atom number (111 part): " + line);
                return;
            }
            Integer a2 = readInteger(line, 3, 3);
            if (a2 == null || a2 < 0 || a2 > numOfAtomsToRead) {
                errors.add("MOL counts (111222ttt...) Line  " + curLineNum
                        + " : incorrect atom number (222 part): " + line);
                return;
            }
            Integer ttt = readInteger(line, 6, 3);
            if (ttt == null || ttt < 0 || ttt > 3) {
                errors.add("MOL counts (111222ttt...) Line  " + curLineNum
                        + " : incorrect bond typer (ttt part): " + line);
                return;
            }
            Integer sss = readInteger(line, 9, 3);
            if (sss == null) {
                errors.add("MOL counts (111222ttt...) Line  " + curLineNum
                        + " : incorrect bond stereo (sss part): " + line);
                return;
            }
            InchiBondStereo ibs = getBondStereoFromMdlCode(sss);
            if (ibs == null) {
                errors.add("MOL counts (111222ttt...) Line  " + curLineNum
                        + " : incorrect bond stereo (sss part): " + line);
                return;
            }
    
            InchiBond bond = new InchiBond(ric.getAtom(a1 - 1), ric.getAtom(a2 - 1), InchiBondType.of((byte) ttt.intValue()), ibs);
            ric.addBond(bond);
        }
    
        private void readMolPropertiesBlock(RinchiInputComponent ric) {
            String line = readLine();
            while (processPropertyLine(line, ric) == 0)
                line = readLine();
        }
    
        private int processPropertyLine(String line, RinchiInputComponent ric) {
            if (line == null || line.startsWith("M  END"))
                return -1;
    
            if (line.startsWith("M  ISO"))
                readIsotopePropertyLine(line, ric);
    
            if (line.startsWith("M  CHG"))
                readChargePropertyLine(line, ric);
    
            if (line.startsWith("M  RAD"))
                readRadicalPropertyLine(line, ric);
    
            return 0;
        }
    
        private int readIsotopePropertyLine(String line, RinchiInputComponent ric) {
            //MDL format for isotope line:
            //M  ISOnn8 aaa vvv ...
    
            Integer n = readInteger(line, 6, 3); //atom count
            if (n == null || n < 1 || n > 8) {
                errors.add("M ISO molecule property Line (M  ISOnn8 aaa vvv ...) " + curLineNum
                        + " : incorrect number of atoms (nn8 part): " + line);
                return -1;
            }
    
            int pos = 9;
            for (int i = 0; i < n; i++) {
                // aaa
                Integer atomIndex = readInteger(line, pos, 4);
                if (atomIndex == null || atomIndex < 1 || atomIndex > ric.getAtoms().size()) {
                    errors.add("M ISO molecule property Line (M  ISOnn8 aaa vvv ...) " + curLineNum
                            + " : incorrect atom index for (aaa vvv) pair #" + (i + 1) + " in line: " + line);
                    return -2;
                }
                pos += 4;
                // vvv
                Integer mass = readInteger(line, pos, 4);
                if (mass == null || mass < 1) {
                    errors.add("M ISO molecule property Line (M  ISOnn8 aaa vvv ...) " + curLineNum
                            + " : incorrect mass for (aaa vvv) pair #" + (i + 1) + " in line: " + line);
                    return -3;
                }
                pos += 4;
                ric.getAtom(atomIndex - 1).setIsotopicMass(mass);
            }
            return 0;
        }
    
        private int readChargePropertyLine(String line, RinchiInputComponent ric) {
            //MDL format for charge line:
            //M  CHGnn8 aaa vvv ...
    
            Integer n = readInteger(line, 6, 3); //atom count
            if (n == null || n < 1 || n > 8) {
                errors.add("M CHG molecule property Line (M  CHGnn8 aaa vvv ...) " + curLineNum
                        + " : incorrect number of atoms (nn8 part): " + line);
                return -1;
            }
    
            int pos = 9;
            for (int i = 0; i < n; i++) {
                // aaa
                Integer atomIndex = readInteger(line, pos, 4);
                if (atomIndex == null || atomIndex < 1 || atomIndex > ric.getAtoms().size()) {
                    errors.add("M CHG molecule property Line (M  CHGnn8 aaa vvv ...) " + curLineNum
                            + " : incorrect atom index for (aaa vvv) pair #" + (i + 1) + " in line: " + line);
                    return -2;
                }
                pos += 4;
                // vvv
                Integer charge = readInteger(line, pos, 4);
                if (charge == null || charge < -15 || charge > 15) {
                    errors.add("M CHG molecule property Line (M  ISOnn8 aaa vvv ...) " + curLineNum
                            + " : incorrect charge for (aaa vvv) pair #" + (i + 1) + " in line: " + line);
                    return -3;
                }
                pos += 4;
                ric.getAtom(atomIndex - 1).setCharge(charge);
            }
            return 0;
        }
    
        private int readRadicalPropertyLine(String line, RinchiInputComponent ric) {
            //MDL format for radical line:
            //M  RADnn8 aaa vvv ...
    
            Integer n = readInteger(line, 6, 3); //atom count
            if (n == null || n < 1 || n > 8) {
                errors.add("M RAD molecule property Line (M  RADnn8 aaa vvv ...) " + curLineNum
                        + " : incorrect number of atoms (nn8 part): " + line);
                return -1;
            }
    
            int pos = 9;
            for (int i = 0; i < n; i++) {
                // aaa
                Integer atomIndex = readInteger(line, pos, 4);
                if (atomIndex == null || atomIndex < 1 || atomIndex > ric.getAtoms().size()) {
                    errors.add("M RAD molecule property Line (M  RADnn8 aaa vvv ...) " + curLineNum
                            + " : incorrect atom index for (aaa vvv) pair #" + (i + 1) + " in line: " + line);
                    return -2;
                }
                pos += 4;
                // vvv
                Integer radCode = readInteger(line, pos, 4);
                if (radCode == null || radCode < 0 || radCode > 3) {
                    errors.add("M RAD molecule property Line (M  RADnn8 aaa vvv ...) " + curLineNum
                            + " : incorrect radical value for (aaa vvv) pair #" + (i + 1) + " in line: " + line);
                    return -3;
                }
                pos += 4;
                InchiRadical radical = getInchiRadical(radCode);
                ric.getAtom(atomIndex - 1).setRadical(radical);
            }
            return 0;
        }
    
        private RinchiInputComponent readMdlMolecule(boolean readMolLine) {
            RinchiInputComponent ric = new RinchiInputComponent();
            readMolHeader(readMolLine);
            if (!errors.isEmpty())
                return null;
    
            readMolCountsLine();
            if (!errors.isEmpty())
                return null;
    
            readMolCtabBlock(ric);
            if (!errors.isEmpty())
                return null;
    
            readMolPropertiesBlock(ric);
            if (!errors.isEmpty())
                return null;
    
            return ric;
        }
    
        private Integer readInteger(String line, int startPos, int length) {
            int endPos = startPos + length;
            if (startPos > line.length() || endPos > line.length())
                return null;
            String s = line.substring(startPos, endPos).trim();
            try {
                return Integer.parseInt(s);
            } catch (Exception x) {
                errors.add(errorPrefix() + "Error on parsing integer: " + s);
                return null;
            }
        }
    
        private Double readMdlCoordinate(String line, int startPos) {
            int endPos = startPos + MdlReactionUtils.MDL_FLOAT_SPACES;
            if (startPos > line.length() || endPos > line.length())
                return null;
    
            String s = line.substring(startPos, endPos).trim();
            if (line.charAt(startPos + 5) != '.') {
                errors.add(errorPrefix() + "Incorrect coordinate format: " + s);
                return null;
            }
    
            try {
                return Double.parseDouble(s);
            } catch (Exception x) {
                errors.add(errorPrefix() + "Error on parsing float: " + s);
                return null;
            }
        }
    
        private int getChargeFromOldCtabCoding(int code) {
            //MDL Charge designation/coding
            //0 = uncharged or value other than these, 1 = +3, 2 = +2, 3 = +1,
            //4 = doublet radical, 5 = -1, 6 = -2, 7 = -3
            switch (code) {
                case 1:
                    return +3;
                case 2:
                    return +2;
                case 3:
                    return +1;
                case 5:
                    return -1;
                case 6:
                    return -2;
                case 7:
                    return -3;
                default:
                    return 0;
            }
        }
    
        private InchiBondStereo getBondStereoFromMdlCode(int code) {
            switch (code) {
                case 0:
                    return InchiBondStereo.NONE;
                case 1:
                    return InchiBondStereo.SINGLE_1UP;
                case 4:
                    return InchiBondStereo.SINGLE_1EITHER;
                case 6:
                    return InchiBondStereo.SINGLE_1DOWN;
                case 3:
                    return InchiBondStereo.DOUBLE_EITHER;
                default:
                    return null;
            }
        }
    
        private InchiRadical getInchiRadical(int mdlRadicalCode) {
            switch (mdlRadicalCode) {
                case 1:
                    return InchiRadical.SINGLET;
                case 2:
                    return InchiRadical.DOUBLET;
                case 3:
                    return InchiRadical.TRIPLET;
                default:
                    return InchiRadical.NONE;
            }
        }
    
        private String errorPrefix() {
            return "Line " + curLineNum + ": ";
        }
    
    }

}
