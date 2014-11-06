package com.wuetherich.osgi.ds.annotations.internal.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

public class RegenerateComponentDesciptionForSelectedProjectsHandler extends AbstractHandler implements IHandler {

  @Override
  public Object execute(ExecutionEvent event) throws ExecutionException {

    if (isEnabled()) {

      ISelection selection = HandlerUtil.getCurrentSelection(event);

      if (selection instanceof IStructuredSelection) {

        for (Object object : ((IStructuredSelection) selection).toList()) {

          IProject project = null;

          if (object instanceof IResource) {
            IResource resource = (IResource) object;
            project = resource.getProject();
          } else if (object instanceof IAdaptable) {
            IResource resource = (IResource) ((IAdaptable) object).getAdapter(IResource.class);
            if (resource != null) {
              project = resource.getProject();
            }
          }

          if (project != null) {
            BuildSupport.rebuildProject(project);
          }
        }
      }
    }

    //
    return null;
  }
}
