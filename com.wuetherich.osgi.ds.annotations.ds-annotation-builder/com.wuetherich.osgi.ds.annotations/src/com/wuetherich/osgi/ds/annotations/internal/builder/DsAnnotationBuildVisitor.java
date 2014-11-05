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

import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.compiler.IProblem;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescription;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationBuildVisitor implements IResourceVisitor, IResourceDeltaVisitor {

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(IResource resource) throws CoreException {

    //
    if (resource.getType() != IResource.FILE) {
      return true;
    }

    //
    handle(resource);

    //
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean visit(IResourceDelta delta) throws CoreException {

    //
    if (delta.getKind() == IResourceDelta.ADDED) {

      //
      return visit(delta.getResource());

    } else if (delta.getKind() == IResourceDelta.REMOVED) {

      // //
      // if (delta.getResource().getName().endsWith(".xml") && new
      // Path(Constants.COMPONENT_DESCRIPTION_FOLDER).isPrefixOf(delta.getResource().getProjectRelativePath())) {
      // ComponentDescriptionWriter.updateSourceFile(delta.getResource().getProject(),
      // delta.getResource().getProjectRelativePath());
      // }

      //
      ComponentDescriptionWriter.deleteGeneratedFiles(delta.getResource().getProject(), delta.getResource()
          .getProjectRelativePath());

    } else if (delta.getKind() == IResourceDelta.CHANGED) {
      return visit(delta.getResource());
    }

    return true;
  }

  /**
   * <p>
   * </p>
   * 
   * @param resource
   * @throws CoreException
   */
  private void handle(IResource resource) throws CoreException {

    //
    if (new Path(Constants.COMPONENT_DESCRIPTION_FOLDER).isPrefixOf(resource.getProjectRelativePath())) {
      ManifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(resource.getProject());
    }

    // Only handle Java source files here...
    if (!resource.getName().endsWith(".java")) {
      return;
    }

    // get the corresponding java element
    IJavaElement element = JavaCore.create(resource);
    IJavaProject javaProject = JavaCore.create(resource.getProject());
    if (element == null || !javaProject.isOnClasspath(element) || !element.isStructureKnown()) {
      return;
    }

    // delete all markers
    try {
      resource.deleteMarkers(Constants.DS_ANNOTATION_PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
    } catch (CoreException e1) {
      e1.printStackTrace();
    }

    //
    parse((ICompilationUnit) element, resource);
  }

  /**
   * <p>
   * </p>
   * 
   * @param icompilationUnit
   * @throws CoreException
   */
  private void parse(ICompilationUnit icompilationUnit, IResource resource) throws CoreException {

    // create the AST
    CompilationUnit compilationUnit = createAst(icompilationUnit);

    // do not process files with compile errors
    if (hasErrors(compilationUnit)) {
      return;
    }

    // visit the AST
    DsAnnotationAstVisitor myAstVisitor = new DsAnnotationAstVisitor(resource.getProject());
    compilationUnit.accept(myAstVisitor);

    // Insane hack: we have to check whether types has been parsed or not
    // Under some circumstances the JDT AST is empty and getComponentDescriptions() returns an empty list
    // bug: https://github.com/wuetherich/ds-annotation-builder/issues/11
    if (myAstVisitor.getComponentDescriptions().isEmpty() && myAstVisitor.hasTypes()) {
      // delete any component description that eventually have been generated before for this resource
      ComponentDescriptionWriter.deleteGeneratedFiles(resource.getProject(), resource.getProjectRelativePath());
    }

    // iterate over the component descriptions
    for (IComponentDescription description : myAstVisitor.getComponentDescriptions()) {

      if (description.hasProblems()) {

        try {

          for (DsAnnotationProblem problem : description.getProblems()) {
            IMarker marker = resource.createMarker(Constants.DS_ANNOTATION_PROBLEM_MARKER);
            marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
            marker.setAttribute(IMarker.CHAR_START, problem.getCharStart());
            marker.setAttribute(IMarker.CHAR_END, problem.getCharEnd());
            marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
          }

        } catch (CoreException e) {

          // delete
          ComponentDescriptionWriter.deleteGeneratedFiles(resource.getProject(), resource.getFullPath());

          // TODO
          e.printStackTrace();
        }

      } else {

        //
        ComponentDescriptionWriter.writeComponentDescription(resource.getProject(), description);
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param result
   * @return
   */
  private boolean hasErrors(CompilationUnit result) {

    //
    for (IProblem problem : result.getProblems()) {
      if (problem.isError()) {
        return true;
      }
    }

    //
    return false;
  }

  /**
   * <p>
   * </p>
   * 
   * @param icompilationUnit
   * @return
   */
  private CompilationUnit createAst(ICompilationUnit icompilationUnit) {
    ASTParser parser = ASTParser.newParser(AST.JLS4); // handles JDK 1.0,
    // 1.1, 1.2, 1.3,
    // 1.4, 1.5, 1.6
    parser.setSource(icompilationUnit);

    // In order to parse 1.5 code, some compiler options need to be set to
    // 1.5
    Map options = JavaCore.getOptions();
    JavaCore.setComplianceOptions(JavaCore.VERSION_1_7, options);
    parser.setCompilerOptions(options);
    parser.setResolveBindings(true);
    CompilationUnit result = (CompilationUnit) parser.createAST(null);
    return result;
  }
}
