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
   * @param methodName
   */
  void setActivateMethod(String methodName);

  /**
   * <p>
   * </p>
   * 
   * @param methodName
   */
  void setDeactivateMethod(String methodName);

  /**
   * <p>
   * </p>
   * 
   * @param methodName
   */
  void setModified(String methodName);

  /**
   * <p>
   * </p>
   * 
   * @param name
   */
  void setName(String name);

  /**
   * <p>
   * </p>
   * 
   * @param isEnabled
   */
  void setEnabled(Boolean isEnabled);

  /**
   * <p>
   * </p>
   * 
   * @param isImmediate
   */
  void setImmediate(Boolean isImmediate);

  /**
   * <p>
   * </p>
   * 
   * @param factory
   */
  void setFactory(String factory);

  /**
   * <p>
   * </p>
   * 
   * @param configurationPolicy
   */
  void setConfigurationPolicy(String configurationPolicy);

  /**
   * <p>
   * </p>
   * 
   * @param configurationPid
   */
  void setConfigurationPid(String configurationPid);

  /**
   * <p>
   * </p>
   * 
   * @param propertyArray
   */
  void addProperty(Object[] propertyArray);

  /**
   * <p>
   * </p>
   * 
   * @param properties
   */
  void addProperties(String properties);

  /**
   * <p>
   * </p>
   * 
   * @param services
   */
  void setService(String[] services);

  /**
   * <p>
   * </p>
   * 
   * @param serviceFactory
   */
  void setServiceFactory(Boolean serviceFactory);

  /**
   * <p>
   * </p>
   * 
   * @param service
   * @param bind
   * @param name
   * @param cardinality
   * @param policy
   * @param policyOption
   * @param unbind
   * @param updated
   * @param target
   */
  void addReference(String service, String bind, String name, String cardinality, String policy, String policyOption,
      String unbind, String updated, String target);
}
