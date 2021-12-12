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

import java.util.HashMap;
import java.util.Map;

import io.github.dan2097.jnainchi.inchi.InchiLibrary.tagINCHIRadical;

public enum InchiRadical {

  NONE(tagINCHIRadical.INCHI_RADICAL_NONE),
  
  SINGLET(tagINCHIRadical.INCHI_RADICAL_SINGLET),
  
  DOUBLET(tagINCHIRadical.INCHI_RADICAL_DOUBLET),
  
  TRIPLET(tagINCHIRadical.INCHI_RADICAL_TRIPLET);

  private final byte code;
  
  private InchiRadical(int code) {
    this.code = (byte) code;
  }
  
  byte getCode() {
    return code;
  }
  
  private static final Map<Byte, InchiRadical> map = new HashMap<>();
  
  static {
    for (InchiRadical val : InchiRadical.values()) {
      map.put(val.code, val);
    }
  }
  
  static InchiRadical of(byte code) {
    return map.get(code);
  }

}