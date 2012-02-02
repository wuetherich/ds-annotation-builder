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

import java.io.StringBufferInputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import com.wuetherich.osgi.ds.annotations.internal.AbstractComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.Constants;
import com.wuetherich.osgi.ds.annotations.internal.DsAnnotationProblem;

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

      //
      GeneratedComponentDescriptionsStore.deleteGeneratedFiles(delta.getResource().getProject(), delta.getResource()
          .getFullPath());

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
    if (!resource.getName().endsWith(".java")) {
      return;
    }

    //
    IJavaElement element = JavaCore.create(resource);
    IJavaProject javaProject = JavaCore.create(resource.getProject());
    if (!javaProject.isOnClasspath(element) || !element.isStructureKnown()) {
      return;
    }

    if (JavaCore.create(resource) == null) {
      return;
    }

    try {
      resource.deleteMarkers(Constants.DS_ANNOTATION_PROBLEM_MARKER, true, IResource.DEPTH_ZERO);
    } catch (CoreException e1) {
      e1.printStackTrace();
    }

    //
    IJavaElement compilationUnit = JavaCore.create(resource);

    if (compilationUnit != null) {
      parse((ICompilationUnit) compilationUnit, resource);
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param icompilationUnit
   * @throws CoreException
   */
  private void parse(ICompilationUnit icompilationUnit, IResource resource) throws CoreException {

    //
    CompilationUnit result = createAst(icompilationUnit);

    DsAnnotationAstVisitor myAstVisitor = new DsAnnotationAstVisitor();
    result.accept(myAstVisitor);

    IProject iProject = resource.getProject();
    IFolder folder = iProject.getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    if (!folder.exists()) {
      folder.create(true, true, null);
    }

    // delete
    GeneratedComponentDescriptionsStore.deleteGeneratedFiles(iProject, resource.getFullPath());

    for (AbstractComponentDescription description : myAstVisitor.getComponentDescriptions()) {

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
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      } else {
        //
        IFile file = folder.getFile(description.getName() + ".xml");

        //
        if (file.exists()) {
          file.delete(true, null);
        }

        //
        file.create(new StringBufferInputStream(description.toXml()), true, null);

        GeneratedComponentDescriptionsStore.addGeneratedFile(iProject, file.getFullPath(), resource.getFullPath());
      }
    }

    folder.refreshLocal(IResource.DEPTH_INFINITE, null);
  }

  private CompilationUnit createAst(ICompilationUnit icompilationUnit) {
    ASTParser parser = ASTParser.newParser(AST.JLS3); // handles JDK 1.0,
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
