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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IComponentDescriptionWriter {

  /**
   * <p>
   * </p>
   * 
   * @param project
   */
  void removeDanglingComponentDescriptions(IProject project);

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param description
   * @throws CoreException
   */
  void writeComponentDescription(IProject project, IComponentDescription description) throws CoreException;

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param resource
   * @throws CoreException
   */
  void deleteGeneratedFiles(IProject project, IPath resource) throws CoreException;

}
