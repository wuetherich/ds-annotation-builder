package com.wuetherich.osgi.ds.annotations.builder;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.wuetherich.osgi.ds.annotations.Constants;

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

  @Override
  protected void clean(IProgressMonitor monitor) throws CoreException {
    
		// getProject().deleteMarkers("com.xyz.myproblems",
		// true, IResource.DEPTH_INFINITE);
	  
	  getProject().deleteMarkers(Constants.DS_ANNOTATION_PROBLEM_MARKER, true,
				IResource.DEPTH_ZERO);
	  
	//
	List<IPath> result = GeneratedComponentDescriptionsStore
			.getGeneratedFiles(getProject());

	//
	for (IPath path : result) {

		try {
			IFile file = ResourcesPlugin.getWorkspace().getRoot()
					.getFile(path);
			file.delete(true, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	 super.clean(monitor);
  }
}
