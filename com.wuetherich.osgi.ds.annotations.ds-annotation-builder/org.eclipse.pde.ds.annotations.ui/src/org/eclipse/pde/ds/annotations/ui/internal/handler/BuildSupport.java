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
package org.eclipse.pde.ds.annotations.ui.internal.handler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.DsAnnotationsCore;

public class BuildSupport {

  public static void rebuildProjects() {
    for (IProject project : DsAnnotationsCore.getDsAnnotationAwareProjects()) {
      rebuildProject(project);
    }
  }

  public static void rebuildProject(final IProject project) {

    Job job = new Job(String.format("Rebuild '%s'...", project.getName())) {

      @Override
      protected IStatus run(IProgressMonitor monitor) {
        try {
          project.build(IncrementalProjectBuilder.FULL_BUILD, Constants.BUILDER_ID, null, monitor);
        } catch (CoreException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        return Status.OK_STATUS;
      }

    };
    job.schedule();
  }

}
