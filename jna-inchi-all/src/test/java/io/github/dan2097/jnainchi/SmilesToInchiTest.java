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
package io.github.dan2097.jnainchi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

public class SmilesToInchiTest {

  @ParameterizedTest
  @CsvFileSource(resources = "smilesintegrationtests.smi", delimiter = '\t')
  public void testConversion(String smiles, String expectedInchi) throws IOException {
    String inchi = SmilesToInchi.toInchi(smiles).getInchi();
    if (inchi != null) {
      assertEquals(expectedInchi, inchi, smiles + " was misconverted as: " + inchi);
    } else {
      fail(smiles + " was not converted");
    }
  }
}
