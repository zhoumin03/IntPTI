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
package org.sosy_lab.cpachecker.cpa.apron.refiner;

import org.sosy_lab.common.ShutdownNotifier;
import org.sosy_lab.common.configuration.Configuration;
import org.sosy_lab.common.configuration.InvalidConfigurationException;
import org.sosy_lab.common.log.LogManager;
import org.sosy_lab.cpachecker.cfa.CFA;
import org.sosy_lab.cpachecker.core.interfaces.ConfigurableProgramAnalysis;
import org.sosy_lab.cpachecker.core.interfaces.Refiner;
import org.sosy_lab.cpachecker.core.interfaces.WrapperCPA;
import org.sosy_lab.cpachecker.cpa.apron.ApronCPA;
import org.sosy_lab.cpachecker.cpa.arg.AbstractARGBasedRefiner;
import org.sosy_lab.cpachecker.cpa.predicate.PredicateCPARefiner;
import org.sosy_lab.cpachecker.cpa.value.ValueAnalysisState;
import org.sosy_lab.cpachecker.cpa.value.refiner.ValueAnalysisPathInterpolator;
import org.sosy_lab.cpachecker.cpa.value.refiner.ValueAnalysisStrongestPostOperator;
import org.sosy_lab.cpachecker.cpa.value.refiner.utils.ValueAnalysisFeasibilityChecker;
import org.sosy_lab.cpachecker.cpa.value.refiner.utils.ValueAnalysisPrefixProvider;
import org.sosy_lab.cpachecker.util.refinement.FeasibilityChecker;
import org.sosy_lab.cpachecker.util.refinement.StrongestPostOperator;

/**
 * Refiner implementation that delegates to {@link ValueAnalysisPathInterpolator},
 * and if this fails, optionally delegates also to {@link PredicateCPARefiner}.
 */
public abstract class ApronDelegatingRefiner implements Refiner {

  public static Refiner create(ConfigurableProgramAnalysis cpa)
      throws InvalidConfigurationException {
    if (!(cpa instanceof WrapperCPA)) {
      throw new InvalidConfigurationException(
          ApronDelegatingRefiner.class.getSimpleName() + " could not find the ApronCPA");
    }

    ApronCPA apronCPA = ((WrapperCPA) cpa).retrieveWrappedCpa(ApronCPA.class);
    if (apronCPA == null) {
      throw new InvalidConfigurationException(
          ApronDelegatingRefiner.class.getSimpleName() + " needs an ApronCPA");
    }

    final Configuration config = apronCPA.getConfiguration();
    final LogManager logger = apronCPA.getLogger();
    final ShutdownNotifier shutdownNotifier = apronCPA.getShutdownNotifier();
    final CFA cfa = apronCPA.getCFA();

    final StrongestPostOperator<ValueAnalysisState> strongestPostOp =
        new ValueAnalysisStrongestPostOperator(logger, Configuration.builder().build(), cfa);

    final FeasibilityChecker<ValueAnalysisState> feasibilityChecker =
        new ValueAnalysisFeasibilityChecker(strongestPostOp, logger, cfa, config);

    final ValueAnalysisPathInterpolator interpolatingRefiner =
        new ValueAnalysisPathInterpolator(
            feasibilityChecker,
            strongestPostOp,
            new ValueAnalysisPrefixProvider(logger, cfa, config),
            config,
            logger,
            shutdownNotifier,
            cfa);

    ApronARGBasedDelegatingRefiner refiner =
        new ApronARGBasedDelegatingRefiner(
            config,
            logger,
            shutdownNotifier,
            cfa,
            apronCPA.getManager(),
            apronCPA.getTransferRelation(),
            feasibilityChecker,
            interpolatingRefiner);
    return AbstractARGBasedRefiner.forARGBasedRefiner(refiner, cpa);
  }
}
