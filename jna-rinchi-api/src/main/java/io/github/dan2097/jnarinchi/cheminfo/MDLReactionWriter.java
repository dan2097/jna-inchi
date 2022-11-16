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
package io.github.dan2097.jnarinchi.cheminfo;

import java.text.NumberFormat;
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
 * Writes a reaction ({@link RinchiInput} object) into a MDL RXN or RDFile file format string.
 * Null pointer is returned when writing is unsuccessful. All errors could be
 * taken as a list with function {@link #getErrors()} or 
 * as a single string by means of function {@link #getAllErrors()}.
 * 
 * File format is set by function {@link #setFormat(ReactionFileFormat)}.
 * By default the file format is set to {@link ReactionFileFormat#RD}.
 * Format {@link ReactionFileFormat#AUTO} is treated as equivalent to {@link ReactionFileFormat#RD}.  
 * 
 * Atom parities for all stereo elements of type
 * {@link io.github.dan2097.jnainchi.InchiStereoType#Tetrahedral}
 * are recalculated to match the tetrahedral stereo element with ligand atoms reordered with 
 * increasing atom indices (as it is the good practice for MDL format storage). 
 * The latter is performed since the input {@link RinchiInput} object may have 
 * stereo elements with an arbitrary order of the ligand atoms. 
 * 
 * @author nick
 */
public class MDLReactionWriter {
	
	private static final String LINE_SEPARATOR = "\n";
	
	private StringBuilder stringBuilder;
	private final List<String> errors = new ArrayList<>();
	private ReactionFileFormat format = ReactionFileFormat.RD;
	//This flag is not made visible as it is preferred always true 
	//in order to follow the correct atom ordering for MDL format 
	private boolean checkParityAccordingAtomNumbering = true;
	// currently, only RXN and RDFile V2000 is supported
	private final CTabVersion ctabVersion = CTabVersion.V2000;
	private RinchiInput rInput = null;
	private final List<RinchiInputComponent> reagents = new ArrayList<>();
	private final List<RinchiInputComponent> products = new ArrayList<>();
	private final List<RinchiInputComponent> agents = new ArrayList<>();
	
	/**
	 * Converts a reaction represented as RinchiInput object into an MDL RXN/RDFile format text.
	 * Default format is RDFile. File format is set via setFormat() function.  
	 * 
	 * @param rInp input RinchiInput object
	 * @return reaction file text
	 */
	public String rinchiInputToFileText(RinchiInput rInp) {
		this.rInput = rInp;
		if (rInput == null) {
			errors.add("RinchiInput is null!");
			return null;
		}
		
		reset();
		analyzeComponents();
		
		if (format == ReactionFileFormat.RD || format == ReactionFileFormat.AUTO )
			addRDFileHeader("1", "JNA-RIN", "");
		
		addRXNHeader("Reaction 1", "      JNA-RIN", "");
		
		//Add RXN count line: rrrppp
		addInteger(reagents.size(), 3); //rrr
		addInteger(products.size(), 3); //ppp
		stringBuilder.append(LINE_SEPARATOR);
		
		//Add reagents
		for (int i = 0; i < reagents.size(); i++) 
			addRinchiInputComponent(reagents.get(i), "Reagent " + (i+1), "  JNA-RIN", "");
		//Add products
		for (int i = 0; i < products.size(); i++) 
			addRinchiInputComponent(products.get(i), "Product " + (i+1), "  JNA-RIN", "");
		
		//Add agents for RDFile
		if (format == ReactionFileFormat.RD || format == ReactionFileFormat.AUTO ) {
			for (int i = 0; i < agents.size(); i++) 
				addRinchiInputComponentAsAgent(agents.get(i), i, "Agent " + (i+1), "  JNA-RIN", "");
		}
		
		return stringBuilder.toString();
	}
	
	private void reset() {		
		stringBuilder = new StringBuilder();
		errors.clear();
		reagents.clear();
		products.clear();
		agents.clear();
	}
	
	private void addRinchiInputComponent(RinchiInputComponent ric, String line1, String line2, String line3)
	{
		addMolHeader(line1, line2, line3);
		addCTabBlockV2000(ric);
		addPropertyBlock(ric);
		stringBuilder.append("M  END");
		stringBuilder.append(LINE_SEPARATOR);
	}
	
	private void addRinchiInputComponentAsAgent(RinchiInputComponent ric, int agentIndex, String line1, String line2, String line3)
	{
		stringBuilder.append("$DTYPE RXN:VARIATION(1):AGENT(").append(agentIndex + 1).append("):MOL(1):MOLSTRUCTURE");
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append("$DATUM $MFMT");
		stringBuilder.append(LINE_SEPARATOR);
		
		//Molecule header
		stringBuilder.append(line1);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line2);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line3);
		stringBuilder.append(LINE_SEPARATOR);
		
		addCTabBlockV2000(ric);
		addPropertyBlock(ric);
		stringBuilder.append("M  END");
		stringBuilder.append(LINE_SEPARATOR);
	}
	
	private void addMolHeader(String line1, String line2, String line3) {
		stringBuilder.append("$MOL");
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line1);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line2);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line3);
		stringBuilder.append(LINE_SEPARATOR);
	}
	
	private void addRDFileHeader(String info1, String info2, String info3) {
		stringBuilder.append("$RDFILE ");
		stringBuilder.append(info1);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append("$DATM ");
		stringBuilder.append(info2);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append("$RFMT ");
		stringBuilder.append(info3);
		stringBuilder.append(LINE_SEPARATOR);
	}
	
	private void addRXNHeader(String line2, String line3, String line4) {
		stringBuilder.append("$RXN");
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line2);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line3);
		stringBuilder.append(LINE_SEPARATOR);
		stringBuilder.append(line4);
		stringBuilder.append(LINE_SEPARATOR);
	}
	
	private void addCTabBlockV2000(RinchiInputComponent ric) {
		//Counts line: aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
		addInteger(ric.getAtoms().size(), 3); //aaa
		addInteger(ric.getBonds().size(), 3); //bbb
		stringBuilder.append("  0"); //lll
		stringBuilder.append("  0"); //fff
		
		Map<InchiAtom,InchiStereoParity> parities = StereoUtils.getAtomParities(ric, checkParityAccordingAtomNumbering);
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
		addString(atom.getElName(),3); 
		//dd not specified yet
		stringBuilder.append(" 0");
		//ccc
		if (atom.getRadical() == InchiRadical.DOUBLET)
			stringBuilder.append("  4"); //MDL code for doublet radical
		else
			addInteger(getOldCTABChargeCoding(atom.getCharge()),3);
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
					addInteger(atIndex+1, 4);
					addInteger(ric.getAtom(atIndex).getCharge(), 4);
				}
				stringBuilder.append(LINE_SEPARATOR);
			}			
			//One additional set of k charged atoms (atomList.size() = 8 * numSets + k)
			int k = atomList.size() % 8;
			stringBuilder.append("M  CHG");
			addInteger(k,3);
			for (int i = 0; i < k; i++) {
				int atIndex = atomList.get(numSets * 8 + i);
				addInteger(atIndex+1, 4);
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
					addInteger(atIndex+1, 4);
					addInteger(ric.getAtom(atIndex).getIsotopicMass(), 4);
				}
				stringBuilder.append(LINE_SEPARATOR);
			}			
			//One additional set of k isotope masses (atomList.size() = 8 * numSets + k)
			int k = atomList.size() % 8;
			stringBuilder.append("M  ISO");
			addInteger(k,3);
			for (int i = 0; i < k; i++) {
				int atIndex = atomList.get(numSets * 8 + i);
				addInteger(atIndex+1, 4);
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
					addInteger(atIndex+1, 4);
					int radCode = getRadicalMDLCode(ric.getAtom(atIndex).getRadical());
					addInteger(radCode, 4);
				}
				stringBuilder.append(LINE_SEPARATOR);
			}			
			//One additional set of k charged atoms (atomList.size() = 8 * numSets + k)
			int k = atomList.size() % 8;
			stringBuilder.append("M  RAD");
			addInteger(k,3);
			for (int i = 0; i < k; i++) {
				int atIndex = atomList.get(numSets * 8 + i);
				addInteger(atIndex+1, 4);
				int radCode = getRadicalMDLCode(ric.getAtom(atIndex).getRadical());
				addInteger(radCode, 4);
			}
			stringBuilder.append(LINE_SEPARATOR);
		}
	}
	
	private List<Integer> getAtomsWithCharge(RinchiInputComponent ric){
		List<Integer> atomList = new ArrayList<>();
		for (int i = 0; i < ric.getAtoms().size(); i++)
			if (ric.getAtom(i).getCharge() != 0)
				atomList.add(i);
		return atomList;
	}
	
	private List<Integer> getAtomsWithIsotope(RinchiInputComponent ric){
		List<Integer> atomList = new ArrayList<>();
		for (int i = 0; i < ric.getAtoms().size(); i++)
			if (ric.getAtom(i).getIsotopicMass() != 0)
				atomList.add(i);
		return atomList;
	}
	
	private List<Integer> getAtomsWithRadical(RinchiInputComponent ric){
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
		}
		return 0;
	}
	
	private int getBondMDLBondCode(InchiBond bond) {
		switch (bond.getType()) {
		case SINGLE:
			return 1;
		case DOUBLE:
			return 2;
		case TRIPLE:
			return 3;
		case ALTERN:
			return 4; //stored as MDL aromatic
		}
		return 1;
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
		}
		return 0;
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
	
	private void addString(String vStr, int fixedSpace) {
		addString(vStr, fixedSpace, true);
	}
	
	private void addString(String vStr, int fixedSpace, boolean spacesAtTheEnd) {
		//Adding empty spaces and value
		int nEmptySpaces = fixedSpace - vStr.length();
		if (nEmptySpaces < 0) 
			stringBuilder.append(vStr.substring(fixedSpace));
		else {
			if (spacesAtTheEnd) {
				stringBuilder.append(vStr);
				for (int i = 0; i < nEmptySpaces; i++)
					stringBuilder.append(" ");
			} else {
				for (int i = 0; i < nEmptySpaces; i++)
					stringBuilder.append(" ");
				stringBuilder.append(vStr);
			}
		}
	}
	
	private void addInteger(int value, int fixedSpace) {
		addNumber(Integer.toString(value), fixedSpace);
	}
	
	private void addDouble(Double value) {
		addDouble(value, MDLReactionUtils.MDL_NUMBER_FORMAT, MDLReactionUtils.MDL_FLOAT_SPACES);
	}
	
	private void addDouble(Double value, NumberFormat nf, int fixedSpace) {
		if(Double.isNaN(value) || Double.isInfinite(value)) {
			addNumber(nf.format(0.0), fixedSpace);
		} else {
			addNumber(nf.format(value), fixedSpace);
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
		}
		return 0;
	}
	
	/**
	 * Gets a list of generated errors.
	 */
	public List<String> getErrors() {
		return errors;
	}
	
	/**
	 * Gets a single string with all errors.
	 */
	public String getAllErrors() {
		StringBuilder sb = new StringBuilder();
		for (String err: errors)
			sb.append(err).append("\n");
		return sb.toString();
	}

	/**
	 * Gets the reaction file format: RXN or RDFile (also AUTO can be used).
	 */
	public ReactionFileFormat getFormat() {
		return format;
	}
	
	/**
	 * Sets the reaction file format: RXN, RDFile or AUTO
	 * 
	 * @param format reaction file format
	 */
	public void setFormat(ReactionFileFormat format) {
		if (format != null)
			this.format = format;
	}
	
	/**
	 * Gets the CTab Version
	 */
	public CTabVersion getCtabVersion() {
		return ctabVersion;
	}

}
