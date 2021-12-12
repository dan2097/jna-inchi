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

public class InchiBond {
  
  private final InchiAtom start;
  private final InchiAtom end;
  private final InchiBondType type;
  private final InchiBondStereo stereo;
  
  public InchiBond(InchiAtom start, InchiAtom end, InchiBondType type) {
    this(start, end, type, InchiBondStereo.NONE);
  }

  public InchiBond(InchiAtom start, InchiAtom end, InchiBondType type, InchiBondStereo stereo) {
    if (start.equals(end)) {
      throw new IllegalArgumentException("start and end must be different atoms");
    }
    if (type == null) {
      throw new IllegalArgumentException("type must not be null");
    }
    if (stereo == null) {
      throw new IllegalArgumentException("stereo must not be null, use InchiBondStereo.NONE");
    }
    this.start = start;
    this.end = end;
    this.type = type;
    this.stereo = stereo;
  }

  public InchiAtom getStart() {
    return start;
  }

  public InchiAtom getEnd() {
    return end;
  }

  public InchiBondType getType() {
    return type;
  }

  public InchiBondStereo getStereo() {
    return stereo;
  }

  public InchiAtom getOther(InchiAtom atom) {
    if (start == atom) {
      return end;
    }
    if (end == atom) {
      return start;
    }
    return null;
  }
  

}
