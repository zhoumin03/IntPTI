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
package org.sosy_lab.cpachecker.core.interfaces.pcc;

import org.sosy_lab.cpachecker.pcc.strategy.partialcertificate.PartialReachedSetDirectedGraph;

import java.util.List;
import java.util.Set;

/**
 * Interface for algorithms that compute balanced graph partitionings.
 *
 * Since the problem is NP complete and no polytime approximation exists, almost equal size
 * partitions may be computed.
 */
public interface BalancedGraphPartitioner {

  /**
   * Divides the node of <code>pGraph</code> into <code>pNumPartitions</code> disjunct sets of
   * almost equal size.
   *
   * @param pNumPartitions - number of disjunct sets, greater 1
   * @param pGraph         - directed graph whose nodes should be partitioned
   * @return the partitioning, each set contains the indices of the nodes which it contains
   */
  public List<Set<Integer>> computePartitioning(
      int pNumPartitions,
      PartialReachedSetDirectedGraph pGraph)
      throws InterruptedException;
}
