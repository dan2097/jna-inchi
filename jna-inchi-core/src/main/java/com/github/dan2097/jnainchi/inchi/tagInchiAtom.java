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
package com.github.dan2097.jnainchi.inchi;
import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.ByReference;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class tagInchiAtom extends Structure implements ByReference {
  public double x;
  public double y;
  public double z;
  /**
   * adjacency list: ordering numbers of<br>
   * C type : AT_NUM[20]
   */
  public short[] neighbor = new short[20];
  /**
   * inchi_BondType<br>
   * C type : S_CHAR[20]
   */
  public byte[] bond_type = new byte[20];
  /**
   * inchi_BondStereo2D; negative if the<br>
   * C type : S_CHAR[20]
   */
  public byte[] bond_stereo = new byte[20];
  /**
   * zero-terminated chemical element name:<br>
   * C type : char[6]
   */
  public byte[] elname = new byte[6];
  /**
   * number of neighbors, bond types and bond<br>
   * C type : AT_NUM
   */
  public short num_bonds;
  /**
   * implicit hydrogen atoms<br>
   * C type : S_CHAR[3 + 1]
   */
  public byte[] num_iso_H = new byte[3 + 1];
  /**
   * 0 => non-isotopic; isotopic mass or<br>
   * C type : AT_NUM
   */
  public short isotopic_mass;
  /**
   * inchi_Radical<br>
   * C type : S_CHAR
   */
  public byte radical;
  /**
   * positive or negative; 0 => no charge<br>
   * C type : S_CHAR
   */
  public byte charge;

  protected List<String> getFieldOrder() {
    return Arrays.asList("x", "y", "z", "neighbor", "bond_type", "bond_stereo", "elname", "num_bonds", "num_iso_H", "isotopic_mass", "radical", "charge");
  }

}
