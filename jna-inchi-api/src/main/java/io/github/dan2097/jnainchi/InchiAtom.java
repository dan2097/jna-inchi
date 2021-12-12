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

public class InchiAtom {
  
  private String elName;
  private double x = 0;
  private double y = 0;
  private double z = 0;

  //array positions for hydrogen (i.e. isotope not specified), protium, deuterium, tritium
  private int[] implicitHydrogen = new int[4];
  private int isotopicMass = 0;
  private InchiRadical radical = InchiRadical.NONE;
  private int charge = 0;
  
  public InchiAtom(String elName) {
    this.elName = elName;
  }
  
  public InchiAtom(String elName, double x, double y, double z) {
    this.elName = elName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public String getElName() {
    return elName;
  }

  public void setElName(String elName) {
    this.elName = elName;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getZ() {
    return z;
  }

  public void setZ(double z) {
    this.z = z;
  }

  public int getImplicitHydrogen() {
    return implicitHydrogen[0];
  }

  /**
   * Used for specifying implicit hydrogen of natural isotopic abundance.
   * If the hydrogen is known to be protium/deuterium/tritium this can be either specified
   * using {@link #setImplicitProtium(int)}, {@link #setImplicitDeuterium(int)}, {@link #setImplicitTritium(int)}
   * or using explicit atoms
   * 
   * -1 has the special meaning of deduce hydrogen count from valency
   * @param implicitHydrogen
   */
  public void setImplicitHydrogen(int implicitHydrogen) {
    if (implicitHydrogen > Byte.MAX_VALUE  || implicitHydrogen < -1) {
      throw new IllegalArgumentException("Unacceptable implicitHydrogen:" + implicitHydrogen);
    }
    this.implicitHydrogen[0] = implicitHydrogen;
  }
  
  public void setImplicitProtium(int implicitProtium) {
    if (implicitProtium > Byte.MAX_VALUE  || implicitProtium < 0) {
      throw new IllegalArgumentException("Unacceptable implicitProtium:" + implicitProtium);
    }
    this.implicitHydrogen[1] = implicitProtium;
  }
  
  public int getImplicitProtium() {
    return implicitHydrogen[1];
  }
  
  public void setImplicitDeuterium(int implicitDeuterium) {
    if (implicitDeuterium > Byte.MAX_VALUE  || implicitDeuterium < 0) {
      throw new IllegalArgumentException("Unacceptable implicitDeuterium:" + implicitDeuterium);
    }
    this.implicitHydrogen[2] = implicitDeuterium;
  }
  
  public int getImplicitDeuterium() {
    return implicitHydrogen[2];
  }
  
  public void setImplicitTritium(int implicitTritium) {
    if (implicitTritium > Byte.MAX_VALUE  || implicitTritium < 0) {
      throw new IllegalArgumentException("Unacceptable implicitTritium:" + implicitTritium);
    }
    this.implicitHydrogen[3] = implicitTritium;
  }
  
  public int getImplicitTritium() {
    return implicitHydrogen[3];
  }

  public int getIsotopicMass() {
    return isotopicMass;
  }

  public void setIsotopicMass(int isotopicMass) {
    if (isotopicMass > Short.MAX_VALUE || isotopicMass < 0) {
      throw new IllegalArgumentException("Unacceptable isotopicMass:" + isotopicMass);
    }
    this.isotopicMass = isotopicMass;
  }

  public InchiRadical getRadical() {
    return radical;
  }

  public void setRadical(InchiRadical radical) {
    this.radical = radical;
  }

  public int getCharge() {
    return charge;
  }

  public void setCharge(int charge) {
    if (charge > Byte.MAX_VALUE || charge < Byte.MIN_VALUE) {
      throw new IllegalArgumentException("Unacceptable charge:" + charge);
    }
    this.charge = charge;
  }
}
