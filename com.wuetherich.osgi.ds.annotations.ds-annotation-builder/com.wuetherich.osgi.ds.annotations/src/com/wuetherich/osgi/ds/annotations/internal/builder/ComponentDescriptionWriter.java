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

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.util.GenericCache;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ComponentDescriptionWriter {

  /** - */
  private static Pattern REGEXP_PATTERN = Pattern.compile(Constants.DS_ANNOTATION_BUILDER_GENERATED_REGEXP); ;

  // /**
  // * <p>
  // * </p>
  // *
  // * @param project
  // * @param path
  // */
  // public static void updateSourceFile(IProject project, IPath path) {
  //
  // //
  // IPath sourceFile = findSourceFileForComponentDescriptionPath(project, path);
  //
  // if (sourceFile != null) {
  // try {
  // project.getFile(sourceFile).refreshLocal(IResource.DEPTH_ZERO, null);
  // } catch (CoreException e) {
  // //
  // }
  // }
  // }

  /**
   * <p>
   * </p>
   * 
   * @param project
   */
  public static void removeDanglingComponentDescriptions(IProject project) {

    //
    for (Map.Entry<IPath, List<IPath>> entry : loadProperties(project).entrySet()) {

      try {

        //
        IFile originFile = project.getFile(entry.getKey());

        //
        if (!originFile.exists()) {
          for (IPath path : entry.getValue()) {
            try {
              project.getFile(path).delete(true, null);
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //
    try {
      ManifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(project, loadProperties(project));
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param description
   * @throws CoreException
   */
  public static void writeComponentDescription(IProject project, ComponentDescription description) throws CoreException {

    // get the output file
    IFile file = project.getFile(new Path(Constants.COMPONENT_DESCRIPTION_FOLDER).append(new Path(description.getName()
        + ".xml")));

    // check if the component description has changed
    try {
      if (file.exists() && description.equals(file.getContents(true)) && containsDsAnnotationBuilderComment(file)) {
        return;
      }
    } catch (JAXBException e) {
      // simply ignore exceptions
    }

    // create the output folder if necessary
    IFolder folder = project.getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    if (!folder.exists()) {
      folder.create(true, true, null);
    }

    //
    if (file.exists()) {
      if (!file.isDerived()) {
        file.setDerived(true, null);
      }
      file.setContents(new StringBufferInputStream(description.toXml()), IFile.FORCE, null);
    } else {
      // write the new component description to disc
      file.create(new StringBufferInputStream(description.toXml()), true, null);
      file.setDerived(true, null);
    }

    // finally we have to refresh the local folder
    folder.refreshLocal(IResource.DEPTH_INFINITE, null);

    //
    ManifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(project, loadProperties(project));
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param resource
   * @throws CoreException
   */
  public static void deleteGeneratedFiles(IProject project, IPath resource) throws CoreException {

    //
    Map<IPath, List<IPath>> generatedFiles = loadProperties(project);

    //
    List<IPath> result = generatedFiles.get(resource);

    //
    if (result != null) {

      for (IPath path : result) {
        try {
          IFile file = project.getFile(path);
          file.delete(true, null);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

    //
    ManifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(project, loadProperties(project));
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   * @throws CoreException
   */
  private static Map<IPath, List<IPath>> loadProperties(IProject project) {

    //
    Assert.isNotNull(project);

    //
    IFolder folder = project.getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    try {
      if (folder.exists()) {
        folder.refreshLocal(IResource.DEPTH_INFINITE, null);
      }
    } catch (CoreException e) {
      // noop
    }

    //
    GenericCache<IPath, List<IPath>> genericCache = new GenericCache<IPath, List<IPath>>() {
      @Override
      public List<IPath> create(IPath key) {
        return new LinkedList<IPath>();
      }
    };

    try {
      //
      if (folder.exists()) {
        for (IResource iResource : folder.members()) {

          if (iResource instanceof IFile && iResource.getName().endsWith(".xml")) {
            String source = extractSource((IFile) iResource);
            if (source != null) {
              genericCache.getOrCreate(new Path(source)).add(iResource.getProjectRelativePath());
            }
          }
        }
      }
    } catch (CoreException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    //
    return genericCache;
  }

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   * @throws Exception
   */
  private static boolean containsDsAnnotationBuilderComment(IFile file) {
    return extractSource(file) != null;
  }

  /**
   * <p>
   * </p>
   * 
   * @param file
   * @return
   * @throws Exception
   */
  private static String extractSource(IFile file) {

    //
    String result = null;

    try {
      // get input stream
      InputStream inputStream = file.getContents();
      XMLStreamReader xmlStreamReader = null;

      try {

        //
        xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);

        //
        while (xmlStreamReader.hasNext() && result == null) {
          if (xmlStreamReader.next() == XMLStreamConstants.COMMENT) {
            String comment = xmlStreamReader.getText();
            Matcher matcher = REGEXP_PATTERN.matcher(comment);
            if (matcher.matches()) {
              result = matcher.group(2);
            }
          }
        }
      } finally {
        try {
          inputStream.close();
          xmlStreamReader.close();
        } catch (Exception e) {
          //
        }
      }
    } catch (Throwable e) {
      //
    }

    //
    return result;
  }

  private static IPath findSourceFileForComponentDescriptionPath(IProject project, IPath path) {
    for (Map.Entry<IPath, List<IPath>> entry : loadProperties(project).entrySet()) {
      for (IPath iPath : entry.getValue()) {
        if (iPath.equals(path)) {
          return entry.getKey();
        }
      }
    }
    return null;
  }
}
