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

public class InchiInputFromAuxinfoOutput {
  
  private final InchiInput inchiInput;
  private final Boolean chiralFlag;
  private final String message;
  private final InchiStatus status;
  
  
  InchiInputFromAuxinfoOutput(InchiInput inchiInput, Boolean chiralFlag,  String message, InchiStatus status) {
    this.inchiInput = inchiInput;
    this.chiralFlag = chiralFlag;
    this.message = message;
    this.status = status;
  }

  public InchiInput getInchiInput() {
    return inchiInput;
  }

  /**
   * True if the structure was marked as chiral
   * False if marked as not chiral
   * null if not marked
   * @return
   */
  public Boolean getChiralFlag() {
    return chiralFlag;
  }


  public String getMessage() {
    return message;
  }


  public InchiStatus getStatus() {
    return status;
  }

}
