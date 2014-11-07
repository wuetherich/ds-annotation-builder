package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IComponentDescriptionReader {

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   */
  boolean isComponentDesciptor(IFile file);

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   */
  boolean containsDsAnnotationBuilderComment(IFile file);
  
  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   */
  String extractSourceLocation(IFile file);

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   */
  List<IPath> getAllComponentDescriptions(IProject project);

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   */
  Map<IPath, List<IPath>> loadGeneratedDescriptionsMap(IProject project);

}
