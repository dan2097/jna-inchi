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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


/**
 * Testing the main functionality of jna-rinchi-api module
 *
 */
public class JnaRinchiTest 
{
		
	/**
	 * Reading a reaction from a resource text file into a text string
	 * 
	 * @param fileName reaction text file name
	 * @return reaction as a string
	 */
	public static String readReactionFromResourceFile(String fileName) {
		StringBuilder sb = new StringBuilder();
		try (InputStream is = JnaRinchiTest.class.getResourceAsStream(fileName);
				BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line).append("\n");
			}
			return sb.toString();
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Reading the full RInChI information from a text file.
	 * 
	 * @param fileName text file name
	 * @return RinchiFullInfo object with the full RInChI information
	 */
	public static RinchiFullInfo readRinchiFullInfoFromResourceFile(String fileName) {
		try (InputStream is = JnaRinchiTest.class.getResourceAsStream(fileName)) {
			Properties props = new Properties();
			props.load(is);
			RinchiFullInfo rfi = new RinchiFullInfo();
			String s;
			s = props.getProperty("RInChI");
			if (s != null)
				rfi.setRinchi("RInChI=" + s);			
			s = props.getProperty("RAuxInfo");
			if (s != null)
				rfi.setAuxInfo("RAuxInfo=" + s);
			s = props.getProperty("Long-RInChIKey");
			if (s != null)
				rfi.setRinchiKeyLong("Long-RInChIKey=" + s);			
			s = props.getProperty("Short-RInChIKey");
			if (s != null)
				rfi.setRinchiKeyShort("Short-RInChIKey=" + s);
			s = props.getProperty("Web-RInChIKey");
			if (s != null)
				rfi.setRinchiKeyWeb("Web-RInChIKey=" + s);
			return rfi;
		}
		catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Generic test with example data provided by IUPAC RInChI developers.
	 * The test compares the generated RInChI, RAuxInfo and RInChI-Keys with the
	 * supplied ones.
	 * 
	 * @param reactionFile the reaction text file in  RXN or RDFile format
	 * @param rinchiFile text file with all RInCHI information
	 */
	public static void genericExampleTest(String reactionFile, String rinchiFile) {
		String reactText = readReactionFromResourceFile(reactionFile);
		assertTrue(reactText != null, "Reading reaction from text file " + reactionFile);
		RinchiFullInfo rfi = readRinchiFullInfoFromResourceFile(rinchiFile);
		assertTrue(rfi != null, "Reading Rinchi Full Infor from file " + rinchiFile);
		
		RinchiOutput rinchiOut;
		RinchiKeyOutput rinchiKeyOut;
		
		//Generate RInChI from text file
		//RInChI examples works with option: forceEquilibrium = false (which is by default);		
		rinchiOut = JnaRinchi.fileTextToRinchi(reactText);
		assertEquals(rfi.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
		assertEquals(rfi.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);
		
		//Generate Long-RinchiKey from text file
		rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.LONG);
		assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile);
		
		//Generate Short-RinchiKey from text file
		rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.SHORT);
		assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile);
				
		//Generate Web-RinchiKey from text file
		rinchiKeyOut = JnaRinchi.fileTextToRinchiKey(reactText, RinchiKeyType.WEB);
		assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile);

		//Generate Long-RinchiKey from RInChI
		rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.LONG, rinchiOut.getRinchi());
		assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile 
				+ " generated from RInChI" );
		
		//Generate Short-RinchiKey from RInChI
		rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.SHORT, rinchiOut.getRinchi());
		assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile 
				+ " generated from RInChI" );
		
		//Generate Web-RinchiKey from RInChI
		rinchiKeyOut = JnaRinchi.rinchiToRinchiKey(RinchiKeyType.WEB, rinchiOut.getRinchi());
		assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile 
				+ " generated from RInChI" );
		
	}
	
	/**
	 * Performs testing of the conversion from RInChI to MDL RXN/RDFile and vice versa simultaneously.
	 * 
	 * @param reactionFile the reaction text file in  RXN or RDFile format
	 * @param rinchiFile text file with all RInCHI information
	 * @param format reaction file format
	 */
	public static void doubleConversionExampleTest(String reactionFile, String rinchiFile, ReactionFileFormat format) {
		//Double conversion test RIChI --> file text --> RInChI
		String reactText = readReactionFromResourceFile(reactionFile);
		assertTrue(reactText != null, "Reading reaction from text file " + reactionFile);
		RinchiFullInfo rfi = readRinchiFullInfoFromResourceFile(rinchiFile);
		assertTrue(rfi != null, "Reading Rinchi Full Infor from file " + rinchiFile);
		
		RinchiOutput rinchiOut0, rinchiOut;
		
		FileTextOutput fileTextOut = JnaRinchi.rinchiToFileText(rfi.getRinchi(), rfi.getAuxInfo(), format);
		assertTrue(fileTextOut.getStatus() == FileTextStatus.SUCCESS, "RIChI to FileText conversion status for " + rinchiFile);
		rinchiOut = JnaRinchi.fileTextToRinchi(fileTextOut.getReactionFileText());
		assertEquals(rfi.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
		assertEquals(rfi.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);
				
		//Testing number of components and decomposed InChIs
		rinchiOut0 = JnaRinchi.fileTextToRinchi(reactText);
		assertEquals(rinchiOut0.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
		assertEquals(rinchiOut0.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);
	}
	
	
	@Test 
	public void testCheckLibrary() {
		boolean checkLib = true;
		String errMsg = "";
		try {
			JnaRinchi.getJnaRinchiVersion();
		}
		catch (Exception x) {
			checkLib = false;
			errMsg = x.getMessage();
		}
		assertTrue(checkLib, errMsg);
	}
		
	@Test 
	public void testExample_1_reactant_A() {
		genericExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt");
		doubleConversionExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_1_reactant_no_product() {
		genericExampleTest("examples/1_reactant_-_no_product.rxn", "examples/1_reactant_-_no_product.txt");
		doubleConversionExampleTest("examples/1_reactant_-_no_product.rxn", "examples/1_reactant_-_no_product.txt",ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/1_reactant_-_no_product.rxn", "examples/1_reactant_-_no_product.txt",ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_1_reactant_no_structure() {
		genericExampleTest("examples/1_reactant_-_no_structure.rxn", "examples/1_reactant_-_no_structure.txt");
		doubleConversionExampleTest("examples/1_reactant_-_no_structure.rxn", "examples/1_reactant_-_no_structure.txt",ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/1_reactant_-_no_structure.rxn", "examples/1_reactant_-_no_structure.txt",ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_1_reactant_R() {
		genericExampleTest("examples/1_reactant_-_R.rxn", "examples/1_reactant_-_R.txt");
		doubleConversionExampleTest("examples/1_reactant_-_R.rxn", "examples/1_reactant_-_R.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/1_reactant_-_R.rxn", "examples/1_reactant_-_R.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_1_reactant_X() {
		genericExampleTest("examples/1_reactant_-_X.rxn", "examples/1_reactant_-_X.txt");
		doubleConversionExampleTest("examples/1_reactant_-_X.rxn", "examples/1_reactant_-_X.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/1_reactant_-_X.rxn", "examples/1_reactant_-_X.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Esterification_01_flat() {
		genericExampleTest("examples/Esterification_01_flat.rdf", "examples/Esterification_01_flat.txt");
		//Double conversion with RXN format fails! 
		//doubleConversionExampleTest("examples/Esterification_01_flat.rdf", "examples/Esterification_01_flat.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Esterification_01_flat.rdf", "examples/Esterification_01_flat.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Esterification_01() {
		genericExampleTest("examples/Esterification_01.rdf", "examples/Esterification_01.txt");
		//Double conversion with RXN format fails!
		//doubleConversionExampleTest("examples/Esterification_01.rdf", "examples/Esterification_01.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Esterification_01.rdf", "examples/Esterification_01.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Esterification_02() {
		genericExampleTest("examples/Esterification_02.rdf", "examples/Esterification_02.txt");
		//Double conversion with RXN format fails!
		//doubleConversionExampleTest("examples/Esterification_02.rdf", "examples/Esterification_02.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Esterification_02.rdf", "examples/Esterification_02.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Esterification_03() {
		genericExampleTest("examples/Esterification_03.rdf", "examples/Esterification_03.txt");
		//Double conversion with RXN format fails!
		//doubleConversionExampleTest("examples/Esterification_03.rdf", "examples/Esterification_03.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Esterification_03.rdf", "examples/Esterification_03.txt", ReactionFileFormat.RD);
	}

	@Test 
	public void testExample_Inverted_stereochemistry() {
		genericExampleTest("examples/Inverted_stereochemistry.rxn", "examples/Inverted_stereochemistry.txt");
		doubleConversionExampleTest("examples/Inverted_stereochemistry.rxn", "examples/Inverted_stereochemistry.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Inverted_stereochemistry.rxn", "examples/Inverted_stereochemistry.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Multiplesteps() {
		genericExampleTest("examples/Multiplesteps.rdf", "examples/Multiplesteps.txt");
		//Double conversion with RXN format fails!
		//doubleConversionExampleTest("examples/Multiplesteps.rdf", "examples/Multiplesteps.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Multiplesteps.rdf", "examples/Multiplesteps.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_No_reactant_1_product_02() {
		genericExampleTest("examples/No_reactant_-_1_product_02.rxn", "examples/No_reactant_-_1_product_02.txt");
		doubleConversionExampleTest("examples/No_reactant_-_1_product_02.rxn", "examples/No_reactant_-_1_product_02.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/No_reactant_-_1_product_02.rxn", "examples/No_reactant_-_1_product_02.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_No_reactant_1_product() {
		genericExampleTest("examples/No_reactant_-_1_product.rxn", "examples/No_reactant_-_1_product.txt");
		doubleConversionExampleTest("examples/No_reactant_-_1_product.rxn", "examples/No_reactant_-_1_product.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/No_reactant_-_1_product.rxn", "examples/No_reactant_-_1_product.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_No_reactant_no_product() {
		genericExampleTest("examples/No_reactant_-_no_product.rdf", "examples/No_reactant_-_no_product.txt");
		//Double conversion with RXN format fails!
		//doubleConversionExampleTest("examples/No_reactant_-_no_product.rdf", "examples/No_reactant_-_no_product.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/No_reactant_-_no_product.rdf", "examples/No_reactant_-_no_product.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_no_structure_1_product() {
		genericExampleTest("examples/no_structure_-_1_product.rxn", "examples/no_structure_-_1_product.txt");
		doubleConversionExampleTest("examples/no_structure_-_1_product.rxn", "examples/no_structure_-_1_product.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/no_structure_-_1_product.rxn", "examples/no_structure_-_1_product.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_No_Structure_0_02() {
		genericExampleTest("examples/No_Structure_0-02.rdf", "examples/No_Structure_0-02.txt");
		//Double conversion with RXN format fails!
		//doubleConversionExampleTest("examples/No_Structure_0-02.rdf", "examples/No_Structure_0-02.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/No_Structure_0-02.rdf", "examples/No_Structure_0-02.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Nnostruct_X() {
		genericExampleTest("examples/nostruct_-_X.rxn", "examples/nostruct_-_X.txt");
		doubleConversionExampleTest("examples/nostruct_-_X.rxn", "examples/nostruct_-_X.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/nostruct_-_X.rxn", "examples/nostruct_-_X.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_R__A() {
		genericExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt");
		doubleConversionExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_RingOpening01() {
		genericExampleTest("examples/RingOpening01.rxn", "examples/RingOpening01.txt");
		doubleConversionExampleTest("examples/RingOpening01.rxn", "examples/RingOpening01.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/RingOpening01.rxn", "examples/RingOpening01.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_star_star__nostruct() {
		genericExampleTest("examples/star_star_-_nostruct.rxn", "examples/star_star_-_nostruct.txt");
		doubleConversionExampleTest("examples/star_star_-_nostruct.rxn", "examples/star_star_-_nostruct.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/star_star_-_nostruct.rxn", "examples/star_star_-_nostruct.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Styrene__Polystyrene_as_no_struct() {
		genericExampleTest("examples/Styrene_-_Polystyrene_as_no-struct.rxn", "examples/Styrene_-_Polystyrene_as_no-struct.txt");
		doubleConversionExampleTest("examples/Styrene_-_Polystyrene_as_no-struct.rxn", "examples/Styrene_-_Polystyrene_as_no-struct.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Styrene_-_Polystyrene_as_no-struct.rxn", "examples/Styrene_-_Polystyrene_as_no-struct.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_Tautomerization_01() {
		genericExampleTest("examples/Tautomerization_01.rxn", "examples/Tautomerization_01.txt");
		doubleConversionExampleTest("examples/Tautomerization_01.rxn", "examples/Tautomerization_01.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/Tautomerization_01.rxn", "examples/Tautomerization_01.txt", ReactionFileFormat.RD);
	}
	
	@Test 
	public void testExample_X__1_product() {
		genericExampleTest("examples/X_-_1_product.rxn", "examples/X_-_1_product.txt");
		doubleConversionExampleTest("examples/X_-_1_product.rxn", "examples/X_-_1_product.txt", ReactionFileFormat.RXN);
		doubleConversionExampleTest("examples/X_-_1_product.rxn", "examples/X_-_1_product.txt", ReactionFileFormat.RD);
	}
}
