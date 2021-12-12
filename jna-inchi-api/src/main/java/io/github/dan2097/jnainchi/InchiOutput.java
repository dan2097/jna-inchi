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

public class InchiOutput {
  
  private final String inchi;
  private final String auxInfo;
  private final String message;
  private final String log;
  private final InchiStatus status;

  InchiOutput(String inchi, String auxInfo, String message, String log, InchiStatus status) {
    this.inchi = inchi;
    this.auxInfo = auxInfo;
    this.message = message;
    this.log = log;
    this.status = status;
  }

  public String getInchi() {
    return inchi;
  }

  public String getAuxInfo() {
    return auxInfo;
  }

  public String getMessage() {
    return message;
  }

  public String getLog() {
    return log;
  }

  public InchiStatus getStatus() {
    return status;
  }
  
  @Override
  public String toString() {
    return inchi;
  }
}
