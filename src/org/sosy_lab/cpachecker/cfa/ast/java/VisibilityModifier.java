/*
 * IntPTI: integer error fixing by proper-type inference
 * Copyright (c) 2017.
 *
 * Open-source component:
 *
 * CPAchecker
 * Copyright (C) 2007-2014  Dirk Beyer
 *
 * Guava: Google Core Libraries for Java
 * Copyright (C) 2010-2006  Google
 *
 *
 */
package org.sosy_lab.cpachecker.cfa.ast.java;

/**
 * An enumeration listing the visibility modifier public, protected, none, private of Java.
 */
public enum VisibilityModifier {

  PUBLIC("public"),
  NONE(""),
  PROTECTED("protected"),
  PRIVATE("private");

  private final String mod;

  private VisibilityModifier(String pMod) {
    mod = pMod;
  }

  public String getModifierString() {
    return mod;
  }
}
