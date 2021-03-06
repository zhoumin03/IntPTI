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
package org.sosy_lab.cpachecker.cpa.smg.errorReport;

import org.sosy_lab.cpachecker.cfa.ast.c.CAstNode;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.checker.CheckerWithInstantErrorReport;
import org.sosy_lab.cpachecker.core.interfaces.checker.DefaultTracedErrorReport;
import org.sosy_lab.cpachecker.cpa.smg.SMGState;
import org.sosy_lab.cpachecker.weakness.Weakness;

public class DFErrorReport extends DefaultTracedErrorReport {

  protected DFErrorReport(CAstNode pNode, CFAEdge edge, CheckerWithInstantErrorReport pChecker) {
    super(pNode, edge, pChecker);
  }

  @Override
  public Weakness getWeakness() {
    return Weakness.INVALID_FREE;
  }

  @Override
  public Class<? extends AbstractState> getSourceStateClass() {
    return SMGState.class;
  }

}
