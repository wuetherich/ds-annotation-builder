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
package org.eclipse.pde.ds.annotations.internal.componentdescription;

import java.util.List;

import org.eclipse.pde.ds.annotations.internal.DsAnnotationProblem;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IComponentDescription {

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  String getName();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  String getAssociatedSourceFile();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  String toXml();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  boolean hasProblems();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  List<DsAnnotationProblem> getProblems();

  /**
   * <p>
   * </p>
   *
   * @return
   */
  AbstractTypeAccessor getTypeAccessor();
}
