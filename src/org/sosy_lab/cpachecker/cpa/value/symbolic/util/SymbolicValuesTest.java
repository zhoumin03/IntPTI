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
package org.sosy_lab.cpachecker.cpa.value.symbolic.util;

import org.junit.Assert;
import org.junit.Test;
import org.sosy_lab.cpachecker.cfa.types.Type;
import org.sosy_lab.cpachecker.cfa.types.c.CNumericTypes;
import org.sosy_lab.cpachecker.cpa.value.symbolic.type.SymbolicExpression;
import org.sosy_lab.cpachecker.cpa.value.symbolic.type.SymbolicIdentifier;
import org.sosy_lab.cpachecker.cpa.value.symbolic.type.SymbolicValueFactory;
import org.sosy_lab.cpachecker.cpa.value.type.NumericValue;
import org.sosy_lab.cpachecker.util.states.MemoryLocation;

/**
 * Unit tests for {@link SymbolicValues}.
 */
public class SymbolicValuesTest {

  private final MemoryLocation memLoc1 = MemoryLocation.valueOf("a");
  private final MemoryLocation memLoc2 = MemoryLocation.valueOf("b");

  private final SymbolicValueFactory factory = SymbolicValueFactory.getInstance();
  private final Type defType = CNumericTypes.INT;

  private final SymbolicIdentifier id1 = factory.newIdentifier();
  private final SymbolicIdentifier id2 = factory.newIdentifier();

  private final SymbolicExpression idExp1 = factory.asConstant(id1,
      defType);
  private final SymbolicExpression idExp2 = factory.asConstant(id2,
      defType);

  private final NumericValue num1 = new NumericValue(5);
  private final SymbolicExpression numExp1 = factory.asConstant(num1, defType);

  @Test
  public void testRepresentSameCCodeExpression_diffValueSameLocation() {
    final SymbolicExpression locLessExp1 = factory.add(idExp1, idExp2, defType, defType);
    final SymbolicExpression locLessExp2 = factory.add(idExp1, numExp1, defType, defType);

    SymbolicExpression exp1 = locLessExp1.copyForLocation(memLoc1);
    SymbolicExpression exp2 = locLessExp1.copyForLocation(memLoc2);
    SymbolicExpression exp3 = locLessExp2.copyForLocation(memLoc2);

    SymbolicExpression constr1 = factory.lessThan(exp1, exp3, defType, defType);
    SymbolicExpression constr2 = factory.lessThan(exp1, exp2, defType, defType);

    Assert.assertTrue(SymbolicValues.representSameCCodeExpression(exp1, exp1));
    Assert.assertTrue(SymbolicValues.representSameCCodeExpression(constr1, constr2));
    Assert.assertFalse(SymbolicValues.representSameCCodeExpression(exp1, exp2));
  }

  @Test
  public void testRepresentSameCCodeExpression_constraintAndItsNegation() {
    final SymbolicExpression expWithLocation1 = idExp1.copyForLocation(memLoc1);
    final SymbolicExpression expWithLocation2 = idExp2.copyForLocation(memLoc2);

    SymbolicExpression constraint = factory.add(expWithLocation1, numExp1, defType, defType);
    constraint = factory.lessThan(constraint, expWithLocation2, defType, defType);
    final SymbolicExpression negation = factory.negate(constraint, defType);

    Assert.assertFalse(SymbolicValues.representSameCCodeExpression(constraint, negation));
  }
}
