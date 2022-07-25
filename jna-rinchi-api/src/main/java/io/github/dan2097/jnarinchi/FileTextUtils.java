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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.github.dan2097.jnainchi.InchiAtom;

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
	private boolean add_M_ISO_Line = false;
	private boolean add_M_CHG_Line = false;
	private CTABVersion ctabVersion = CTABVersion.V2000; //Currently only V2000 is supported
	private RinchiInput rInput = null;
	private List<RinchiInputComponent> reagents = new ArrayList<RinchiInputComponent>();
	private List<RinchiInputComponent> products = new ArrayList<RinchiInputComponent>();
	private List<RinchiInputComponent> agents = new ArrayList<RinchiInputComponent>();
	
		
	public String rinchiInputToFileText(RinchiInput rInp) {
		this.rInput = rInp;
		if (rInput == null) {
			errors.add("RinchiInput is null!");
			return null;
		}
		
		reset();
		analyzeComponents();
		
		if (format == ReactionFileFormat.RD || format == ReactionFileFormat.AUTO )
			addRDFileHeader();
		
		//TODO
		
		return strBuilder.toString();
	}
	
	private void reset() {
		strBuilder = new StringBuilder();
		errors.clear();
		reagents.clear();
		products.clear();
		agents.clear();
	}
	
	private void addRrinchiInputComponentToMolFile(RinchiInputComponent ric, String info1, String info2) 
	{
		addMolHeader(info1, info2);
		//TODO
	}
	
	private void addMolHeader(String info1, String info2) {
		strBuilder.append("$MOL");
		strBuilder.append(endLine);
		strBuilder.append(info1);
		strBuilder.append(endLine);
		strBuilder.append("  ");
		strBuilder.append(info2);
		strBuilder.append(endLine);
		strBuilder.append(endLine);
	}
	
	private void addRDFileHeader() {
		//TODO
	}
	
	private void addCTABBlockV2000(RinchiInputComponent ric) {
		//Counts line: aaabbblllfffcccsssxxxrrrpppiiimmmvvvvvv
		addInteger(ric.getAtoms().size(), 3); //aaa
		addInteger(ric.getBonds().size(), 3); //bbb
		strBuilder.append("  0"); //lll
		strBuilder.append("  0"); //fff
		addInteger(ric.getStereos().size(), 3); //ccc
		strBuilder.append("  0"); //sss
		strBuilder.append("  0"); //xxx
		strBuilder.append("  0"); //rrr
		strBuilder.append("  0"); //ppp
		strBuilder.append("  0"); //iii
		strBuilder.append("999"); //mmm
		strBuilder.append(" V2000"); //vvvvvv
		strBuilder.append(endLine);
		
	}
	
	private void addAtomLine(InchiAtom atom) {
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
		addInteger(getOldCTABChargeCoding(atom.getCharge()),3);
		//sss stereoparity not specified yet
		strBuilder.append("  0"); 
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
	
	private int getImplicitHAtomCoding(InchiAtom atom) {
		//Implicit H atoms coding: 1 = H0, 2 = H1, 3 = H2, 4 = H3, 5 = H4
		if (atom.getImplicitHydrogen() == 0)
			return 0;
		else
			return atom.getImplicitHydrogen() + 1; 
	}	
	
	
	public List<String> getErrors() {
		return errors;
	}

	public ReactionFileFormat getFormat() {
		return format;
	}

	public void setFormat(ReactionFileFormat format) {
		if (format != null)
			this.format = format;
	}

	public boolean isAdd_M_ISO_Line() {
		return add_M_ISO_Line;
	}

	public void setAdd_M_ISO_Line(boolean add_M_ISO_Line) {
		this.add_M_ISO_Line = add_M_ISO_Line;
	}

	public boolean isAdd_M_CHG_Line() {
		return add_M_CHG_Line;
	}

	public void setAdd_M_CHG_Line(boolean add_M_CHG_Line) {
		this.add_M_CHG_Line = add_M_CHG_Line;
	}
		
}
