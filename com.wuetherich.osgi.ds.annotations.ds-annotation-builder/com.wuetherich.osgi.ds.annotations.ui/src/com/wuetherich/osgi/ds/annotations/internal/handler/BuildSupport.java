package com.wuetherich.osgi.ds.annotations.internal.handler;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.DsAnnotationsCore;

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
