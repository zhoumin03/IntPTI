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
package org.sosy_lab.cpachecker.cfa.ast.c;


import org.sosy_lab.cpachecker.cfa.ast.ARightHandSide;
import org.sosy_lab.cpachecker.cfa.types.c.CType;


/**
 * Interface for all possible right-hand sides of an assignment.
 */
public interface CRightHandSide extends CAstNode, ARightHandSide {


  public <R, X extends Exception> R accept(CRightHandSideVisitor<R, X> pV) throws X;

  @Override
  public CType getExpressionType();


}
