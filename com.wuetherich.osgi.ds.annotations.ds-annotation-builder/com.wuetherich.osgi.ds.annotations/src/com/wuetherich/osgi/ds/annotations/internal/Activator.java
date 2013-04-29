/*******************************************************************************
 * Copyright (c) 2012 Gerd Wuetherich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Wuetherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.core.project.IBundleProjectService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * <p>
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
   * @return
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

    // the service reference
    ServiceReference ref = bundleContext.getServiceReference(IBundleProjectService.class.getName());

    //
    if (ref != null) {
      IBundleProjectService service = (IBundleProjectService) bundleContext.getService(ref);
      if (service != null) {

        IBundleProjectDescription result = service.getDescription(project);

        bundleContext.ungetService(ref);

        return result;
      }
    }

    //
    return null;
  }
}
