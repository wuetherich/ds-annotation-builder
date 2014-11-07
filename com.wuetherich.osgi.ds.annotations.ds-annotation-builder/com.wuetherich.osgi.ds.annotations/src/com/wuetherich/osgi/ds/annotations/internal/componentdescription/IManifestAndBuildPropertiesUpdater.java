package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IManifestAndBuildPropertiesUpdater {

  /**
   * <p>
   * </p>
   *
   * @param project
   * @throws CoreException
   */
  void updateManifestAndBuildProperties(IProject project) throws CoreException;
}
