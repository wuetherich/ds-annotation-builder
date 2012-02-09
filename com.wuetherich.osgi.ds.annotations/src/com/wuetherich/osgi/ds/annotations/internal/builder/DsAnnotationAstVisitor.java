/*******************************************************************************
 * Copyright (c) 2012 Gerd Wuetherich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd Wuetherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal.builder;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.wuetherich.osgi.ds.annotations.internal.AbstractComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationAstVisitor extends ASTVisitor {

  /** the current type declaration */
  private TypeDeclaration                                    _currentTypeDeclaration;

  /** the current method declaration */
  private MethodDeclaration                                  _currentMethodDeclaration;

  /** the descriptions */
  private Map<TypeDeclaration, AbstractComponentDescription> _descriptions;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationAstVisitor}.
   * </p>
   */
  public DsAnnotationAstVisitor() {

    // create the description map
    _descriptions = new HashMap<TypeDeclaration, AbstractComponentDescription>();
  }

  /**
   * <p>
   * Returns all the component descriptions.
   * </p>
   * 
   * @return all the component descriptions.
   */
  public Collection<AbstractComponentDescription> getComponentDescriptions() {
    return _descriptions.values();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(TypeDeclaration node) {
    _currentTypeDeclaration = node;
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void endVisit(TypeDeclaration node) {
    _currentTypeDeclaration = null;
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

    // handle annotation
    if (isDsAnnotation(node)) {
      handleDsAnnotation(node);
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
    if (isDsAnnotation(node)) {
      handleDsAnnotation(node);
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
    if (isDsAnnotation(node)) {
      handleDsAnnotation(node);
    }

    // only visit types and methods
    return _currentMethodDeclaration == null;
  }

  /**
   * <p>
   * </p>
   * 
   * @param node
   */
  private void handleDsAnnotation(Annotation node) {

    try {

      //
      if (_currentTypeDeclaration != null) {

        //
        if (node.resolveTypeBinding().getQualifiedName().equals(Component.class.getName())) {

          //
          _descriptions.put(_currentTypeDeclaration, new AstBasedComponentDescription(_currentTypeDeclaration));
          getCurrentComponentDescription().setComponentDefaults();

          //
          if (node.isNormalAnnotation()) {
            handleNormalComponentAnnotation((NormalAnnotation) node);
          } else if (node.isSingleMemberAnnotation()) {
            //
          } else if (node.isMarkerAnnotation()) {
            //
          }
        }

        if (_currentMethodDeclaration != null) {

          //
          if (node.resolveTypeBinding().getQualifiedName().equals(Activate.class.getName())) {
            getCurrentComponentDescription().setActivateMethod(
                _currentMethodDeclaration.getName().getFullyQualifiedName());
          }

          //
          else if (node.resolveTypeBinding().getQualifiedName().equals(Deactivate.class.getName())) {
            getCurrentComponentDescription().setDeactivateMethod(
                _currentMethodDeclaration.getName().getFullyQualifiedName());
          }

          //
          else if (node.resolveTypeBinding().getQualifiedName().equals(Modified.class.getName())) {
            getCurrentComponentDescription().setModifiedMethod(
                _currentMethodDeclaration.getName().getFullyQualifiedName());
          }

          //
          else if (node.resolveTypeBinding().getQualifiedName().equals(Reference.class.getName())) {

            //
            if (node.isNormalAnnotation()) {
              handleNormalReferenceAnnotation((NormalAnnotation) node);
            } else if (node.isSingleMemberAnnotation()) {
              //
            } else if (node.isMarkerAnnotation()) {

              //
              String service = _currentMethodDeclaration.resolveBinding().getParameterTypes()[0].getBinaryName();
              String bind = _currentMethodDeclaration.getName().getFullyQualifiedName();

              //
              getCurrentComponentDescription().addReference(service, bind, null, null, null, null, null);
            }
          }
        }
      }

    } catch (Exception e) {

      ASTNode astNode = node;

      //
      if (e instanceof DsAnnotationException) {

        //
        DsAnnotationException annotationException = (DsAnnotationException) e;

        //
        if (annotationException.hasAnnotationField()) {

          astNode = node;

          if (node instanceof NormalAnnotation) {

            for (Object object : ((NormalAnnotation) node).values()) {

              //
              MemberValuePair pair = (MemberValuePair) object;
              String valueName = pair.getName().toString();

              //
              if (valueName.equals(annotationException.getAnnotationField())) {
                astNode = pair;
                break;
              }
            }
          }
        }
      }

      //
      if (getCurrentComponentDescription() != null) {
        getCurrentComponentDescription().getProblems().add(
            new DsAnnotationProblem(e.getMessage() != null ? e.getMessage() : "Unknown error in annotation", astNode
                .getStartPosition(), astNode.getStartPosition() + astNode.getLength()));
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param normalAnnotation
   */
  private void handleNormalReferenceAnnotation(NormalAnnotation normalAnnotation) {

    String name = null;
    String cardinality = null;
    String policy = null;
    String unbind = null;
    String target = null;

    String service = _currentMethodDeclaration.resolveBinding().getParameterTypes()[0].getBinaryName();
    String bind = _currentMethodDeclaration.getName().getFullyQualifiedName();

    for (Object object : normalAnnotation.values()) {

      //
      MemberValuePair pair = (MemberValuePair) object;
      String valueName = pair.getName().toString();

      if ("name".equals(valueName)) {
        name = pair.resolveMemberValuePairBinding().getValue().toString();
      }
      //
      else if ("cardinality".equals(valueName)) {

        IVariableBinding variableBinding = (IVariableBinding) pair.resolveMemberValuePairBinding().getValue();
        cardinality = variableBinding.getName().toLowerCase();
      }
      //
      else if ("policy".equals(valueName)) {
        policy = ((IVariableBinding) pair.resolveMemberValuePairBinding().getValue()).getName();
      }
      //
      else if ("service".equals(valueName)) {
        service = ((ITypeBinding) pair.resolveMemberValuePairBinding().getValue()).getBinaryName();
      }
      //
      else if ("unbind".equals(valueName)) {
        unbind = pair.resolveMemberValuePairBinding().toString();
      }
      //
      else if ("target".equals(valueName)) {
        target = pair.resolveMemberValuePairBinding().getValue().toString();
      }
    }

    //
    getCurrentComponentDescription().addReference(service, bind, name, cardinality, policy, unbind, target);
  }

  /**
   * <p>
   * </p>
   * 
   * @param normalAnnotation
   */
  private void handleNormalComponentAnnotation(NormalAnnotation normalAnnotation) {

    for (Object object : normalAnnotation.values()) {

      //
      MemberValuePair pair = (MemberValuePair) object;
      String valueName = pair.getName().toString();

      //
      if ("name".equals(valueName)) {
        getCurrentComponentDescription().setName((String) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("enabled".equals(valueName)) {
        getCurrentComponentDescription().setEnabled((Boolean) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("immediate".equals(valueName)) {
        getCurrentComponentDescription().setImmediate((Boolean) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("factory".equals(valueName)) {
        getCurrentComponentDescription().setFactory((String) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("configurationPolicy".equals(valueName)) {
        IVariableBinding variableBinding = (IVariableBinding) pair.resolveMemberValuePairBinding().getValue();
        getCurrentComponentDescription().setConfigurationPolicy(variableBinding.getName().toLowerCase());
      }
      //
      else if ("property".equals(valueName)) {
        for (Object keyValue : (Object[]) pair.resolveMemberValuePairBinding().getValue()) {
          getCurrentComponentDescription().addProperty((String) keyValue);
        }
      }
      //
      else if ("properties".equals(valueName)) {
        for (Object keyValue : (Object[]) pair.resolveMemberValuePairBinding().getValue()) {
          getCurrentComponentDescription().addProperties((String) keyValue);
        }
      }
      //
      else if ("service".equals(valueName)) {

        Object[] objects = (Object[]) pair.resolveMemberValuePairBinding().getValue();
        List<String> services = new LinkedList<String>();
        for (Object tb : objects) {
          ITypeBinding typeBinding = (ITypeBinding) tb;
          services.add(typeBinding.getBinaryName());
        }
        getCurrentComponentDescription().setService(services.toArray(new String[0]));
      }
      //
      else if ("servicefactory".equals(valueName)) {
        getCurrentComponentDescription().setServiceFactory((Boolean) pair.resolveMemberValuePairBinding().getValue());
      }

    }
  }

  /**
   * <p>
   * Helper method. Returns <code>true</code> if the specified annotation is a DS annotation, <code>false</code>
   * otherwise.
   * </p>
   * 
   * @return <code>true</code> if the specified annotation is a DS annotation, <code>false</code> otherwise.
   */
  private boolean isDsAnnotation(Annotation annotation) {

    //
    ITypeBinding typeBinding = annotation.resolveTypeBinding();

    //
    if (typeBinding != null) {
      return Component.class.getPackage().getName().equals(annotation.resolveTypeBinding().getPackage().getName());
    } else {
      return false;
    }
  }

  /**
   * <p>
   * Helper method. Returns the current {@link AbstractComponentDescription}.
   * </p>
   * 
   * @return the current {@link AbstractComponentDescription}.
   */
  private AbstractComponentDescription getCurrentComponentDescription() {
    return _descriptions.get(_currentTypeDeclaration);
  }
}
