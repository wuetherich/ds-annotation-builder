/*******************************************************************************
 * Copyright (c) 2011-2013 Gerd W&uuml;therich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd W&uuml;therich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;

import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;
import com.wuetherich.osgi.ds.annotations.internal.builder.ComponentProperty;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.AbstractTypeAccessor;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.Reference;
import com.wuetherich.osgi.ds.annotations.internal.util.Cache;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractComponentDescription implements IComponentDescription {

  /** - */
  protected static final String FIELD_NAME_SERVICE        = "service";

  protected static final String FIELD_NAME_SERVICEFACTORY = "servicefactory";

  /** - */
  protected static final String FIELD_NAME_TARGET         = "target";

  /** - */
  private AbstractTypeAccessor  _typeAccessor;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractComponentDescription}.
   * </p>
   * 
   * @param typeAccessor
   */
  public AbstractComponentDescription(AbstractTypeAccessor typeAccessor) {
    Assert.isNotNull(typeAccessor);

    //
    this._typeAccessor = typeAccessor;
  }

  public void execute() {

    // check activate and deactive names
    if (_typeAccessor.getDeactivateMethodName() != null
        && _typeAccessor.getDeactivateMethodName().equals(_typeAccessor.getActivateMethodName())) {
      throw new DsAnnotationException(String.format("Activate and deactivate method have the same name '%s'.",
          _typeAccessor.getActivateMethodName()));
    }

    // check service implementation
    if (_typeAccessor.getService() != null) {
      for (String service : _typeAccessor.getService()) {
        Assert.isNotNull(service);
        if (!_typeAccessor.isInstanceOf(service)) {
          throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_SERVICE_TYPE, service,
              FIELD_NAME_SERVICE));
        }
      }
    }

    // check service factory / services
    if (_typeAccessor.getService() == null && getTypeAccessor().getAllDirectlyImplementedSuperInterfaces().isEmpty()
        && _typeAccessor.getServiceFactory() != null) {
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_SERVICEFACTORY_DECLARATION,
          FIELD_NAME_SERVICEFACTORY));
    }

    // name
    onSetName(_typeAccessor.getName() != null ? _typeAccessor.getName() : _typeAccessor.getImplementationClassName());

    // enabled
    if (_typeAccessor.isEnabled() != null) {
      onSetEnabled(_typeAccessor.isEnabled());
    }

    // immediate
    if (_typeAccessor.isImmediate() != null) {
      onSetImmediate(_typeAccessor.isImmediate());
    }

    // factory
    if (_typeAccessor.getFactory() != null) {
      onSetFactory(_typeAccessor.getFactory());
    }

    // service factory
    if (_typeAccessor.getServiceFactory() != null) {
      onSetServiceFactory(_typeAccessor.getServiceFactory());
    }

    // services
    onSetService(_typeAccessor.getService() != null ? _typeAccessor.getService() : getTypeAccessor()
        .getAllDirectlyImplementedSuperInterfaces().toArray(new String[0]));

    // configuration pid
    if (_typeAccessor.getConfigurationPid() != null) {
      onSetConfigurationPid(_typeAccessor.getConfigurationPid());
    }

    // configuration policy
    if (_typeAccessor.getConfigurationPolicy() != null) {
      onSetConfigurationPolicy(_typeAccessor.getConfigurationPolicy());
    }

    // activate
    if (_typeAccessor.getActivateMethodName() != null) {
      onSetActivate(_typeAccessor.getActivateMethodName());
    }

    // deactivate
    if (_typeAccessor.getDeactivateMethodName() != null) {
      onSetDeactivate(_typeAccessor.getDeactivateMethodName());
    }

    // modified
    if (_typeAccessor.getModifiedMethodName() != null) {
      onSetModified(_typeAccessor.getModifiedMethodName());
    }

    // property
    if (_typeAccessor.getProperty() != null) {
      processPropertyAttribute(_typeAccessor.getProperty());
    }

    // properties
    if (_typeAccessor.getProperties() != null) {
      for (Object keyValue : _typeAccessor.getProperties()) {
        onAddProperties((String) keyValue);
      }
    }

    // references
    for (Reference ref : _typeAccessor.getReferences()) {
      
      // check bind method name
      checkReferenceBindMethod(ref.getBind());
      
      //
      onAddReference(ref.getService(), ref.getBind(), computeReferenceName(ref.getName(), ref.getBind()), ref.getCardinality(), ref.getPolicy(),
          ref.getPolicyOption(), ref.getUnbind(), ref.getUpdated(), ref.getTarget());
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public AbstractTypeAccessor getTypeAccessor() {
    return _typeAccessor;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getAssociatedSourceFile() {
    return _typeAccessor.getAssociatedSourceFile();
  }

  /**
   * {@inheritDoc}
   */
  public final boolean hasProblems() {
    return _typeAccessor.hasProblems();
  }

  /**
   * {@inheritDoc}
   */
  public final List<DsAnnotationProblem> getProblems() {
    return _typeAccessor.getProblems();
  }

  public abstract void onSetActivate(String methodName);

  public abstract void onSetModified(String methodName);

  public abstract void onSetDeactivate(String methodName);

  public abstract void onSetName(String value);

  public abstract void onSetEnabled(Boolean value);

  public abstract void onSetImmediate(Boolean value);

  public abstract void onSetFactory(String value);

  public abstract void onAddProperties(String value);

  public abstract void onAddProperty(List<String> orderedProperties, Map<String, List<ComponentProperty>> properties);

  public abstract void onSetConfigurationPolicy(String configurationPolicy);

  public abstract void onSetConfigurationPid(String configurationPid);

  public abstract void onSetServiceFactory(Boolean value);

  public abstract void onSetService(String[] services);

  public abstract void onAddReference(String service, String bind, String name, String cardinality, String policy,
      String policyOption, String unbind, String updated, String target);

  /**
   * <p>
   * </p>
   * 
   * @param name
   * @return
   */
  protected boolean isNotEmpty(String name) {
    return name != null && name.trim().length() > 0;
  }

  /**
   * <p>
   * </p>
   *
   * @param name
   * @param bindMethodName
   * @return
   */
  protected String computeReferenceName(String name, String bindMethodName) {

    //
    if (name != null) {
      if (name.length() == 0) {
        throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_REFERENCE_NAME, name));
      } else {
        return name;
      }
    }

    checkReferenceBindMethod(bindMethodName);

    if (bindMethodName.startsWith("add")) { //$NON-NLS-1$
      return bindMethodName.substring("add".length()); //$NON-NLS-1$
    } else if (bindMethodName.startsWith("set")) { //$NON-NLS-1$
      return bindMethodName.substring("set".length()); //$NON-NLS-1$
    } else if (bindMethodName.startsWith("bind")) { //$NON-NLS-1$
      return bindMethodName.substring("bind".length()); //$NON-NLS-1$
    }

    //
    return bindMethodName;
  }
 
  /**
   * <p>
   * </p>
   *
   * @param bindMethodName
   * @return
   */
  private String checkReferenceBindMethod(String bindMethodName) {

    //
    if (bindMethodName.equals("add")) {
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_BIND_METHOD_NAME, "add",
          "addMyService"));
    }

    if (bindMethodName.equals("set")) {
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_BIND_METHOD_NAME, "set",
          "setMyService"));
    }

    if (bindMethodName.equals("bind")) {
      throw new DsAnnotationException(String.format(Messages.ComponentDescription_INVALID_BIND_METHOD_NAME, "bind",
          "bindMyService"));
    }

    return bindMethodName;
  }
  
  /**
   * <p>
   * </p>
   * 
   * @param propertyArray
   */
  private void processPropertyAttribute(Object[] propertyArray) {
    
    // ordered properties
    final List<String> orderedProperties = new LinkedList<String>();
    
    // the property map
    Cache<String, List<ComponentProperty>> propertyMap = new Cache<String, List<ComponentProperty>>() {
      private static final long serialVersionUID = 1L;

      @Override
      protected List<ComponentProperty> create(String key) {
        orderedProperties.add(key);
        return new LinkedList<ComponentProperty>();
      }
    };

    // process the property entries
    for (Object keyValue : propertyArray) {

      //
      String[] strings = ((String) keyValue).split("=");

      //
      if (strings.length < 2) {
        throw new DsAnnotationException(
            String
                .format(
                    "Invalid property definition '%s'. Property definitions must follow the following syntax: name ( : type )? = value. ",
                    ((String) keyValue)));
      }

      String[] nameTypePair = strings[0].split(":");

      //
      ComponentProperty componentProperty = new ComponentProperty();
      if (nameTypePair.length > 1) {
        propertyMap.getOrCreate(nameTypePair[0]).add(componentProperty);
        componentProperty.setName(nameTypePair[0]);
        componentProperty.setType(nameTypePair[1]);
      } else {
        propertyMap.getOrCreate(strings[0]).add(componentProperty);
        componentProperty.setName(strings[0]);
      }

      //
      componentProperty.setValue(strings[1]);
    }

    //
    onAddProperty(orderedProperties, propertyMap);
  }
}
