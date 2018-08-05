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

import java.util.HashMap;
import java.util.Map;

import com.github.dan2097.jnainchi.inchi.InchiLibrary;

public enum InchiKeyStatus {
  
  /** Success; no errors or warnings*/
  OK(InchiLibrary.INCHIKEY_OK),
  
  /** Unknown program error*/
  UNKNOWN_ERROR(InchiLibrary.INCHIKEY_UNKNOWN_ERROR),
  
  /** Source string is empty*/
  EMPTY_INPUT(InchiLibrary.INCHIKEY_EMPTY_INPUT),
  
  /** Invalid InChI prefix or invalid version (not 1)*/
  INVALID_INCHI_PREFIX(InchiLibrary.INCHIKEY_INVALID_INCHI_PREFIX),
  
  /** Not enough memory*/
  NOT_ENOUGH_MEMORY(InchiLibrary.INCHIKEY_NOT_ENOUGH_MEMORY),
  
  /** Source InChI has invalid layout*/
  INVALID_INCHI(InchiLibrary.INCHIKEY_INVALID_INCHI),

  /** Source standard InChI has invalid layout*/
  INVALID_STD_INCHI(InchiLibrary.INCHIKEY_INVALID_STD_INCHI);

  private final int code;
  
  private InchiKeyStatus(int code){
    this.code = code;
  }

  private static final Map<Integer, InchiKeyStatus> map = new HashMap<>();
  
  static {
    for (InchiKeyStatus val : InchiKeyStatus.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiKeyStatus of(int code) {
    return map.get(code);
  }

}
