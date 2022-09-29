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

import java.io.BufferedReader;
import java.io.StringReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.github.dan2097.jnainchi.InchiAtom;
import io.github.dan2097.jnainchi.InchiBond;
import io.github.dan2097.jnainchi.InchiBondStereo;
import io.github.dan2097.jnainchi.InchiBondType;
import io.github.dan2097.jnainchi.InchiRadical;
import io.github.dan2097.jnainchi.InchiStereo;
import io.github.dan2097.jnainchi.InchiStereoParity;
import io.github.dan2097.jnarinchi.cheminfo.MoleculeUtils;
import io.github.dan2097.jnarinchi.cheminfo.PerTable;
import io.github.dan2097.jnarinchi.cheminfo.StereoUtils;


/**
 * This class provides utilities for converting RinchiInput data to file texts in RXN or RDFile format.
 * @author Nikolay Kochev
 */
public class FileTextUtils {
	
	public static enum CTABVersion {
		V2000, V3000
	}
	
	private static NumberFormat mdlNumberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
	private static final int MDL_FLOAT_SPACES = 10;
	
	static {
		mdlNumberFormat.setMinimumIntegerDigits(1);
		mdlNumberFormat.setMaximumIntegerDigits(4);
		mdlNumberFormat.setMinimumFractionDigits(4);
		mdlNumberFormat.setMaximumFractionDigits(4);
		mdlNumberFormat.setGroupingUsed(false);
	}
	
	private String endLine = "\n";	
	private StringBuilder strBuilder;	
	private List<String> errors = new ArrayList<String>();
	private ReactionFileFormat format = ReactionFileFormat.RD;
	private boolean checkParityAccordingAtomNumbering = true;
	private boolean guessTetrahedralChiralityFromBondsInfo = false;
	private ReactionFileFormat autoRecognizedformat = null;
	private CTABVersion ctabVersion = CTABVersion.V2000; //Currently only V2000 is supported
	private RinchiInput rInput = null;
	private List<RinchiInputComponent> reagents = new ArrayList<RinchiInputComponent>();
	private List<RinchiInputComponent> products = new ArrayList<RinchiInputComponent>();
	private List<RinchiInputComponent> agents = new ArrayList<RinchiInputComponent>();
	
	//Reading work variables
	private String inputString = null;
	private BufferedReader inputReader = null;
	private int curLineNum = 0;
	private int numOfReagentsToRead = 0;
	private int numOfProductsToRead = 0;
	private int numOfAtomsToRead = 0;
	private int numOfBondsToRead = 0;
	private String errorComponentContext = "";
	
		
	/**
	 * Converts a reaction represented as MDL RXN/RDFile format text to RinchiInput object.
	 * 
	 * @param inputString reaction represented as a string (RXN/RDFile format)
	 * @return RinchiInput object
	 */
	public RinchiInput fileTextToRinchiInput(String inputString) {
		this.inputString = inputString;
		BufferedReader reader = new BufferedReader(new StringReader(inputString));
		return fileTextToRinchiInput(reader);
	}
	
	/**
	 * Converts a reaction represented as MDL RXN/RDFile format text to RinchiInput object
	 * Input reaction information is taken from a buffered reader.
	 * 
	 * @param inputReader buffered input reader
	 * @return RinchiInput object
	 */
	public RinchiInput fileTextToRinchiInput(BufferedReader inputReader) {
		this.inputReader = inputReader;
		resetForFileTextReading();
		rInput = new RinchiInput();
		
		try {
			iterateInputLines();
			inputReader.close();
		}
		catch (Exception x) {
			errors.add("Error on reading or closing input reader: " + x.getMessage());
		}
		
		if (errors.isEmpty())
			return rInput;
		else
			return null;
	}
	
	/**
	 * Converts a reaction represented as RinchiInput object into a MDL RXN/RDFile format text.
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
		strBuilder.append(endLine);
		
		//Add reagents
		for (int i = 0; i < reagents.size(); i++) 
			addRrinchiInputComponent(reagents.get(i), "Reagent " + (i+1), "  JNA-RIN", "");
		//Add products
		for (int i = 0; i < products.size(); i++) 
			addRrinchiInputComponent(products.get(i), "Product " + (i+1), "  JNA-RIN", "");
		
		//Add agents for RDFile
		if (format == ReactionFileFormat.RD || format == ReactionFileFormat.AUTO ) {
			for (int i = 0; i < agents.size(); i++) 
				addRrinchiInputComponentAsAgent(agents.get(i), i, "Agent " + (i+1), "  JNA-RIN", "");
		}
		
		return strBuilder.toString();
	}
	
	private void reset() {		
		strBuilder = new StringBuilder();
		errors.clear();
		reagents.clear();
		products.clear();
		agents.clear();
	}
	
	private void resetForFileTextReading() {
		errors.clear();
		reagents.clear();
		products.clear();
		agents.clear();
		curLineNum = 0;
		numOfReagentsToRead = 0;
		numOfProductsToRead = 0;
	}
	
	private void addRrinchiInputComponent(RinchiInputComponent ric, String line1, String line2, String line3) 
	{
		addMolHeader(line1, line2, line3);
		addCTABBlockV2000(ric);
		addPropertyBlock(ric);
		strBuilder.append("M  END");
		strBuilder.append(endLine);
	}
	
	private void addRrinchiInputComponentAsAgent(RinchiInputComponent ric, int agentIndex, String line1, String line2, String line3) 
	{
		strBuilder.append("$DTYPE RXN:VARIATION(1):AGENT(" + (agentIndex + 1) + "):MOL(1):MOLSTRUCTURE");
		strBuilder.append(endLine);
		strBuilder.append("$DATUM $MFMT");
		strBuilder.append(endLine);
		
		//Molecule header
		strBuilder.append(line1);
		strBuilder.append(endLine);		
		strBuilder.append(line2);
		strBuilder.append(endLine);
		strBuilder.append(line3);
		strBuilder.append(endLine);
		
		addCTABBlockV2000(ric);
		addPropertyBlock(ric);
		strBuilder.append("M  END");
		strBuilder.append(endLine);
	}
	
	private void addMolHeader(String line1, String line2, String line3) {
		strBuilder.append("$MOL");
		strBuilder.append(endLine);
		strBuilder.append(line1);
		strBuilder.append(endLine);		
		strBuilder.append(line2);
		strBuilder.append(endLine);
		strBuilder.append(line3);
		strBuilder.append(endLine);
	}
	
	private void addRDFileHeader(String info1, String info2, String info3) {
		strBuilder.append("$RDFILE ");
		strBuilder.append(info1);
		strBuilder.append(endLine);
		strBuilder.append("$DATM ");
		strBuilder.append(info2);
		strBuilder.append(endLine);
		strBuilder.append("$RFMT ");		
		strBuilder.append(info3);
		strBuilder.append(endLine);
	}
	
	private void addRXNHeader(String line2, String line3, String line4) {
		strBuilder.append("$RXN");
		strBuilder.append(endLine);
		strBuilder.append(line2);
		strBuilder.append(endLine);
		strBuilder.append(line3);
		strBuilder.append(endLine);		
		strBuilder.append(line4);
		strBuilder.append(endLine);
	}
	
	private void addCTABBlockV2000(RinchiInputComponent ric) {
		//Counts line: aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
		addInteger(ric.getAtoms().size(), 3); //aaa
		addInteger(ric.getBonds().size(), 3); //bbb
		strBuilder.append("  0"); //lll
		strBuilder.append("  0"); //fff
		
		Map<InchiAtom,InchiStereoParity> parities = StereoUtils.getAtomParities(ric, checkParityAccordingAtomNumbering);
		//ccc
		if (parities.isEmpty())
			strBuilder.append("  0");
		else
			strBuilder.append("  1");
		strBuilder.append("  0"); //sss
		strBuilder.append("  0"); //xxx
		strBuilder.append("  0"); //rrr
		strBuilder.append("  0"); //ppp
		strBuilder.append("  0"); //iii
		strBuilder.append("999"); //mmm
		strBuilder.append(" V2000"); //vvvvvv
		strBuilder.append(endLine);
		
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
		strBuilder.append(" ");
		//aaa
		addString(atom.getElName(),3); 
		//dd not specified yet
		strBuilder.append(" 0"); 
		//ccc
		if (atom.getRadical() == InchiRadical.DOUBLET)
			strBuilder.append("  4"); //MDL code for doublet radical
		else
			addInteger(getOldCTABChargeCoding(atom.getCharge()),3);
		//sss stereo parity
		if (parity != null) {
			switch (parity) {
			case ODD:
				strBuilder.append("  1");
				break;
			case EVEN:
				strBuilder.append("  2");
				break;
			case UNKNOWN:
				strBuilder.append("  3");
				break;		
			default:
				strBuilder.append("  0");
			}
		}
		//hhh: implicit H atoms: used for query 
		//addInteger(getImplicitHAtomCoding(atom),3);
		strBuilder.append("  0");
		//bbb stereo box care: used for queries
		strBuilder.append("  0");
		//vvv valence
		strBuilder.append("  0");
		//HHH not specified
		strBuilder.append("  0");
		
		//rrriiimmmnnneee are not specified
		strBuilder.append(endLine);
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
		strBuilder.append("  0");
		//rrr (bond topology, used only for SSS)
		strBuilder.append("  0");
		//ccc (reacting center status): 0 - unmarked 
		strBuilder.append("  0");
		strBuilder.append(endLine);
	}
	
	private void addPropertyBlock(RinchiInputComponent ric) {
		List<Integer> atomList;		
		
		//Add charges
		atomList = getAtomsWithCharge(ric);		
		if (!atomList.isEmpty()) {
			//Atom charges are added in sets of 8 atoms (M  CHGnn8 aaa vvv ...)
			int numSets = atomList.size() / 8;
			for (int curSet = 0; curSet < numSets; curSet++) {
				strBuilder.append("M  CHG  8");
				for (int i = 0; i < 8; i++) {
					int atIndex = atomList.get(curSet * 8 + i);
					addInteger(atIndex+1, 4);
					addInteger(ric.getAtom(atIndex).getCharge(), 4);
				}
				strBuilder.append(endLine);
			}			
			//One additional set of k charged atoms (atomList.size() = 8 * numSets + k)
			int k = atomList.size() % 8;
			strBuilder.append("M  CHG");
			addInteger(k,3);
			for (int i = 0; i < k; i++) {
				int atIndex = atomList.get(numSets * 8 + i);
				addInteger(atIndex+1, 4);
				addInteger(ric.getAtom(atIndex).getCharge(), 4);
			}
			strBuilder.append(endLine);
		}
		
		//Add isotope masses
		atomList = getAtomsWithIsotope(ric); 
		if (!atomList.isEmpty()) {
			//Atom isotope masses are added in sets of 8 atoms (M  ISOnn8 aaa vvv ...)
			int numSets = atomList.size() / 8;
			for (int curSet = 0; curSet < numSets; curSet++) {
				strBuilder.append("M  ISO  8");
				for (int i = 0; i < 8; i++) {
					int atIndex = atomList.get(curSet * 8 + i);
					addInteger(atIndex+1, 4);
					addInteger(ric.getAtom(atIndex).getIsotopicMass(), 4);
				}
				strBuilder.append(endLine);
			}			
			//One additional set of k isotope masses (atomList.size() = 8 * numSets + k)
			int k = atomList.size() % 8;
			strBuilder.append("M  ISO");
			addInteger(k,3);
			for (int i = 0; i < k; i++) {
				int atIndex = atomList.get(numSets * 8 + i);
				addInteger(atIndex+1, 4);
				addInteger(ric.getAtom(atIndex).getIsotopicMass(), 4);
			}
			strBuilder.append(endLine);
		}
		
		//Add radicals
		atomList = getAtomsWithRadical(ric);
		if (!atomList.isEmpty()) {
			//Atom radicals are added in sets of 8 atoms (M  RADnn8 aaa vvv ...)
			int numSets = atomList.size() / 8;
			for (int curSet = 0; curSet < numSets; curSet++) {
				strBuilder.append("M  RAD  8");
				for (int i = 0; i < 8; i++) {
					int atIndex = atomList.get(curSet * 8 + i);
					addInteger(atIndex+1, 4);
					int radCode = getRadicalMDLCode(ric.getAtom(atIndex).getRadical());
					addInteger(radCode, 4);
				}
				strBuilder.append(endLine);
			}			
			//One additional set of k charged atoms (atomList.size() = 8 * numSets + k)
			int k = atomList.size() % 8;
			strBuilder.append("M  RAD");
			addInteger(k,3);
			for (int i = 0; i < k; i++) {
				int atIndex = atomList.get(numSets * 8 + i);
				addInteger(atIndex+1, 4);
				int radCode = getRadicalMDLCode(ric.getAtom(atIndex).getRadical());
				addInteger(radCode, 4);
			}
			strBuilder.append(endLine);
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
	
	private InchiRadical getInchiRadical(int mdlRadicalCode) {
		switch (mdlRadicalCode) {
		case 1:
			return InchiRadical.SINGLET;
		case 2:
			return InchiRadical.DOUBLET;
		case 3:
			return InchiRadical.TRIPLET;	
		}
		return InchiRadical.NONE;
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
		};
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
	
	private InchiBondStereo getBondStereoFromMDLCode(int code) {
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
		}
		return null;
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
			strBuilder.append(vStr.substring(fixedSpace));
		else {
			if (spacesAtTheEnd) {
				strBuilder.append(vStr);
				for (int i = 0; i < nEmptySpaces; i++)
					strBuilder.append(" ");
			} else {
				for (int i = 0; i < nEmptySpaces; i++)
					strBuilder.append(" ");
				strBuilder.append(vStr);
			}
		}
	}
	
	private void addInteger(int value, int fixedSpace) {
		String vStr = Integer.toString(value);
		if (vStr.length() > fixedSpace)
			vStr = "0";
		//Adding empty spaces and value
		int nEmptySpaces = fixedSpace - vStr.length();
		for (int i = 0; i < nEmptySpaces; i++)
			strBuilder.append(" ");
		strBuilder.append(vStr);
	}
	
	private void addDouble(Double value) {
		addDouble(value, mdlNumberFormat, MDL_FLOAT_SPACES);
	}
	
	private void addDouble(Double value, NumberFormat nf, int fixedSpace) {
		String vStr;
		if(Double.isNaN(value) || Double.isInfinite(value))
			vStr = nf.format(0.0);
		else
			vStr = nf.format(value);
		
		if (vStr.length() > fixedSpace)
			vStr = "0";
		//Adding empty spaces and value
		int nEmptySpaces = fixedSpace - vStr.length();
		for (int i = 0; i < nEmptySpaces; i++)
			strBuilder.append(" ");
		strBuilder.append(vStr);
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
	
	private int getChargeFromOldCTABCoding(int code) {
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
		}
		return 0;
	}
	
	private int getImplicitHAtomCoding(InchiAtom atom) {
		//Implicit H atoms coding: 1 = H0, 2 = H1, 3 = H2, 4 = H3, 5 = H4
		if (atom.getImplicitHydrogen() == 0)
			return 0;
		else
			return atom.getImplicitHydrogen() + 1; 
	}
			
	private String readLine() {
		String line = null;
		curLineNum++;
		try {
			line = inputReader.readLine();
		}
		catch (Exception x) {
			errors.add("Unable to read line " + curLineNum + ": " + x.getMessage());
		}
		return line; 
	}
	
	private int iterateInputLines() {
		//Handle file header/headers according to format
		switch(format) {
		case AUTO:
			readAutoFileHeader();
			break;
		case RXN:
			readRXNFileHeader(true);
			break;
		case RD:
			readRDFileHeader(true);
			readRXNFileHeader(true);
			break;	
		}		
		if (!errors.isEmpty())
			return errors.size();
		
		readRXNCountLine();
		if (!errors.isEmpty())
			return errors.size();
		
		//Reading reagents
		for (int i = 0; i< numOfReagentsToRead; i++) {
			RinchiInputComponent ric = readMDLMolecule(true);
			errorComponentContext = "Reading reagent #" + (i+1) + " ";
			if (ric != null) {
				MoleculeUtils.setImplicitHydrogenAtoms(ric);
				ric.setRole(ReactionComponentRole.REAGENT);
				rInput.addComponent(ric);
			}
			else
				return errors.size();
		}
		
		//Reading products
		for (int i = 0; i< numOfProductsToRead; i++) {
			RinchiInputComponent ric = readMDLMolecule(true);
			errorComponentContext = "Reading product #" + (i+1) + " ";
			if (ric != null) {
				MoleculeUtils.setImplicitHydrogenAtoms(ric);
				ric.setRole(ReactionComponentRole.PRODUCT);
				rInput.addComponent(ric);
			}
			else
				return errors.size();
		}
		
		if ((format == ReactionFileFormat.RD) || 
				(format == ReactionFileFormat.AUTO && autoRecognizedformat == ReactionFileFormat.RD)) 
			iterateAgentsDataLines();
				
		return 0;
	};
	
	private void iterateAgentsDataLines() {		
		String line;
		int nAgents = 0;
		while ((line = readLine()) != null) {
			if (line.startsWith("$DATUM ")) {
				String line1 = line.substring(7).trim();
				if (line1.startsWith("$MFMT"));{
					errorComponentContext = "Reading agent #" + (nAgents+1) + " ";
					RinchiInputComponent ric = readMDLMolecule(false);
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
	
	private void readAutoFileHeader() {
		String line = readLine();
		if (line == null || ( !line.startsWith("$RDFILE") && !line.startsWith("$RXN")) ) {
			errors.add("RXN/RDFile Header: Line " + curLineNum + " is missing or does not start with $RXN or $RDFILE");
			return;
		}
		
		if (line.startsWith("$RDFILE")) {
			readRDFileHeader(false);
			readRXNFileHeader(true);
		}	
		else  //line starts With "$RXN"
			readRXNFileHeader(false);
	}
	
	private void readRDFileHeader(boolean readRDFILELine) {
		String line = readLine();
		if (readRDFILELine) {
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
			return;
		}
	}
	
	private void readRXNFileHeader(boolean readRXNLine) {
		String line = readLine();
		if (readRXNLine) {
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
			return;
		}
	}
	
	private void readRXNCountLine() {
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
		}
		else
			numOfReagentsToRead = rrr;
		Integer ppp = readInteger(line, 3, 3);
		if (ppp == null || ppp < 0) {
			errors.add("RXN counts (rrrppp) Line  " + curLineNum + " : incorrect number of reagents (ppp part): " + line);
			return;
		}
		else
			numOfProductsToRead = ppp;
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
			return;
		}
	}
	
	private void readMOLCountsLine() {
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
		}
		else
			numOfAtomsToRead = aaa;
		Integer bbb = readInteger(line, 3, 3);
		if (bbb == null || bbb < 0) {
			errors.add("MOL counts (aaabbblll...) Line  " + curLineNum 
					+ " : incorrect number of bonds (bbb part): " + line);
			return;
		}
		else
			numOfBondsToRead = bbb;
	}
	
	private void readMOLCTABBlock(RinchiInputComponent ric) {
		Map<InchiAtom, InchiStereoParity> parities = new HashMap<>();
		
		for (int i = 0; i < numOfAtomsToRead; i++) {
			readMOLAtomLine(i, ric, parities);
			if (!errors.isEmpty())
				return;
		}
		for (int i = 0; i < numOfBondsToRead; i++) {
			readMOLBondLine(i, ric);
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
	
	private void readMOLAtomLine(int atomIndex, RinchiInputComponent ric, Map<InchiAtom,InchiStereoParity> parities) {
		//Read MDL atom line
		//xxxxx.xxxxyyyyy.yyyyzzzzz.zzzz aaaddcccssshhhbbbvvvHHHrrriiimmmnnneee
		String line = readLine();
		if (line == null) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " is missing !");
			return;
		}
		Double coordX = readMDLCoordinate(line, 0);
		if (coordX == null) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " coordinate x error --> " + line);
			return;
		}
		Double coordY = readMDLCoordinate(line, 10);
		if (coordY == null) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " coordinate y error --> " + line);
			return;
		}
		Double coordZ = readMDLCoordinate(line, 20);
		if (coordZ == null) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " coordinate z error --> " + line);
			return;
		}
		String atSymbol = readString(line, 30, 4); //length 4 for: ' ' + aaa
		if (atSymbol == null) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " atom symbol error --> " + line);
			return;
		}
		
		//Check atom symbol
		int atNum = PerTable.getAtomicNumberFromElSymbol(atSymbol);
		if (atNum == -1) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " atom symbol error --> " + line);
			return;
		}
		
		//Check old CTAB charge style
		Integer chCode = readInteger(line, 36, 3);
		if (chCode == null || chCode < 0 || chCode > 7 ) {
			errors.add(errorComponentContext + "MOL atom # " + (atomIndex + 1) 
					+ " in Line " + curLineNum + " atom charge coding error --> " + line);
			return;
		}
		int charge = getChargeFromOldCTABCoding(chCode);
		
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
		}
		return null;
	}
	
	private void readMOLBondLine(int bondIndex, RinchiInputComponent ric) {
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
		if (sss == null ) {
			errors.add("MOL counts (111222ttt...) Line  " + curLineNum 
					+ " : incorrect bond stereo (sss part): " + line);
			return;
		}
		InchiBondStereo ibs = getBondStereoFromMDLCode(sss);
		if (ibs == null) {
			errors.add("MOL counts (111222ttt...) Line  " + curLineNum 
					+ " : incorrect bond stereo (sss part): " + line);
			return;
		}
		
		InchiBond bond = new InchiBond(ric.getAtom(a1-1), ric.getAtom(a2-1), InchiBondType.of((byte)ttt.intValue()), ibs);
		ric.addBond(bond);
	}
	
	private void readMOLPropertiesBlock(RinchiInputComponent ric) {
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
						+ " : incorrect atom index for (aaa vvv) pair #" + (i+1) + " in line: " + line);
				return -2;
			}
			pos += 4;
			// vvv
			Integer mass = readInteger(line, pos, 4);
			if (mass == null || mass < 1 ) {
				errors.add("M ISO molecule property Line (M  ISOnn8 aaa vvv ...) " + curLineNum 
						+ " : incorrect mass for (aaa vvv) pair #" + (i+1) + " in line: " + line);
				return -3;
			}
			pos += 4;
			ric.getAtom(atomIndex-1).setIsotopicMass(mass);
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
						+ " : incorrect atom index for (aaa vvv) pair #" + (i+1) + " in line: " + line);
				return -2;
			}
			pos += 4;
			// vvv
			Integer charge = readInteger(line, pos, 4);
			if (charge == null || charge < -15 || charge > 15) {
				errors.add("M CHG molecule property Line (M  ISOnn8 aaa vvv ...) " + curLineNum 
						+ " : incorrect charge for (aaa vvv) pair #" + (i+1) + " in line: " + line);
				return -3;
			}
			pos += 4;
			ric.getAtom(atomIndex-1).setCharge(charge);
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
						+ " : incorrect atom index for (aaa vvv) pair #" + (i+1) + " in line: " + line);
				return -2;
			}
			pos += 4;
			// vvv
			Integer radCode = readInteger(line, pos, 4);
			if (radCode == null || radCode < 0 || radCode > 3) {
				errors.add("M RAD molecule property Line (M  RADnn8 aaa vvv ...) " + curLineNum 
						+ " : incorrect radical value for (aaa vvv) pair #" + (i+1) + " in line: " + line);
				return -3;
			}
			pos += 4;
			InchiRadical radical = getInchiRadical(radCode);
			ric.getAtom(atomIndex-1).setRadical(radical);
		}		
		return 0;
	}
	
	private RinchiInputComponent readMDLMolecule(boolean readMOLline) {
		RinchiInputComponent ric = new RinchiInputComponent();
		readMolHeader(readMOLline);
		if (!errors.isEmpty())
			return null;
		
		readMOLCountsLine();
		if (!errors.isEmpty())
			return null;
		
		readMOLCTABBlock(ric);
		if (!errors.isEmpty())
			return null;
		
		readMOLPropertiesBlock(ric);
		if (!errors.isEmpty())
			return null;
		
		return ric;
	}
		
	private String readString(String line, int startPos, int lenght) {
		int endPos = startPos + lenght;
		if (startPos > line.length() || endPos > line.length())
			return null;
		String s = line.substring(startPos, endPos).trim();
		return s;
	}
	
	private Integer readInteger(String line, int startPos, int lenght) {
		int endPos = startPos + lenght;
		if (startPos > line.length() || endPos > line.length())
			return null;
		String s = line.substring(startPos, endPos).trim();
		try {
			int i = Integer.parseInt(s);
			return i;
		}
		catch(Exception x) {
			errors.add(errorPrefix() + "Error on parsing integer: " + s);
			return null;
		}
	}
	
	private Double readMDLCoordinate(String line, int startPos) {
		int endPos = startPos + MDL_FLOAT_SPACES;
		if (startPos > line.length() || endPos > line.length())
			return null;
		
		String s = line.substring(startPos, endPos).trim();
		if (line.charAt(startPos + 5) != '.') {
			errors.add(errorPrefix() + "Incorrect coordinate format: " + s);
			return null;
		}	
		
		try {
			double d = Double.parseDouble(s);
			return d;
		}
		catch(Exception x) {
			errors.add(errorPrefix() + "Error on parsing float: " + s);
			return null;
		}
	}
	
	private String errorPrefix() {
		return "Line " + curLineNum + ": "; 
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
	 * Gets the value of flag checkParityAccordingAtomNumbering.
	 */
	public boolean isCheckParityAccordingAtomNumbering() {
		return checkParityAccordingAtomNumbering;
	}	
	
	/**
	 * Sets the value of flag: checkParityAccordingAtomNumbering.
	 * 
	 * @param checkParityAccordingAtomNumbering flag value to be set.
	 */
	public void setCheckParityAccordingAtomNumbering(boolean checkParityAccordingAtomNumbering) {
		this.checkParityAccordingAtomNumbering = checkParityAccordingAtomNumbering;
	}

	/**
	 * Gets the value of flag guessTetrahedralChiralityFromBondsInfo.
	 */
	public boolean isGuessTetrahedralChiralityFromBondsInfo() {
		return guessTetrahedralChiralityFromBondsInfo;
	}
	
	
	/**
	 * Gets the value of flag guessTetrahedralChiralityFromBondsInfo.
	 * 
	 * @param guessTetrahedralChiralityFromBondsInfo flag value to be set.
	 */
	public void setGuessTetrahedralChiralityFromBondsInfo(boolean guessTetrahedralChiralityFromBondsInfo) {
		this.guessTetrahedralChiralityFromBondsInfo = guessTetrahedralChiralityFromBondsInfo;
	}
		
}
