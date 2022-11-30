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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondStereo;
import io.github.dan2097.jnainchi.InchiRadical;
import io.github.dan2097.jnainchi.InchiStereoParity;
import io.github.dan2097.jnarinchi.CTabVersion;
import io.github.dan2097.jnarinchi.ReactionFileFormat;
import io.github.dan2097.jnarinchi.RinchiInput;
import io.github.dan2097.jnarinchi.RinchiInputComponent;

/**
 * Writes a reaction ({@link RinchiInput} object) into an MDL RXN or RDFile file format string.
 * <p>
 * By default, i.e. if the constructor {@link #MDLReactionWriter()} without any arguments is used,
 * the expected format is set to {@link ReactionFileFormat#RD}. If the RXN format is to be
 * specified, the format can be set by {@link #MDLReactionWriter(ReactionFileFormat)}.
 * The file format {@link ReactionFileFormat#AUTO} is treated as equivalent to the default
 * setting of {@link ReactionFileFormat#RD}.
 * </p>
 * <p>
 * Atom parities for all stereo elements of type
 * {@link io.github.dan2097.jnainchi.InchiStereoType#Tetrahedral}
 * are recalculated to match the tetrahedral stereo element with ligand atoms reordered with
 * increasing atom indices (as it is the good practice for MDL format storage).
 * The latter is performed since the input {@link RinchiInput} object may have
 * stereo elements with an arbitrary order with regard to the ligand atoms.
 * </p>
 *
 * @author nick
 */
public class MDLReactionWriter {
    private static final String LINE_SEPARATOR = "\n";
    private static final String RDFILE_LINE_1_RDFILE = "$RDFILE 1";
    private static final String RDFILE_LINE_2_DATM = "$DATM";
    private static final String RDFILE_LINE_3_RFMT = "$RFMT";
    private static final String RXN_HEADER_LINE_1_RXN = "$RXN";
    private static final String RXN_HEADER_LINE_2_REACTION_NAME = "";
    private static final String RXN_HEADER_LINE_3_PROGRAM = "      JNA-RIN  ";
    private static final String RXN_HEADER_LINE_4_COMMENT = "";
    private static final String MOLFILE_MOL = "$MOL";
    private static final String MOLFILE_HEADER_LINE_2_PROGRAM = "  JNA-RIN ";
    private static final String MOLFILE_HEADER_LINE_3_COMMENT = "";
    private static final String MOLFILE_M_END = "M  END";
    private static final DateTimeFormatter DATE_TIME_FORMATTER_RDFILE = DateTimeFormatter.ofPattern("MM/dd/yy HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_RXN = DateTimeFormatter.ofPattern("MMddyyyyHHmm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER_MOLFILE = DateTimeFormatter.ofPattern("MMddyyHHmm");
    
    private final ReactionFileFormat format;
    // currently, only RXN and RDFile V2000 is supported
    private final CTabVersion ctabVersion = CTabVersion.V2000;
    //This flag is not made visible as it is preferred always true
    //in order to follow the correct atom ordering for MDL format
    private final boolean checkParityAccordingAtomNumbering = true;

    /**
     * Instantiates a new MDLReactionWriter with default settings.
     * <br>
     * Default settings are
     * <ul>
     *     <li>reaction file format {@link ReactionFileFormat#RD}</li>
     * </ul>
     * @see #MDLReactionWriter(ReactionFileFormat)
     */
    public MDLReactionWriter() {
        this(ReactionFileFormat.RD);
    }

    /**
     * Instantiates a new MDLReactionWriter with the specified {@link ReactionFileFormat reaction file format}.
     * @param format the reaction file format to write
     * @see #MDLReactionWriter()
     */
    public MDLReactionWriter(ReactionFileFormat format) {
        this.format = format;
    }
    
    /**
     * Converts a reaction represented as RinchiInput object into an MDL RXN/RDFile format text.
     * Default format is RDFile. File format is set via setFormat() function.
     *
     * @param rInp input RinchiInput object
     * @return reaction file text
     */
    public String rinchiInputToFileText(RinchiInput rInp) {
      return new MDLReactionWriterInstance(rInp).write();
    }
    
    /**
     * Gets the reaction file format: RXN or RDFile (also AUTO can be used).
     *
     * @return reaction file format
     */
    public ReactionFileFormat getFormat() {
        return format;
    }

    /**
     * Gets the CTAB Version.
     *
     * @return the CTAB version
     */
    public CTabVersion getCtabVersion() {
        return ctabVersion;
    }
    
    private class MDLReactionWriterInstance {

        private final RinchiInput rInput;
        private final StringBuilder stringBuilder = new StringBuilder();
        private final List<RinchiInputComponent> reagents = new ArrayList<>();
        private final List<RinchiInputComponent> products = new ArrayList<>();
        private final List<RinchiInputComponent> agents = new ArrayList<>();
        
        MDLReactionWriterInstance(RinchiInput rInput) {
            if (rInput == null) {
                throw new IllegalArgumentException("RinchiInput is null!");
            }
            this.rInput = rInput;
        }

        String write() {
            analyzeComponents();

            if (format == ReactionFileFormat.RD || format == ReactionFileFormat.AUTO)
                addRDFileHeader();

            addRXNHeader();

            //Add RXN count line: rrrppp
            addInteger(reagents.size(), 3); //rrr
            addInteger(products.size(), 3); //ppp
            stringBuilder.append(LINE_SEPARATOR);

            //Add reagents
            for (int i = 0; i < reagents.size(); i++)
                addRinchiInputComponent(reagents.get(i), "Reagent " + (i + 1));
            //Add products
            for (int i = 0; i < products.size(); i++)
                addRinchiInputComponent(products.get(i), "Product " + (i + 1));

            //Add agents for RDFile
            if (format == ReactionFileFormat.RD || format == ReactionFileFormat.AUTO) {
                for (int i = 0; i < agents.size(); i++)
                    addRinchiInputComponentAsAgent(agents.get(i), i, "Agent " + (i + 1));
            }

            return stringBuilder.toString();
        }

        private void addRinchiInputComponent(RinchiInputComponent ric, String moleculeName) {
            addMolHeader(moleculeName);
            addCTabBlockV2000(ric);
            addPropertyBlock(ric);
            stringBuilder.append(MOLFILE_M_END);
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addRinchiInputComponentAsAgent(RinchiInputComponent ric, int agentIndex, String moleculeName) {
            stringBuilder.append("$DTYPE RXN:VARIATION(1):AGENT(").append(agentIndex + 1).append("):MOL(1):MOLSTRUCTURE");
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append("$DATUM $MFMT");
            stringBuilder.append(LINE_SEPARATOR);

            //Molecule header
            stringBuilder.append(moleculeName);
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(MOLFILE_HEADER_LINE_2_PROGRAM);
            stringBuilder.append(LocalDateTime.now().format(DATE_TIME_FORMATTER_MOLFILE));
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(MOLFILE_HEADER_LINE_3_COMMENT);
            stringBuilder.append(LINE_SEPARATOR);

            addCTabBlockV2000(ric);
            addPropertyBlock(ric);
            stringBuilder.append(MOLFILE_M_END);
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addMolHeader(String moleculeName) {
            stringBuilder.append(MOLFILE_MOL);
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(moleculeName);
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(MOLFILE_HEADER_LINE_2_PROGRAM);
            stringBuilder.append(LocalDateTime.now().format(DATE_TIME_FORMATTER_MOLFILE));
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(MOLFILE_HEADER_LINE_3_COMMENT);
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addRDFileHeader() {
            stringBuilder.append(RDFILE_LINE_1_RDFILE);
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(RDFILE_LINE_2_DATM);
            stringBuilder.append("    ");
            stringBuilder.append(LocalDateTime.now().format(DATE_TIME_FORMATTER_RDFILE));
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(RDFILE_LINE_3_RFMT);
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addRXNHeader() {
            stringBuilder.append(RXN_HEADER_LINE_1_RXN);
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(RXN_HEADER_LINE_2_REACTION_NAME);
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(RXN_HEADER_LINE_3_PROGRAM);
            stringBuilder.append(LocalDateTime.now().format(DATE_TIME_FORMATTER_RXN));
            stringBuilder.append(LINE_SEPARATOR);
            stringBuilder.append(RXN_HEADER_LINE_4_COMMENT);
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addCTabBlockV2000(RinchiInputComponent ric) {
            //Counts line: aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
            addInteger(ric.getAtoms().size(), 3); //aaa
            addInteger(ric.getBonds().size(), 3); //bbb
            stringBuilder.append("  0"); //lll
            stringBuilder.append("  0"); //fff

            Map<InchiAtom, InchiStereoParity> parities = StereoUtils.getAtomParities(ric, checkParityAccordingAtomNumbering);
            //ccc
            if (parities.isEmpty())
                stringBuilder.append("  0");
            else
                stringBuilder.append("  1");
            stringBuilder.append("  0"); //sss
            stringBuilder.append("  0"); //xxx
            stringBuilder.append("  0"); //rrr
            stringBuilder.append("  0"); //ppp
            stringBuilder.append("  0"); //iii
            stringBuilder.append(MDLReactionUtils.CTAB_LINE_COUNT); //mmm
            // at the moment, only RXN and RDF V2000 is supported
            stringBuilder.append(" ").append(CTabVersion.V2000); //vvvvvv
            stringBuilder.append(LINE_SEPARATOR);

            //Add Atom block
            for (int i = 0; i < ric.getAtoms().size(); i++) {
                InchiStereoParity parity = parities.get(ric.getAtom(i));
                //In general, the atoms within InchiStero object
                //may have atom numbering which is not increasing within InchiInput object
                //In this case parity value may need to be swapped.
                addAtomLine(ric.getAtom(i), parity);
            }
            //Add Bond block
            for (int i = 0; i < ric.getBonds().size(); i++)
                addBondLine(ric.getBond(i), ric);
        }

        private void addAtomLine(InchiAtom atom, InchiStereoParity parity) {
            //MDL atom line specification
            //xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee

            //x,y,z coordinates
            addDouble(atom.getX());
            addDouble(atom.getY());
            addDouble(atom.getZ());
            stringBuilder.append(" ");
            //aaa
            addString(atom.getElName());
            //dd not specified yet
            stringBuilder.append(" 0");
            //ccc
            if (atom.getRadical() == InchiRadical.DOUBLET)
                stringBuilder.append("  4"); //MDL code for doublet radical
            else
                addInteger(getOldCTABChargeCoding(atom.getCharge()), 3);
            //sss stereo parity
            if (parity != null) {
                switch (parity) {
                    case ODD:
                        stringBuilder.append("  1");
                        break;
                    case EVEN:
                        stringBuilder.append("  2");
                        break;
                    case UNKNOWN:
                        stringBuilder.append("  3");
                        break;
                    default:
                        stringBuilder.append("  0");
                }
            }
            //hhh: implicit H atoms: used for query
            //addInteger(getImplicitHAtomCoding(atom),3);
            stringBuilder.append("  0");
            //bbb stereo box care: used for queries
            stringBuilder.append("  0");
            //vvv valence
            stringBuilder.append("  0");
            //HHH not specified
            stringBuilder.append("  0");

            //rrriiimmmnnneee are not specified
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addBondLine(InchiBond bond, RinchiInputComponent ric) {
            //MDL bond line specification
            //111222tttsssxxxrrrccc

            int firstAt = ric.getAtoms().indexOf(bond.getStart()) + 1; //1-based atom numbering
            int secondAt = ric.getAtoms().indexOf(bond.getEnd()) + 1; //1-based atom numbering

            //Writing 111222 portion
            if (isWedgeEndAtSecondAtom(bond.getStereo())) {
                //The places of atoms are swapped to match wedge direction
                //111 first atom
                addInteger(secondAt, 3);
                //222 second atom
                addInteger(firstAt, 3);
            } else {
                //111 first atom
                addInteger(firstAt, 3);
                //222 second atom
                addInteger(secondAt, 3);
            }
            //ttt bond type
            addInteger(getBondMDLBondCode(bond), 3);
            //sss bond stereo
            addInteger(getBondMDLStereoCode(bond), 3);
            //xxx = not used
            stringBuilder.append("  0");
            //rrr (bond topology, used only for SSS)
            stringBuilder.append("  0");
            //ccc (reacting center status): 0 - unmarked
            stringBuilder.append("  0");
            stringBuilder.append(LINE_SEPARATOR);
        }

        private void addPropertyBlock(RinchiInputComponent ric) {
            List<Integer> atomList;

            //Add charges
            atomList = getAtomsWithCharge(ric);
            if (!atomList.isEmpty()) {
                //Atom charges are added in sets of 8 atoms (M  CHGnn8 aaa vvv ...)
                int numSets = atomList.size() / 8;
                for (int curSet = 0; curSet < numSets; curSet++) {
                    stringBuilder.append("M  CHG  8");
                    for (int i = 0; i < 8; i++) {
                        int atIndex = atomList.get(curSet * 8 + i);
                        addInteger(atIndex + 1, 4);
                        addInteger(ric.getAtom(atIndex).getCharge(), 4);
                    }
                    stringBuilder.append(LINE_SEPARATOR);
                }
                //One additional set of k charged atoms (atomList.size() = 8 * numSets + k)
                int k = atomList.size() % 8;
                stringBuilder.append("M  CHG");
                addInteger(k, 3);
                for (int i = 0; i < k; i++) {
                    int atIndex = atomList.get(numSets * 8 + i);
                    addInteger(atIndex + 1, 4);
                    addInteger(ric.getAtom(atIndex).getCharge(), 4);
                }
                stringBuilder.append(LINE_SEPARATOR);
            }

            //Add isotope masses
            atomList = getAtomsWithIsotope(ric);
            if (!atomList.isEmpty()) {
                //Atom isotope masses are added in sets of 8 atoms (M  ISOnn8 aaa vvv ...)
                int numSets = atomList.size() / 8;
                for (int curSet = 0; curSet < numSets; curSet++) {
                    stringBuilder.append("M  ISO  8");
                    for (int i = 0; i < 8; i++) {
                        int atIndex = atomList.get(curSet * 8 + i);
                        addInteger(atIndex + 1, 4);
                        addInteger(ric.getAtom(atIndex).getIsotopicMass(), 4);
                    }
                    stringBuilder.append(LINE_SEPARATOR);
                }
                //One additional set of k isotope masses (atomList.size() = 8 * numSets + k)
                int k = atomList.size() % 8;
                stringBuilder.append("M  ISO");
                addInteger(k, 3);
                for (int i = 0; i < k; i++) {
                    int atIndex = atomList.get(numSets * 8 + i);
                    addInteger(atIndex + 1, 4);
                    addInteger(ric.getAtom(atIndex).getIsotopicMass(), 4);
                }
                stringBuilder.append(LINE_SEPARATOR);
            }

            //Add radicals
            atomList = getAtomsWithRadical(ric);
            if (!atomList.isEmpty()) {
                //Atom radicals are added in sets of 8 atoms (M  RADnn8 aaa vvv ...)
                int numSets = atomList.size() / 8;
                for (int curSet = 0; curSet < numSets; curSet++) {
                    stringBuilder.append("M  RAD  8");
                    for (int i = 0; i < 8; i++) {
                        int atIndex = atomList.get(curSet * 8 + i);
                        addInteger(atIndex + 1, 4);
                        int radCode = getRadicalMDLCode(ric.getAtom(atIndex).getRadical());
                        addInteger(radCode, 4);
                    }
                    stringBuilder.append(LINE_SEPARATOR);
                }
                //One additional set of k charged atoms (atomList.size() = 8 * numSets + k)
                int k = atomList.size() % 8;
                stringBuilder.append("M  RAD");
                addInteger(k, 3);
                for (int i = 0; i < k; i++) {
                    int atIndex = atomList.get(numSets * 8 + i);
                    addInteger(atIndex + 1, 4);
                    int radCode = getRadicalMDLCode(ric.getAtom(atIndex).getRadical());
                    addInteger(radCode, 4);
                }
                stringBuilder.append(LINE_SEPARATOR);
            }
        }

        private List<Integer> getAtomsWithCharge(RinchiInputComponent ric) {
            List<Integer> atomList = new ArrayList<>();
            for (int i = 0; i < ric.getAtoms().size(); i++)
                if (ric.getAtom(i).getCharge() != 0)
                    atomList.add(i);
            return atomList;
        }

        private List<Integer> getAtomsWithIsotope(RinchiInputComponent ric) {
            List<Integer> atomList = new ArrayList<>();
            for (int i = 0; i < ric.getAtoms().size(); i++)
                if (ric.getAtom(i).getIsotopicMass() != 0)
                    atomList.add(i);
            return atomList;
        }

        private List<Integer> getAtomsWithRadical(RinchiInputComponent ric) {
            List<Integer> atomList = new ArrayList<>();
            for (int i = 0; i < ric.getAtoms().size(); i++)
                if (ric.getAtom(i).getRadical() != InchiRadical.NONE)
                    atomList.add(i);
            return atomList;
        }

        private int getRadicalMDLCode(InchiRadical inchiRadical) {
            switch (inchiRadical) {
                case SINGLET:
                    return 1;
                case DOUBLET:
                    return 2;
                case TRIPLET:
                    return 3;
                default:
                    return 0;
            }
        }

        private int getBondMDLBondCode(InchiBond bond) {
            switch (bond.getType()) {
                case DOUBLE:
                    return 2;
                case TRIPLE:
                    return 3;
                case ALTERN:
                    return 4; //stored as MDL aromatic
                case SINGLE:
                default:
                    return 1;
            }
        }

        private int getBondMDLStereoCode(InchiBond bond) {
            if (bond.getStereo() != null)
                return inchiBondStereoToMDLStereoCode(bond.getStereo());
            return 0;
        }

        private int inchiBondStereoToMDLStereoCode(InchiBondStereo inchiBoStereo) {
            switch (inchiBoStereo) {
                case SINGLE_1UP:
                case SINGLE_2UP:
                    return 1;
                case SINGLE_1EITHER:
                case SINGLE_2EITHER:
                    return 4;
                case SINGLE_1DOWN:
                case SINGLE_2DOWN:
                    return 6;
                case DOUBLE_EITHER:
                    return 3;
                default:
                    return 0;
            }
        }

        private boolean isWedgeEndAtSecondAtom(InchiBondStereo inchiBoStereo) {
            if (inchiBoStereo == null)
                return false;
            return (inchiBoStereo == InchiBondStereo.SINGLE_2UP
                    || inchiBoStereo == InchiBondStereo.SINGLE_2DOWN
                    || inchiBoStereo == InchiBondStereo.SINGLE_2EITHER);
        }

        private void analyzeComponents() {
            for (RinchiInputComponent ric : rInput.getComponents()) {
                switch (ric.getRole()) {
                    case REAGENT:
                        reagents.add(ric);
                        break;
                    case PRODUCT:
                        products.add(ric);
                        break;
                    case AGENT:
                        agents.add(ric);
                        break;
                }
            }
        }

        private void addString(String vStr) {
            //Adding empty spaces and value
            final int fixedSpace = 3;
            int nEmptySpaces = fixedSpace - vStr.length();
            if (nEmptySpaces < 0)
                stringBuilder.append(vStr.substring(fixedSpace));
            else {
                stringBuilder.append(vStr);
                for (int i = 0; i < nEmptySpaces; i++)
                    stringBuilder.append(" ");
            }
        }

        private void addInteger(int value, int fixedSpace) {
            addNumber(Integer.toString(value), fixedSpace);
        }

        private void addDouble(Double value) {
            if (Double.isNaN(value) || Double.isInfinite(value)) {
                addNumber(MDLReactionUtils.MDL_NUMBER_FORMAT.format(0.0), MDLReactionUtils.MDL_FLOAT_SPACES);
            } else {
                addNumber(MDLReactionUtils.MDL_NUMBER_FORMAT.format(value), MDLReactionUtils.MDL_FLOAT_SPACES);
            }
        }

        private void addNumber(String numberAsString, int fixedSpace) {
            if (numberAsString.length() > fixedSpace) {
                numberAsString = "0";
            }

            //Adding empty spaces and value
            int nEmptySpaces = fixedSpace - numberAsString.length();
            for (int i = 0; i < nEmptySpaces; i++) {
                stringBuilder.append(" ");
            }
            stringBuilder.append(numberAsString);
        }

        private int getOldCTABChargeCoding(int charge) {
            //MDL Charge designation/coding
            //0 = uncharged or value other than these, 1 = +3, 2 = +2, 3 = +1,
            //4 = doublet radical, 5 = -1, 6 = -2, 7 = -3
            switch (charge) {
                case +3:
                    return 1;
                case +2:
                    return 2;
                case +1:
                    return 1;
                case -1:
                    return 5;
                case -2:
                    return 6;
                case -3:
                    return 7;
                default:
                    return 0;
            }
        }

    }
}
