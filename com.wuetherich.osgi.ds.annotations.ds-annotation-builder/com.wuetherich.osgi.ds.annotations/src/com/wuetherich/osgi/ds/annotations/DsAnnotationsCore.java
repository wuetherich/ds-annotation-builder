package com.wuetherich.osgi.ds.annotations;

import java.util.List;

import org.eclipse.core.resources.IProject;

import com.wuetherich.osgi.ds.annotations.internal.Activator;

/**
 * <p>
 * </p>
 *
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationsCore {

  /**
   * <p>
   * </p>
   *
   * @return
   */
  public static List<IProject> getDsAnnotationAwareProjects() {
    return Activator.getDsAnnotationAwareProjects();
  }
}
