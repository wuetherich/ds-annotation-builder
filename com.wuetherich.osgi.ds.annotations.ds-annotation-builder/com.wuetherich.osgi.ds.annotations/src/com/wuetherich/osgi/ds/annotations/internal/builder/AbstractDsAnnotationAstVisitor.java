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
package com.wuetherich.osgi.ds.annotations.internal.builder;

import java.util.Stack;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public abstract class AbstractDsAnnotationAstVisitor extends ASTVisitor {

  /** the current type declaration */
  private Stack<TypeDeclaration> _currentTypeDeclaration;

  /** the current method declaration */
  private MethodDeclaration      _currentMethodDeclaration;

  /** - */
  private boolean                _hasTypes = false;

  /** - */
  private IProject               _project;

  /**
   * <p>
   * Creates a new instance of type {@link AbstractDsAnnotationAstVisitor}.
   * </p>
   */
  public AbstractDsAnnotationAstVisitor(IProject project) {

    //
    _currentTypeDeclaration = new Stack<TypeDeclaration>();

    //
    _project = project;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public Stack<TypeDeclaration> getCurrentTypeDeclarationStack() {
    return _currentTypeDeclaration;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public MethodDeclaration getCurrentMethodDeclaration() {
    return _currentMethodDeclaration;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public final IProject getProject() {
    return _project;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public final boolean hasTypes() {
    return _hasTypes;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(TypeDeclaration node) {
    _hasTypes = true;
    _currentTypeDeclaration.push(node);
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endVisit(TypeDeclaration node) {
    _currentTypeDeclaration.pop();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(MethodDeclaration node) {
    _currentMethodDeclaration = node;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endVisit(MethodDeclaration node) {
    _currentMethodDeclaration = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(MarkerAnnotation node) {

    if (!_currentTypeDeclaration.isEmpty()) {
      // handle annotation
      try {
        if (isComponentAnnotation(node)) {
          handleComponentAnnotation(node);
        } else if (isReferenceAnnotation(node)) {
          handleReferenceAnnotation(node);
        } else if (isActivateAnnotation(node)) {
          handleActivateAnnotation(node);
        } else if (isDeactivateAnnotation(node)) {
          handleDeactivateAnnotation(node);
        } else if (isModifiedAnnotation(node)) {
          handleModifiedAnnotation(node);
        }
      } catch (Exception e) {
        handleException(node, e);
      }
    }

    // only visit types and methods
    return _currentMethodDeclaration == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(NormalAnnotation node) {

    // handle annotation
    if (!_currentTypeDeclaration.isEmpty()) {
      try {
        if (isComponentAnnotation(node)) {
          handleComponentAnnotation(node);
        } else if (isReferenceAnnotation(node)) {
          handleReferenceAnnotation(node);
        }
      } catch (Exception e) {
        handleException(node, e);
      }
    }

    // only visit types and methods
    return _currentMethodDeclaration == null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(SingleMemberAnnotation node) {

    // handle annotation
    if (!_currentTypeDeclaration.isEmpty()) {
      if (isComponentAnnotation(node)) {
        throw new UnsupportedOperationException();
      } else if (isReferenceAnnotation(node)) {
        throw new UnsupportedOperationException();
      }
    }

    // only visit types and methods
    return _currentMethodDeclaration == null;
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

  /**
   * <p>
   * </p>
   *
   * @param bindName
   * @return
   */
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

  protected abstract void handleReferenceAnnotation(MarkerAnnotation node);

  protected abstract void handleReferenceAnnotation(NormalAnnotation node);

  protected abstract void handleModifiedAnnotation(MarkerAnnotation node);

  protected abstract void handleDeactivateAnnotation(MarkerAnnotation node);

  protected abstract void handleActivateAnnotation(MarkerAnnotation node);

  protected abstract void handleComponentAnnotation(MarkerAnnotation node);

  protected abstract void handleComponentAnnotation(NormalAnnotation node);

  protected abstract void handleException(ASTNode node, Exception e);

  /**
   * <p>
   * </p>
   * 
   * @param annotation
   * @return
   */
  private boolean isReferenceAnnotation(Annotation annotation) {
    return getDsAnnotationFQName(annotation).equals(Reference.class.getName());
  }

  /**
   * <p>
   * </p>
   * 
   * @param annotation
   * @return
   */
  private boolean isComponentAnnotation(Annotation annotation) {
    return getDsAnnotationFQName(annotation).equals(Component.class.getName());
  }

  /**
   * <p>
   * </p>
   * 
   * @param annotation
   * @return
   */
  private boolean isActivateAnnotation(Annotation annotation) {
    return getDsAnnotationFQName(annotation).equals(Activate.class.getName());
  }

  /**
   * <p>
   * </p>
   * 
   * @param annotation
   * @return
   */
  private boolean isDeactivateAnnotation(Annotation annotation) {
    return getDsAnnotationFQName(annotation).equals(Deactivate.class.getName());
  }

  /**
   * <p>
   * </p>
   * 
   * @param annotation
   * @return
   */
  private boolean isModifiedAnnotation(Annotation annotation) {
    return getDsAnnotationFQName(annotation).equals(Modified.class.getName());
  }

  /**
   * <p>
   * </p>
   * 
   * @param annotation
   * @return
   */
  private String getDsAnnotationFQName(Annotation annotation) {
    return annotation.resolveTypeBinding().getQualifiedName();
  }
}
