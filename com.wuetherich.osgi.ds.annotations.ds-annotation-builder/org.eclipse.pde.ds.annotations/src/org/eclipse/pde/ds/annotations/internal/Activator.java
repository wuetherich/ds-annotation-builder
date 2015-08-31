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
package org.eclipse.pde.ds.annotations.internal;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.eclipse.pde.ds.annotations.Constants;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * <p>
 * The {@link BundleActivator} used for this plug-in.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class Activator implements BundleActivator {

  /** the bundle context */
  private static BundleContext bundleContext;

  /**
   * {@inheritDoc}
   */
  @Override
  public void start(BundleContext context) throws Exception {
    bundleContext = context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop(BundleContext context) throws Exception {
    bundleContext = context;
  }

  /**
   * <p>
   * Returns the bundle context.
   * </p>
   * 
   * @return the bundle context.
   */
  public static BundleContext getBundleContext() {
    return bundleContext;
  }

  /**
   * <p>
   * Returns the {@link IBundleProjectDescription}.
   * </p>
   * 
   * @return the {@link IBundleProjectDescription}.
   * @throws CoreException
   */
  public static IBundleProjectDescription getBundleProjectDescription(IProject project) throws CoreException {

    // step 1: get the service reference
    ServiceReference<IBundleProjectService> ref = bundleContext.getServiceReference(IBundleProjectService.class);

    if (ref != null) {

      // step 2: get the service
      IBundleProjectService service = bundleContext.getService(ref);

      if (service != null) {

        // get the IBundleProjectDescription
        IBundleProjectDescription result = service.getDescription(project);

        bundleContext.ungetService(ref);

        return result;
      }
    }

    // return null if no IBundleProjectService is available
    return null;
  }

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public static List<IProject> getDsAnnotationAwareProjects() {

    //
    IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();

    //
    List<IProject> result = new LinkedList<IProject>();

    //
    for (IProject iProject : projects) {

      try {
        if (iProject.hasNature(Constants.NATURE_ID)) {
          result.add(iProject);
        }
      } catch (CoreException e) {
        // ignore
      }
    }

    // return the result
    return result;
  }
}
