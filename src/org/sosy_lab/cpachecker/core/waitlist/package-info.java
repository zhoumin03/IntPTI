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

/**
 * The waitlist, which stores the to-be-processed abstract states during the analysis.
 * It is also responsible for the traversal strategy.
 */
@javax.annotation.ParametersAreNonnullByDefault
package org.sosy_lab.cpachecker.core.waitlist;