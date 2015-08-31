/*******************************************************************************
 * Copyright (c) 2015 Gerd Wütherich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd Wütherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.internal.builder;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class JDTAdapter {

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public static String getJavaVersion() {

    // get the JavaCore class
    Class<JavaCore> javaCoreClass = JavaCore.class;

    try {

      // try to get the VERSION_1_8 version string
      Field field = javaCoreClass.getDeclaredField("VERSION_1_8");
      return (String) field.get(null);

    } catch (Exception e) {

      // return the VERSION_1_7 version string as default (in case we run in an eclipse environment
      // that does not support java 8)
      return JavaCore.VERSION_1_7;
    }
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public static int getApiLevel() {

    // get the AST class
    Class<AST> astClass = AST.class;

    try {

      // try to get the JLS8-API level
      Field field = astClass.getDeclaredField("JLS8");
      return field.getInt(null);

    } catch (Exception e) {

      // return the JLS4-API level as default (in case we run in an eclipse environment
      // that does not support java 8)
      return AST.JLS4;
    }
  }
}
