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

import io.github.dan2097.jnarinchi.RinchiOptions.RinchiKeyType;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;


public class JnaRinchiTest 
{
	@Test 
	public void testDummy() {
		assertTrue(true);
	}
	
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
	
	public static void genericExampleTest(String reactionFile, String rinchiFile) {
		String reactText = readReactionFromResourceFile(reactionFile);
		assertTrue(reactText != null);
		RinchiFullInfo rfi = readRinchiFullInfoFromResourceFile(rinchiFile);
		assertTrue(rfi != null);
		
		//Generate RInChI from mol file
		//RInChI examples works with option: forceEquilibrium = false (which is by default);		
		RinchiOutput rinchiOut = JnaRinchi.fileTextToRinchi(reactText);
		assertEquals(rfi.getRinchi(), rinchiOut.getRinchi(), "Rinchi for " + reactionFile);
		assertEquals(rfi.getAuxInfo(), rinchiOut.getAuxInfo(), "RAuxInfo for " + reactionFile);
		
		//Generate Long-RinchiKey from RInChI
		RinchiKeyOutput rinchiKeyOut = JnaRinchi.rinchiKeyFromRinchi(RinchiKeyType.LONG, rinchiOut.getRinchi());
		assertEquals(rfi.getRinchiKeyLong(), rinchiKeyOut.getRinchiKey(), "Long-RinchiKey for " + reactionFile 
				+ " generated from RInChI" );
		
		//Generate Short-RinchiKey from RInChI
		rinchiKeyOut = JnaRinchi.rinchiKeyFromRinchi(RinchiKeyType.SHORT, rinchiOut.getRinchi());
		assertEquals(rfi.getRinchiKeyShort(), rinchiKeyOut.getRinchiKey(), "Short-RinchiKey for " + reactionFile 
				+ " generated from RInChI" );
		
		//Generate Web-RinchiKey from RInChI
		rinchiKeyOut = JnaRinchi.rinchiKeyFromRinchi(RinchiKeyType.WEB, rinchiOut.getRinchi());
		assertEquals(rfi.getRinchiKeyWeb(), rinchiKeyOut.getRinchiKey(), "Web-RinchiKey for " + reactionFile 
				+ " generated from RInChI" );
	}
	
	
	@Test 
	public void testExamples() {
		genericExampleTest("examples/1_reactant_-_A.rxn", "examples/1_reactant_-_A.txt");
		//genericExampleTest("examples/R-_-A.rxn", "examples/R-_-A.txt");
		
	}
	
}
