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
package org.eclipse.pde.ds.annotations;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.pde.ds.annotations.internal.Activator;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationsCore {

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public static List<IProject> getDsAnnotationAwareProjects() {
    return Activator.getDsAnnotationAwareProjects();
  }
}
