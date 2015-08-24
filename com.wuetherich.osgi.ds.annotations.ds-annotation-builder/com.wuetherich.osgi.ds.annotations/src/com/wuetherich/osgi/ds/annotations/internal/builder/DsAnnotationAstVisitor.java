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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.wuetherich.osgi.ds.annotations.DsAnnotationVersion;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationException;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.ComponentDescriptionFactory;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.Reference;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl.AbstractComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.prefs.DsAnnotationsPreferences;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationAstVisitor extends AbstractDsAnnotationAstVisitor {

  /** the descriptions */
  private Map<TypeDeclaration, IComponentDescription> _descriptions;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationAstVisitor}.
   * </p>
   */
  public DsAnnotationAstVisitor(IProject project) {
    super(project);

    // create the description map
    _descriptions = new HashMap<TypeDeclaration, IComponentDescription>();
  }

  @Override
  public void endVisit(TypeDeclaration node) {

    try {
      IComponentDescription componentDescription = getCurrentComponentDescription();
      if (componentDescription != null) {
        ((AbstractComponentDescription) getCurrentComponentDescription()).execute();
      }
    } catch (Exception e) {
      handleException(node, e);
    }

    super.endVisit(node);
  }

  /**
   * <p>
   * Returns all the component descriptions.
   * </p>
   * 
   * @return all the component descriptions.
   */
  public Collection<IComponentDescription> getComponentDescriptions() {
    return _descriptions.values();
  }

  @Override
  protected void handleReferenceAnnotation(MarkerAnnotation node) {
    String service = getCurrentMethodDeclaration().resolveBinding().getParameterTypes()[0].getBinaryName();
    String bind = getCurrentMethodDeclaration().getName().getFullyQualifiedName();

    Reference reference = new Reference(service, bind, null, null, null, null, null, null, null);
    getCurrentComponentDescription().getTypeAccessor().getReferences().add(reference);
  }

  @Override
  protected void handleReferenceAnnotation(NormalAnnotation node) {
    String name = null;
    String cardinality = null;
    String policy = null;
    String policyOption = null;
    String unbind = null;
    String updated = null;
    String target = null;

    String service = getCurrentMethodDeclaration().resolveBinding().getParameterTypes()[0].getBinaryName();
    String bind = getCurrentMethodDeclaration().getName().getFullyQualifiedName();

    for (Object object : node.values()) {

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
      else if ("policyOption".equals(valueName)) {
        IVariableBinding variableBinding = (IVariableBinding) pair.resolveMemberValuePairBinding().getValue();
        policyOption = variableBinding.getName().toLowerCase();
      }
      //
      else if ("service".equals(valueName)) {
        service = ((ITypeBinding) pair.resolveMemberValuePairBinding().getValue()).getBinaryName();
      }
      //
      else if ("unbind".equals(valueName)) {
        unbind = pair.resolveMemberValuePairBinding().getValue().toString();
      }
      //
      else if ("updated".equals(valueName)) {
        updated = pair.resolveMemberValuePairBinding().getValue().toString();
      }
      //
      else if ("target".equals(valueName)) {
        target = pair.resolveMemberValuePairBinding().getValue().toString();
      }
    }

    //
    Reference reference = new Reference(service, bind, name, cardinality, policy, policyOption, unbind, updated, target);
    getCurrentComponentDescription().getTypeAccessor().getReferences().add(reference);
  }

  @Override
  protected void handleModifiedAnnotation(MarkerAnnotation node) {
    getCurrentComponentDescription().getTypeAccessor().setModifiedMethodName(
        getCurrentMethodDeclaration().getName().getFullyQualifiedName());
  }

  @Override
  protected void handleDeactivateAnnotation(MarkerAnnotation node) {
    getCurrentComponentDescription().getTypeAccessor().setDeactivateMethodName(
        getCurrentMethodDeclaration().getName().getFullyQualifiedName());
  }

  @Override
  protected void handleActivateAnnotation(MarkerAnnotation node) {
    getCurrentComponentDescription().getTypeAccessor().setActivateMethodName(
        getCurrentMethodDeclaration().getName().getFullyQualifiedName());
  }

  @Override
  protected void handleComponentAnnotation(MarkerAnnotation node) {
    createNewComponentDeclaration(node);
  }

  @Override
  protected void handleComponentAnnotation(NormalAnnotation node) {
    createNewComponentDeclaration(node);

    //
    for (Object object : node.values()) {
      MemberValuePair pair = (MemberValuePair) object;

      //
      if ("name".equals(pair.getName().toString())) {
        getCurrentComponentDescription().getTypeAccessor().setName(
            (String) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("enabled".equals(pair.getName().toString())) {
        getCurrentComponentDescription().getTypeAccessor().setEnabled(
            (Boolean) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("immediate".equals(pair.getName().toString())) {
        getCurrentComponentDescription().getTypeAccessor().setImmediate(
            (Boolean) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("factory".equals(pair.getName().toString())) {
        getCurrentComponentDescription().getTypeAccessor().setFactory(
            (String) pair.resolveMemberValuePairBinding().getValue());
      }
      //
      else if ("configurationPid".equals(pair.getName().toString())) {
        Object[] pids = (Object[]) pair.resolveMemberValuePairBinding().getValue();
        getCurrentComponentDescription().getTypeAccessor().setConfigurationPid(pids);
      }
      //
      else if ("configurationPolicy".equals(pair.getName().toString())) {
        IVariableBinding variableBinding = (IVariableBinding) pair.resolveMemberValuePairBinding().getValue();
        getCurrentComponentDescription().getTypeAccessor().setConfigurationPolicy(
            variableBinding.getName().toLowerCase());
      }
      //
      else if ("property".equals(pair.getName().toString())) {
        Object[] properties = (Object[]) pair.resolveMemberValuePairBinding().getValue();
        getCurrentComponentDescription().getTypeAccessor().setProperty(properties);
      }
      //
      else if ("properties".equals(pair.getName().toString())) {
        Object[] properties = (Object[]) pair.resolveMemberValuePairBinding().getValue();
        getCurrentComponentDescription().getTypeAccessor().setProperties(properties);
      }
      //
      else if ("service".equals(pair.getName().toString())) {
        Object[] objects = (Object[]) pair.resolveMemberValuePairBinding().getValue();
        List<String> services = new LinkedList<String>();
        for (Object tb : objects) {
          ITypeBinding typeBinding = (ITypeBinding) tb;
          services.add(typeBinding.getBinaryName());
        }
        getCurrentComponentDescription().getTypeAccessor().setService(services.toArray(new String[0]));
      }
      //
      else if ("servicefactory".equals(pair.getName().toString())) {
        getCurrentComponentDescription().getTypeAccessor().setServiceFactory(
            (Boolean) pair.resolveMemberValuePairBinding().getValue());
      }
    }
  }

  public void handleException(ASTNode astNode, Exception e) {

    //
    if (e instanceof DsAnnotationException) {

      //
      DsAnnotationException annotationException = (DsAnnotationException) e;

      //
      if (annotationException.hasAnnotationField()) {

        if (astNode instanceof NormalAnnotation) {

          for (Object object : ((NormalAnnotation) astNode).values()) {

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
          new DsAnnotationProblem(e.getMessage() != null ? e.getMessage() : "Error", astNode.getStartPosition(),
              astNode.getStartPosition() + astNode.getLength()));
    }
  }

  /**
   * <p>
   * Helper method. Returns the current {@link AbstractComponentDescription}.
   * </p>
   * 
   * @return the current {@link AbstractComponentDescription}.
   */
  private IComponentDescription getCurrentComponentDescription() {
    return _descriptions.get(getCurrentTypeDeclarationStack().peek());
  }

  /**
   * <p>
   * </p>
   * 
   */
  private void createNewComponentDeclaration(Annotation annotation) {

    DetectDsAnnotationVersionAstVisitor visitor = new DetectDsAnnotationVersionAstVisitor(getProject());
    annotation.getRoot().accept(visitor);

    //
    DsAnnotationVersion projectDsVersion = DsAnnotationsPreferences.getDsAnnotationVersion(getProject());
    DsAnnotationVersion effectiveVersion = visitor.getEffectiveVersion();

    //
    IComponentDescription componentDescription = ComponentDescriptionFactory.createComponentDescription(
        new TypeDeclarationAccessor(getCurrentTypeDeclarationStack().peek()), getProject(),
        effectiveVersion.greaterThan(projectDsVersion) ? projectDsVersion : effectiveVersion);

    _descriptions.put(getCurrentTypeDeclarationStack().peek(), componentDescription);

    // throw exception
    if (visitor.hasAnnotationException()) {
      throw visitor.getAnnotationException();
    }

    //
    if (visitor.hasSpecifiedXmlns()
        && visitor.getSpecifiedXmlns().greaterThan(DsAnnotationsPreferences.getDsAnnotationVersion(getProject()))) {

      //
      throw new DsAnnotationException(
          String
              .format("XML namespace '%s' is higher than the XML namespace '%s' defined in the preferences.", visitor
                  .getSpecifiedXmlns().getXmlns(), DsAnnotationsPreferences.getDsAnnotationVersion(getProject())
                  .getXmlns()));
    }
  }
}
