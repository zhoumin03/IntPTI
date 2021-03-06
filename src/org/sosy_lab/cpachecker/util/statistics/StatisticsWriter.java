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
package org.sosy_lab.cpachecker.util.statistics;

import java.io.PrintStream;

public class StatisticsWriter {

  private final StatisticsWriter parentLevelWriter;
  private final PrintStream target;
  private final int level;
  private final int outputNameColWidth;

  private StatisticsWriter(
      PrintStream pTarget, int pLevel,
      int pOutputNameColWidth, StatisticsWriter pParentLevelWriter) {
    this.parentLevelWriter = pParentLevelWriter;
    this.target = pTarget;
    this.level = pLevel;
    this.outputNameColWidth = pOutputNameColWidth;
  }

  /**
   * Use this method instead of direct calls to the constructor
   * to allow overriding.
   */
  protected StatisticsWriter newInstance(
      PrintStream pTarget, int pLevel,
      int pOutputNameColWidth, StatisticsWriter pParentLevelWriter) {
    return new StatisticsWriter(pTarget, pLevel, pOutputNameColWidth, pParentLevelWriter);
  }

  public static StatisticsWriter writingStatisticsTo(PrintStream pTarget) {
    return new StatisticsWriter(pTarget, 0, 50, null);
  }

  public StatisticsWriter withNameColumnWith(int pWidth) {
    return newInstance(target, level, pWidth, this);
  }

  public StatisticsWriter beginLevel() {
    return newInstance(target, level + 1, outputNameColWidth, this);
  }

  public StatisticsWriter withLevel(int pLevel) {
    return newInstance(target, pLevel, outputNameColWidth, this);
  }

  public StatisticsWriter endLevel() {
    if (parentLevelWriter == null) {
      return this;
    } else {
      return parentLevelWriter;
    }
  }

  public StatisticsWriter ifUpdatedAtLeastOnce(AbstractStatValue stat) {
    return ifTrue(stat.getUpdateCount() > 0);
  }

  public StatisticsWriter ifTrue(boolean condition) {
    if (condition) {
      return this;
    } else {
      return new DisabledStatisticsWriter(target, level, outputNameColWidth, parentLevelWriter);
    }
  }

  public StatisticsWriter spacer() {
    target.println();
    return this;
  }

  public StatisticsWriter put(String name, Object value) {
    StatisticsUtils.write(target, level, outputNameColWidth, name, value);
    return this;
  }

  public StatisticsWriter put(AbstractStatValue stat) {
    StatisticsUtils.write(target, level, outputNameColWidth, stat);
    return this;
  }

  public StatisticsWriter putIfUpdatedAtLeastOnce(AbstractStatValue stat) {
    return putIf(stat.getUpdateCount() > 0, stat);
  }

  public StatisticsWriter putIf(boolean condition, AbstractStatValue stat) {
    if (condition) {
      put(stat);
    }
    return this;
  }

  public StatisticsWriter putIf(boolean condition, String name, Object value) {
    if (condition) {
      put(name, value);
    }
    return this;
  }

  private static class DisabledStatisticsWriter extends StatisticsWriter {

    DisabledStatisticsWriter(
        PrintStream pTarget, int pLevel,
        int pOutputNameColWidth, StatisticsWriter pParentLevelWriter) {
      super(pTarget, pLevel, pOutputNameColWidth, pParentLevelWriter);
    }

    @Override
    protected DisabledStatisticsWriter newInstance(
        PrintStream pTarget, int pLevel,
        int pOutputNameColWidth, StatisticsWriter pParentLevelWriter) {
      return new DisabledStatisticsWriter(pTarget, pLevel, pOutputNameColWidth, pParentLevelWriter);
    }

    @Override
    public StatisticsWriter spacer() {
      return this;
    }

    @Override
    public StatisticsWriter put(AbstractStatValue pStat) {
      return this;
    }

    @Override
    public StatisticsWriter put(String pName, Object pValue) {
      return this;
    }
  }
}