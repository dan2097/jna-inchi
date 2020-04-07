/**
 * JNA-InChI - Library for calling InChI from Java
 * Copyright © 2018 Daniel Lowe
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
package com.github.dan2097.jnainchi;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.junit.Test;

public class SmilesToInchiTest {

	@Test
	public void testConversion() throws IOException{
      try (BufferedReader input = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("smilesintegrationtests.smi"), "UTF-8"))){
        String line = null;
        while ((line = input.readLine()) != null) {
            if(line.isEmpty() || line.startsWith("#")){
                continue;
            }
            String[] lineArray = line.split("\t");
            String inchi = SmilesToInchi.toInchi(lineArray[0]).getInchi();
            if (inchi!=null) {
                String referenceInchi = lineArray[1];

                if (!inchi.equals(referenceInchi)){
                    fail(lineArray[0] +" was misconverted as: " + inchi);
                }
            } else {
                fail(lineArray[0] +" was not converted");
            }
        }
      }
	}

}
