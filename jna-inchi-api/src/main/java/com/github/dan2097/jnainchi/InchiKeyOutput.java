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

public class InchiKeyOutput {

  private final String inchiKey;
  private final InchiKeyStatus status;
  private final String szXtra1;
  private final String szXtra2;
  
  InchiKeyOutput(String inchiKey, InchiKeyStatus status, String szXtra1, String szXtra2) {
    this.inchiKey = inchiKey;
    this.status = status;
    this.szXtra1 = szXtra1;
    this.szXtra2 = szXtra2;
  }

  public String getInchiKey() {
    return inchiKey;
  }

  public InchiKeyStatus getStatus() {
    return status;
  }
  
  /**
   * Returns the rest of the 256-bit SHA-2 signature for the first block
   * @return
   */
  public String getBlock1HashExtension() {
    return szXtra1;
  }

  /**
   * Returns the rest of the 256-bit SHA-2 signature for the second block
   * @return
   */
  public String getBlock2HashExtension() {
    return szXtra2;
  }

}
