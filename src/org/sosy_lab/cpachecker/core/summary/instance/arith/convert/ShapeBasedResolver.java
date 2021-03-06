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
package org.sosy_lab.cpachecker.core.summary.instance.arith.convert;

import com.google.common.base.Function;

import org.sosy_lab.cpachecker.core.summary.instance.arith.ast.LinearVariable;
import org.sosy_lab.cpachecker.cpa.shape.ShapeState;

abstract public class ShapeBasedResolver<R> implements Function<LinearVariable, R> {
  protected ShapeState shape;

  public ShapeBasedResolver(ShapeState shape) {
    this.shape = shape;
  }

}
