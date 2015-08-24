package com.wuetherich.osgi.ds.annotations.internal.componentdescription;

import java.util.LinkedList;
import java.util.List;

import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;

public abstract class AbstractTypeAccessor {

  private String                    _name;

  private String                    _activateMethodName;

  private String                    _deactivateMethodName;

  private String                    _modifiedMethodName;

  private Boolean                   _isEnabled;

  private Boolean                   _isImmediate;

  private String                    _factory;

  private String                    _configurationPolicy;

  private Object[]                  _configurationPid;

  private Object[]                  _property;

  private Object[]                  _properties;

  private String[]                  _service;

  private Boolean                   _serviceFactory;

  private List<Reference>           _references;

  private String                    _implementationClassName;

  /** - */
  private List<DsAnnotationProblem> _problems;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractTypeAccessor}.
   * </p>
   * 
   */
  public AbstractTypeAccessor() {
    _problems = new LinkedList<DsAnnotationProblem>();
    _references = new LinkedList<Reference>();
  }

  /**
   * {@inheritDoc}
   */
  public final boolean hasProblems() {
    return !_problems.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  public final List<DsAnnotationProblem> getProblems() {
    return _problems;
  }

  public void setName(String name) {
    _name = name;
  }

  public void setActivateMethodName(String activateMethodName) {
    _activateMethodName = activateMethodName;
  }

  public void setDeactivateMethodName(String deactivateMethodName) {
    _deactivateMethodName = deactivateMethodName;
  }

  public void setModifiedMethodName(String modifiedMethodName) {
    _modifiedMethodName = modifiedMethodName;
  }

  public void setEnabled(Boolean isEnabled) {
    _isEnabled = isEnabled;
  }

  public void setImmediate(Boolean isImmediate) {
    _isImmediate = isImmediate;
  }

  public void setFactory(String factory) {
    _factory = factory;
  }

  public void setConfigurationPolicy(String configurationPolicy) {
    _configurationPolicy = configurationPolicy;
  }

  public void setConfigurationPid(Object[] configurationPid) {
    _configurationPid = configurationPid;
  }

  public void setProperty(Object[] property) {
    _property = property;
  }

  public void setProperties(Object[] properties) {
    _properties = properties;
  }

  public void setService(String[] service) {
    _service = service;
  }

  public void setServiceFactory(Boolean serviceFactory) {
    _serviceFactory = serviceFactory;
  }

  public void setReferences(List<Reference> references) {
    _references = references;
  }

  public void setImplementationClassName(String implementationClassName) {
    _implementationClassName = implementationClassName;
  }

  public String getName() {
    return _name;
  }

  public String getActivateMethodName() {
    return _activateMethodName;
  }

  public String getDeactivateMethodName() {
    return _deactivateMethodName;
  }

  public String getModifiedMethodName() {
    return _modifiedMethodName;
  }

  public Boolean isEnabled() {
    return _isEnabled;
  }

  public Boolean isImmediate() {
    return _isImmediate;
  }

  public String getFactory() {
    return _factory;
  }

  public String getConfigurationPolicy() {
    return _configurationPolicy;
  }

  public Object[] getConfigurationPid() {
    return _configurationPid;
  }

  public Object[] getProperty() {
    return _property;
  }

  public Object[] getProperties() {
    return _properties;
  }

  public String[] getService() {
    return _service;
  }

  public Boolean getServiceFactory() {
    return _serviceFactory;
  }

  public List<Reference> getReferences() {
    return _references;
  }

  public String getImplementationClassName() {
    return _implementationClassName;
  }

  public abstract String getAssociatedSourceFile();

  public abstract List<String> getAllDirectlyImplementedSuperInterfaces();

  public abstract boolean checkMethodExists(String unbind);

  public abstract void assertNoDsAnnotation(String unbind);

  public abstract boolean isInstanceOf(String service);
}
