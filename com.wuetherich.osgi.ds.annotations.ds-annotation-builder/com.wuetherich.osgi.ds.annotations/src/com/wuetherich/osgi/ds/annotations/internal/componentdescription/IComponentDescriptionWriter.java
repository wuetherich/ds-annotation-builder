package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

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
