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

import org.sosy_lab.cpachecker.cfa.ast.FileLocation;
import org.sosy_lab.cpachecker.cfa.types.java.JType;

import java.util.Objects;

/**
 * JFieldAccess is no FieldAccess, but a qualified FieldAccess.
 * Distinction between Fields and Variables are made through
 * Declarations JVariableDeclaration and JFieldDeclarations
 * JField Access makes the distinction between non-static
 * fields with qualifier, and the rest.
 */
public class JFieldAccess extends JIdExpression {

  //TODO Investigate if this should be refactored.

  private final JIdExpression qualifier;

  public JFieldAccess(
      FileLocation pFileLocation,
      JType pType,
      String pName,
      JFieldDeclaration pDeclaration,
      JIdExpression pQualifier) {
    super(pFileLocation, pType, pName, pDeclaration);
    qualifier = pQualifier;
  }

  @Override
  public JFieldDeclaration getDeclaration() {
    return (JFieldDeclaration) super.getDeclaration();
  }

  public JIdExpression getReferencedVariable() {
    return qualifier;
  }

  @Override
  public String toASTString() {
    //TODO Change to something simpler.
    // It seems some CPAs depend on this method for
    // getting variable names, investigate and change
    return super.toASTString();
  }

  /* (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 7;
    result = prime * result + Objects.hashCode(qualifier);
    result = prime * result + super.hashCode();
    return result;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof JFieldAccess)
        || super.equals(obj)) {
      return false;
    }

    JFieldAccess other = (JFieldAccess) obj;

    return Objects.equals(other.qualifier, qualifier);
  }

}
