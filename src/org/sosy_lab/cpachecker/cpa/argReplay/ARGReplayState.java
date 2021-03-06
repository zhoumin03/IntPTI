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
package org.sosy_lab.cpachecker.cpa.argReplay;

import com.google.common.collect.Sets;

import org.sosy_lab.cpachecker.core.defaults.LatticeAbstractState;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.cpa.arg.ARGState;

import java.util.Set;

/**
 * Abstract state of a powerset domain.
 */
public class ARGReplayState implements LatticeAbstractState<ARGReplayState> {

  private final Set<ARGState> states;

  private final ConfigurableProgramAnalysis cpa;

  ARGReplayState(Set<ARGState> states, ConfigurableProgramAnalysis cpa) {
    this.states = states;
    this.cpa = cpa;
  }

  public Set<ARGState> getStates() {
    return states;
  }

  public ConfigurableProgramAnalysis getCPA() {
    return cpa;
  }

  @Override
  public String toString() {
    return String.format("(%s)", states).replace("\n", "\n    ");
  }

  @Override
  public ARGReplayState join(ARGReplayState other) {
    if (this == other) {
      return this;
    }
    return new ARGReplayState(Sets.union(this.states, other.states), cpa);
  }

  @Override
  public boolean isLessOrEqual(ARGReplayState other) {
    if (this == other) {
      return true;
    }
    return other.states.containsAll(this.states);
  }

  @Override
  public int hashCode() {
    return states.hashCode() + 31 * cpa.hashCode();
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof ARGReplayState
        && this.states.equals(((ARGReplayState) other).states)
        && this.cpa.equals(((ARGReplayState) other).cpa);
  }

  @Override
  public boolean isEqualTo(AbstractState other) {
    return equals(other);
  }
}
