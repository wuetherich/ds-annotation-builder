package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import java.util.List;

import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public interface IComponentDescription {

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  String getName();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  String getAssociatedSourceFile();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  String toXml();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  boolean hasProblems();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  List<DsAnnotationProblem> getProblems();

  /**
   * <p>
   * </p>
   *
   * @return
   */
  AbstractTypeAccessor getTypeAccessor();
}
