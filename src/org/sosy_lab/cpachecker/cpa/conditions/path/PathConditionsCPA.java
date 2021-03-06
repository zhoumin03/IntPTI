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
package org.sosy_lab.cpachecker.cpa.conditions.path;

import org.sosy_lab.common.Classes;
import org.sosy_lab.common.configuration.ClassOption;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.configuration.Option;
import org.sosy_lab.common.configuration.Options;
import org.sosy_lab.cpachecker.cfa.model.CFAEdge;
import org.sosy_lab.cpachecker.cfa.model.CFANode;
import org.sosy_lab.cpachecker.core.defaults.AutomaticCPAFactory;
import org.sosy_lab.cpachecker.core.defaults.FlatLatticeDomain;
import org.sosy_lab.cpachecker.core.defaults.MergeSepOperator;
import org.sosy_lab.cpachecker.core.defaults.SingleEdgeTransferRelation;
import org.sosy_lab.cpachecker.core.defaults.SingletonPrecision;
import org.sosy_lab.cpachecker.core.defaults.StaticPrecisionAdjustment;
import org.sosy_lab.cpachecker.core.defaults.StopAlwaysOperator;
import org.sosy_lab.cpachecker.core.interfaces.AbstractDomain;
import org.sosy_lab.cpachecker.core.interfaces.AbstractState;
import org.sosy_lab.cpachecker.core.interfaces.CPAFactory;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysisWithBAM;
import org.sosy_lab.cpachecker.core.interfaces.MergeOperator;
import org.sosy_lab.cpachecker.core.interfaces.Precision;
import org.sosy_lab.cpachecker.core.interfaces.PrecisionAdjustment;
import org.sosy_lab.cpachecker.core.interfaces.Reducer;
import org.sosy_lab.cpachecker.core.interfaces.StateSpacePartition;
import org.sosy_lab.cpachecker.core.interfaces.Statistics;
import org.sosy_lab.cpachecker.core.interfaces.StatisticsProvider;
import org.sosy_lab.cpachecker.core.interfaces.StopOperator;
import org.sosy_lab.cpachecker.core.interfaces.TransferRelation;
import org.sosy_lab.cpachecker.core.interfaces.conditions.AdjustableConditionCPA;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * CPA for path conditions ({@link PathCondition}).
 * It can be configured to work with any condition that implements this interface.
 */
@Options(prefix = "cpa.conditions.path")
public class PathConditionsCPA
    implements ConfigurableProgramAnalysisWithBAM, AdjustableConditionCPA, StatisticsProvider {

  @Option(secure = true, description = "The condition", name = "condition", required = true)
  @ClassOption(packagePrefix = "org.sosy_lab.cpachecker.cpa.conditions.path")
  private Class<? extends PathCondition> conditionClass;

  private final PathCondition condition;

  private final AbstractDomain domain = new FlatLatticeDomain();
  private final TransferRelation transfer = new SingleEdgeTransferRelation() {
    @Override
    public Collection<? extends AbstractState> getAbstractSuccessorsForEdge(
        AbstractState pState,
        List<AbstractState> otherStates,
        Precision pPrecision,
        CFAEdge pCfaEdge) {
      return Collections.singleton(condition.getAbstractSuccessor(pState, pCfaEdge));
    }

    @Override
    public Collection<? extends AbstractState> strengthen(
        AbstractState pState,
        List<AbstractState> pOtherStates, CFAEdge pCfaEdge, Precision pPrecision) {
      return null;
    }
  };


  public static CPAFactory factory() {
    return AutomaticCPAFactory.forType(PathConditionsCPA.class);
  }

  private PathConditionsCPA(Configuration config) throws InvalidConfigurationException {
    config.inject(this);

    Class<?>[] argumentTypes = {Configuration.class};
    Object[] argumentValues = {config};
    condition =
        Classes.createInstance(PathCondition.class, conditionClass, argumentTypes, argumentValues);
  }

  @Override
  public void collectStatistics(Collection<Statistics> pStatsCollection) {
    if (condition instanceof StatisticsProvider) {
      ((StatisticsProvider) condition).collectStatistics(pStatsCollection);

    } else if (condition instanceof Statistics) {
      pStatsCollection.add((Statistics) condition);
    }
  }

  @Override
  public AbstractState getInitialState(CFANode pNode, StateSpacePartition pPartition) {
    return condition.getInitialState(pNode);
  }

  @Override
  public Precision getInitialPrecision(CFANode pNode, StateSpacePartition pPartition) {
    return SingletonPrecision.getInstance();
  }

  @Override
  public boolean adjustPrecision() {
    return condition.adjustPrecision();
  }

  @Override
  public AbstractDomain getAbstractDomain() {
    return domain;
  }

  @Override
  public MergeOperator getMergeOperator() {
    return MergeSepOperator.getInstance();
  }

  @Override
  public PrecisionAdjustment getPrecisionAdjustment() {
    return StaticPrecisionAdjustment.getInstance();
  }

  @Override
  public StopOperator getStopOperator() {
    return StopAlwaysOperator.getInstance();
  }

  @Override
  public TransferRelation getTransferRelation() {
    return transfer;
  }

  @Override
  public Reducer getReducer() {
    return condition.getReducer();
  }
}
