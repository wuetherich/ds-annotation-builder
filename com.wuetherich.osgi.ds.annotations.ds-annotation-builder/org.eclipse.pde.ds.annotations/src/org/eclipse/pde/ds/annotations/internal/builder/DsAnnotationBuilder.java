/*******************************************************************************
 * Copyright (c) 2015 Gerd W�therich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd W�therich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.internal.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.internal.componentdescription.ComponentDescriptionFactory;

public class DsAnnotationBuilder extends IncrementalProjectBuilder {

  @Override
  protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {

    if (kind == IncrementalProjectBuilder.FULL_BUILD) {
      fullBuild(monitor);
    } else {
      IResourceDelta delta = getDelta(getProject());
      if (delta == null) {
        fullBuild(monitor);
      } else {
        incrementalBuild(delta, monitor);
      }
    }
    return null;
  }

  private void fullBuild(IProgressMonitor monitor) {
    try {
      getProject().accept(new DsAnnotationBuildVisitor());
    } catch (CoreException e) {
    }
  }

  protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {
    delta.accept(new DsAnnotationBuildVisitor());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void clean(IProgressMonitor monitor) throws CoreException {

    // delete all the markers
    getProject().deleteMarkers(Constants.DS_ANNOTATION_PROBLEM_MARKER, true, IResource.DEPTH_ZERO);

    //
    ComponentDescriptionFactory.getComponentDescriptionWriter().removeDanglingComponentDescriptions(getProject());

    //
    super.clean(monitor);
  }
}
