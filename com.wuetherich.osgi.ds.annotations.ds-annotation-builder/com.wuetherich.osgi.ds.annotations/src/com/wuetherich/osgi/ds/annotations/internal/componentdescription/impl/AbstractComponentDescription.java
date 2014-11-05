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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;
import com.wuetherich.osgi.ds.annotations.internal.builder.ComponentProperty;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.util.GenericCache;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractComponentDescription implements IComponentDescription {

  /** - */
  protected static final String     FIELD_NAME_SERVICE                = "service";

  /** - */
  protected static final String     MSG_NO_SUPERTYPE_S                = "Invalid service type. The specified component must implement/extend type '%s'.";

  /** - */
  protected static final String     FIELD_NAME_TARGET                 = "target";

  /** - */
  protected static final String     MSG_INVALID_FILTER_S              = "Invalid filter '%s'.";

  /** */
  protected static final String     MSG_NON_EXISTING_UNBIND_METHOD_S  = "Non existing unbind method '%s'.";

  /** */
  protected static final String     MSG_NON_EXISTING_UPDATED_METHOD_S = "Non existing updated method '%s'.";

  /** - */
  private List<DsAnnotationProblem> _problems;

  /** - */
  private TypeDeclarationReader     _typeDeclarationReader;

  /** - */
  private String                    _sourceFile;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractComponentDescription}.
   * </p>
   * 
   * @param typeDeclaration
   */
  public AbstractComponentDescription(TypeDeclaration typeDeclaration) {
    Assert.isNotNull(typeDeclaration);

    //
    this._typeDeclarationReader = new TypeDeclarationReader(typeDeclaration);

    //
    try {
      CompilationUnit compilationUnit = (CompilationUnit) typeDeclaration.getParent();
      _sourceFile = compilationUnit.getTypeRoot().getCorrespondingResource().getProjectRelativePath()
          .toPortableString();
    } catch (JavaModelException e) {
      //
    }

    _problems = new LinkedList<DsAnnotationProblem>();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public TypeDeclarationReader getTypeDeclarationReader() {
    return _typeDeclarationReader;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getImplementationClassName() {
    return _typeDeclarationReader.getImplementationClassName();
  }

  @Override
  public String getSourceFile() {
    return _sourceFile;
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

  /**
   * {@inheritDoc}
   */
  public final void setDeactivateMethod(String methodName) {

    //
    if (getActivate() != null && getActivate().equals(methodName)) {
      throw new DsAnnotationException(String.format("Activate and deactivate method have the same name '%s'.",
          methodName));
    }

    //
    onSetDeactivate(methodName);
  }

  public final void setActivateMethod(String methodName) {

    //
    if (getDeactivate() != null && getDeactivate().equals(methodName)) {
      throw new DsAnnotationException(String.format("Activate and deactivate method have the same name '%s'.",
          methodName));
    }

    //
    onSetActivate(methodName);
  }

  @Override
  public final void setModified(String methodName) {
    onSetModified(methodName);
  }

  @Override
  public final void setName(String value) {
    onSetName(value);
  }

  @Override
  public final void setEnabled(Boolean value) {
    onSetEnabled(value);
  }

  @Override
  public final void setImmediate(Boolean value) {
    onSetImmediate(value);
  }

  @Override
  public final void setFactory(String value) {
    onSetFactory(value);
  }

  @Override
  public final void setConfigurationPolicy(String configurationPolicy) {
    onSetConfigurationPolicy(configurationPolicy);
  }
  
  @Override
  public final void setConfigurationPid(String configurationPid) {
    onSetConfigurationPid(configurationPid);
  }

  @Override
  public final void setServiceFactory(Boolean value) {
    onSetServiceFactory(value);
  }

  @Override
  public final void addProperty(Object[] properties) {
    
    //
    GenericCache<String, List<ComponentProperty>> propertyMap = new GenericCache<String, List<ComponentProperty>>() {
      private static final long serialVersionUID = 1L;

      @Override
      protected List<ComponentProperty> create(String key) {
        return new LinkedList<ComponentProperty>();
      }
    };

    // process the property entries
    for (Object keyValue : properties) {

      //
      String[] strings = ((String) keyValue).split("=");
      
      //
      if (strings.length < 2) {
        throw new DsAnnotationException(String.format("Invalid property definition '%s'. Property definitions must follow the following syntax: name ( �:� type )? �=� value. ",
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
    onAddProperty(propertyMap);
  }

  @Override
  public final void addProperties(String value) {
    onAddProperties(value);
  }

  /**
   * <p>
   * </p>
   * 
   * @param services
   */
  public final void setService(String[] services) {

    //
    if (services != null) {
      for (String service : services) {
        Assert.isNotNull(service);
        if (!_typeDeclarationReader.isInstanceOf(service)) {
          throw new DsAnnotationException(String.format(MSG_NO_SUPERTYPE_S, service, FIELD_NAME_SERVICE));
        }
      }
    }

    onSetService(services);
  }

  public final void addReference(String service, String bind, String name, String cardinality, String policy,
      String policyOption, String unbind, String updated, String target) {

    onAddReference(service, bind, name, cardinality, policy, policyOption, unbind, updated, target);
  }

  public abstract String getActivate();

  public abstract String getDeactivate();

  public abstract void onSetActivate(String methodName);

  public abstract void onSetModified(String methodName);

  public abstract void onSetDeactivate(String methodName);

  public abstract void onSetName(String value);

  public abstract void onSetEnabled(Boolean value);

  public abstract void onSetImmediate(Boolean value);

  public abstract void onSetFactory(String value);

  public abstract void onAddProperties(String value);

  public abstract void onAddProperty(Map<String, List<ComponentProperty>> properties);

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
  public boolean isNotEmpty(String name) {
    return name != null && name.trim().length() > 0;
  }
}
