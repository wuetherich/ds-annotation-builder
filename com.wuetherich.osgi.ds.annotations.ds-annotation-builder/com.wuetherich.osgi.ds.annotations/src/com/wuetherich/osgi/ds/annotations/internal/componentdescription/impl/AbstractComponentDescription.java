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
  protected static final String     MSG_NO_SUPERTYPE_S                = "NO SUPERTYPE '%s'.";

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

  /**
   * <p>
   * </p>
   * 
   * @return
   */
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
  public final void setServiceFactory(Boolean value) {
    onSetServiceFactory(value);
  }

  @Override
  public final void addProperty(Map<String, List<ComponentProperty>> properties) {
    onAddProperty(properties);
  }

  @Override
  public final void addProperties(String keyValue) {
    // TODO Auto-generated method stub

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
  public static boolean isNotEmpty(String name) {
    return name != null && name.trim().length() > 0;
  }

  /**
   * <p>
   * </p>
   * 
   * @param bindName
   * @return
   */
  public static String computeUnbindMethodName(String bindName) {

    //
    Assert.isNotNull(bindName);

    //
    if (bindName.startsWith("set")) {
      return "unset" + bindName.substring("set".length());
    } else if (bindName.startsWith("add")) {
      return "remove" + bindName.substring("add".length());
    } else {
      return "un" + bindName;
    }
  }

  public static String computeUpdatedMethodName(String bindName) {

    //
    Assert.isNotNull(bindName);

    //
    if (bindName.startsWith("set")) {
      return "updated" + bindName.substring("set".length());
    } else if (bindName.startsWith("add")) {
      return "updated" + bindName.substring("add".length());
    } else if (bindName.startsWith("bind")) {
      return "updated" + bindName.substring("bind".length());
    } else {
      return "updated" + bindName;
    }
  }
}
