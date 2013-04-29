package com.wuetherich.osgi.ds.annotations.internal.builder.store;

import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface GeneratedComponentDescriptionsSaveParticipant {

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @throws CoreException
   */
  void propertiesSaved(IProject project, Properties properties) throws CoreException;
}
