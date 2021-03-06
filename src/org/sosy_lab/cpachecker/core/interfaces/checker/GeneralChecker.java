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
package org.sosy_lab.cpachecker.core.interfaces.checker;

import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.weakness.Weakness;

/**
 * An empty interface, which is the base interface for all checkers
 */
public interface GeneralChecker {

  /**
   * Which programming language this checker is eligible to?
   *
   * @return the specific language this checker is for
   */
  PL forLanguage();

  /**
   * Which type of weakness this checker is for
   *
   * @return the type of weakness this checker is for
   */
  Weakness getOrientedWeakness();

  /**
   * Which type of abstract state this check works on?
   *
   * @return the oriented abstract state type
   */
  Class<? extends AbstractState> getServedStateType();

}
