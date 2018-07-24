package com.github.dan2097.jnainchi;

public class InchiAtom {
  
  private String elName;
  private double x = 0;
  private double y = 0;
  private double z = 0;

  private int implicitHydrogen = 0;
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
    return implicitHydrogen;
  }

  public void setImplicitHydrogen(int implicitHydrogen) {
    if (implicitHydrogen > Byte.MAX_VALUE  || implicitHydrogen < -1) {
      throw new IllegalArgumentException("Unacceptable implicitHydrogen:" + implicitHydrogen);
    }
    this.implicitHydrogen = implicitHydrogen;
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
