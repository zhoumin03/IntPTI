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
package org.sosy_lab.cpachecker.util.misc;

import java.io.File;

public class DiskUtil {
  public static boolean exists(String path) {
    return new File(path).exists();
  }

  public static boolean existsDirectory(String path) {
    File f = new File(path);
    return f.exists() && f.isDirectory();
  }

  public static boolean existsFile(String path) {
    File f = new File(path);
    return f.exists() && f.isFile();
  }
}