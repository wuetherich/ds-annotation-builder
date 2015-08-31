/*******************************************************************************
 * Copyright (c) 2015 Gerd Wütherich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd Wütherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.internal.builder;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.pde.ds.annotations.DsAnnotationVersion;
import org.eclipse.pde.ds.annotations.internal.DsAnnotationException;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DetectDsAnnotationVersionAstVisitor extends AbstractDsAnnotationAstVisitor {

  /** the descriptions */
  private DsAnnotationVersion   _xmlns = DsAnnotationVersion.V_1_0;

  /** - */
  private DsAnnotationVersion   _specifiedXmlns;

  /** - */
  private DsAnnotationException _annotationException;

  /**
   * <p>
   * Creates a new instance of type {@link DetectDsAnnotationVersionAstVisitor}.
   * </p>
   */
  public DetectDsAnnotationVersionAstVisitor(IProject project) {
    super(project);
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public DsAnnotationVersion getDsAnnotationVersion() {
    return _xmlns;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public DsAnnotationException getAnnotationException() {
    return _annotationException;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public DsAnnotationVersion getSpecifiedXmlns() {
    return _specifiedXmlns;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean hasSpecifiedXmlns() {
    return _specifiedXmlns != null;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean hasAnnotationException() {
    return _annotationException != null;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public DsAnnotationVersion getEffectiveVersion() {
    return _specifiedXmlns != null ? _specifiedXmlns : _xmlns;
  }

  @Override
  protected void handleComponentAnnotation(MarkerAnnotation node) {
    //
  }

  @Override
  protected void handleComponentAnnotation(NormalAnnotation node) {
    for (Object object : node.values()) {
      MemberValuePair pair = (MemberValuePair) object;

      // handle xmlns
      if ("xmlns".equals(pair.getName().toString())) {
        String xmlns = (String) pair.resolveMemberValuePairBinding().getValue();
        _specifiedXmlns = DsAnnotationVersion.getFromNamespace(xmlns);
        if (_specifiedXmlns == null) {
          _annotationException = new DsAnnotationException(String.format("Invalid namespace definition '%s'.", xmlns));
        }
      }
      //
      else if ("configurationPid".equals(pair.getName().toString())) {
        setUpTo(DsAnnotationVersion.V_1_2);
      }
      //
      else if ("configurationPolicy".equals(pair.getName().toString())) {
        setUpTo(DsAnnotationVersion.V_1_1);
      }
    }
  }

  @Override
  protected void handleReferenceAnnotation(MarkerAnnotation node) {

    //
    checkSignature();

    //
    checkReferenceForUpdateMethod();
  }

  @Override
  protected void handleReferenceAnnotation(NormalAnnotation node) {

    for (Object object : node.values()) {
      MemberValuePair pair = (MemberValuePair) object;
      String valueName = pair.getName().toString();

      //
      if ("updated".equals(valueName)) {
        setUpTo(DsAnnotationVersion.V_1_2);
        return;
      }
      //
      else if ("policyOption".equals(valueName)) {
        setUpTo(DsAnnotationVersion.V_1_2);
        return;
      }
    }

    //
    checkSignature();

    //
    checkReferenceForUpdateMethod();
  }

  @Override
  protected void handleModifiedAnnotation(MarkerAnnotation node) {
    setUpTo(DsAnnotationVersion.V_1_1);
  }

  @Override
  protected void handleDeactivateAnnotation(MarkerAnnotation node) {
    setUpTo(DsAnnotationVersion.V_1_1);
  }

  @Override
  protected void handleActivateAnnotation(MarkerAnnotation node) {
    setUpTo(DsAnnotationVersion.V_1_1);
  }

  @Override
  protected void handleException(ASTNode node, Exception e) {
    //
  }

  /**
   * <p>
   * </p>
   * 
   * @param version
   */
  private void setUpTo(DsAnnotationVersion version) {
    if (version.greaterThan(_xmlns)) {
      _xmlns = version;
    }
  }

  private void checkReferenceForUpdateMethod() {
    String methodName = getCurrentMethodDeclaration().getName().getFullyQualifiedName();
    String updatedMethodName = computeUpdatedMethodName(methodName);
    for (MethodDeclaration methodDeclaration : getCurrentTypeDeclarationStack().peek().getMethods()) {
      if (updatedMethodName.equals(methodDeclaration.getName().getFullyQualifiedName())) {
        setUpTo(DsAnnotationVersion.V_1_2);
      }
    }
  }

  private void checkSignature() {

    // check bind method
    if (isSecondParameterOfTypeMap(getCurrentMethodDeclaration())) {
      setUpTo(DsAnnotationVersion.V_1_1);
      return;
    }

    //
    String unbindName = computeUnbindMethodName(getCurrentMethodDeclaration().getName().getFullyQualifiedName());
    for (MethodDeclaration methodDeclaration : getCurrentTypeDeclarationStack().peek().getMethods()) {
      if (unbindName.equals(methodDeclaration.getName().getFullyQualifiedName())) {
        if (isSecondParameterOfTypeMap(methodDeclaration)) {
          setUpTo(DsAnnotationVersion.V_1_1);
          return;
        }
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param methodDeclaration
   * @return
   */
  private boolean isSecondParameterOfTypeMap(MethodDeclaration methodDeclaration) {

    //
    if (methodDeclaration.parameters().size() == 2) {
      SingleVariableDeclaration declaration = (SingleVariableDeclaration) methodDeclaration.parameters().get(1);
      return declaration.getType().resolveBinding().getName().contains(Map.class.getSimpleName());
    }

    //
    return false;
  }
}
