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
package org.sosy_lab.cpachecker.cfa.types;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Objects;


public abstract class AArrayType implements Type {

  private static final long serialVersionUID = -2838888440949947901L;

  private final Type elementType;


  public AArrayType(Type pElementType) {
    elementType = checkNotNull(pElementType);
  }

  public Type getType() {
    return elementType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 7;
    result = prime * result + Objects.hashCode(elementType);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof AArrayType)) {
      return false;
    }

    AArrayType other = (AArrayType) obj;

    return Objects.equals(elementType, other.elementType);
  }


}