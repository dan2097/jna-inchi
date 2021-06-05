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
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
/**
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> , <a href="http://rococoa.dev.java.net/">Rococoa</a>, or <a href="http://jna.dev.java.net/">JNA</a>.
 */
public class inchi_Input_V3000 extends Structure implements ByReference {
  public int n_non_star_atoms;
  public int n_star_atoms;
  /**
   * Index as supplied for atoms<br>
   * C type : int*
   */
  public IntByReference atom_index_orig;
  /**
   * = index or -1 for star atom<br>
   * C type : int*
   */
  public IntByReference atom_index_fin;
  /** Not used yet. */
  public int n_sgroups;
  /** Not used yet. */
  public int n_3d_constraints;
  public int n_collections;
  public int n_non_haptic_bonds;
  public int n_haptic_bonds;
  /**
   * Haptic_bonds[i] is pointer to int<br>
   * C type : int**
   */
  public PointerByReference lists_haptic_bonds;
  public int n_steabs;
  /**
   * steabs[k][0] - not used<br>
   * C type : int**
   */
  public PointerByReference lists_steabs;
  public int n_sterel;
  /**
   * sterel[k][0] - n from "STERELn" tag<br>
   * C type : int**
   */
  public PointerByReference lists_sterel;
  public int n_sterac;
  /**
   * sterac[k][0] - n from "STERACn" tag<br>
   * C type : int**
   */
  public PointerByReference lists_sterac;

  protected List<String> getFieldOrder() {
    return Arrays.asList("n_non_star_atoms", "n_star_atoms", "atom_index_orig", "atom_index_fin", "n_sgroups", "n_3d_constraints", "n_collections", "n_non_haptic_bonds", "n_haptic_bonds", "lists_haptic_bonds", "n_steabs", "lists_steabs", "n_sterel", "lists_sterel", "n_sterac", "lists_sterac");
  }

}
