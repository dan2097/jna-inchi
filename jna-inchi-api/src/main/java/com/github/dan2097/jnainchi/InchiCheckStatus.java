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

import com.github.dan2097.jnainchi.inchi.InchiLibrary.tagRetValCheckINCHI;

public enum InchiCheckStatus {
  
  VALID_STANDARD(tagRetValCheckINCHI.INCHI_VALID_STANDARD),
  VALID_NON_STANDARD(tagRetValCheckINCHI.INCHI_VALID_NON_STANDARD),
  VALID_BETA(tagRetValCheckINCHI.INCHI_VALID_BETA),
  INVALID_PREFIX(tagRetValCheckINCHI.INCHI_INVALID_PREFIX),
  INVALID_VERSION(tagRetValCheckINCHI.INCHI_INVALID_VERSION),
  INVALID_LAYOUT(tagRetValCheckINCHI.INCHI_INVALID_LAYOUT),
  FAIL_I2I(tagRetValCheckINCHI.INCHI_FAIL_I2I);
  
  private final int code;
  
  private InchiCheckStatus(int code){
    this.code = code;
  }

  private static final Map<Integer, InchiCheckStatus> map = new HashMap<>();
  
  static {
    for (InchiCheckStatus val : InchiCheckStatus.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiCheckStatus of(int code) {
    return map.get(code);
  }

}
