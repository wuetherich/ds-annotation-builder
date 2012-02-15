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
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
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

    if (compilationUnit != null && compilationUnit.isStructureKnown()) {
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

    // create the AST
    CompilationUnit result = createAst(icompilationUnit);

    // visit the AST
    DsAnnotationAstVisitor myAstVisitor = new DsAnnotationAstVisitor();
    result.accept(myAstVisitor);

    // create the output folder if necessary
    IFolder folder = resource.getProject().getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    if (!folder.exists()) {
      folder.create(true, true, null);
    }

    // iterate over the component descriptions
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

          // delete
          GeneratedComponentDescriptionsStore.deleteGeneratedFiles(resource.getProject(), resource.getFullPath());

          // TODO
          e.printStackTrace();
        }

      } else {

        // get the output file
        IFile file = folder.getFile(description.getName() + ".xml");

        // check if the component description has changed
        try {
          if (file.exists() && description.equals(file.getContents(true))) {
            System.out.println(String.format("No changes for file '%s'.", file.getFullPath()));
            continue;
          }
        } catch (JAXBException e) {
          // simply ignore exceptions
        }

        // delete the existing file
        if (file.exists()) {
          file.delete(true, null);
        }

        // write the new component description to disc
        file.create(new StringBufferInputStream(description.toXml()), true, null);

        // add generated file to the GeneratedComponentDescriptionsStore
        GeneratedComponentDescriptionsStore.addGeneratedFile(resource.getProject(), file.getFullPath(),
            resource.getFullPath());
      }
    }

    // finally we have to refresh the local folder
    folder.refreshLocal(IResource.DEPTH_INFINITE, null);
  }

  /**
   * <p>
   * </p>
   *
   * @param icompilationUnit
   * @return
   */
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
