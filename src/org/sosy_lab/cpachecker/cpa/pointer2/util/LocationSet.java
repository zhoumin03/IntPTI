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
package org.sosy_lab.cpachecker.cpa.pointer2.util;

import org.sosy_lab.cpachecker.util.states.MemoryLocation;


public interface LocationSet {

  boolean mayPointTo(MemoryLocation pLocation);

  LocationSet addElement(MemoryLocation pLocation);

  LocationSet removeElement(MemoryLocation pLocation);

  LocationSet addElements(Iterable<MemoryLocation> pLocations);

  LocationSet addElements(LocationSet pLocations);

  boolean isBot();

  boolean isTop();

  boolean containsAll(LocationSet pLocations);

}
