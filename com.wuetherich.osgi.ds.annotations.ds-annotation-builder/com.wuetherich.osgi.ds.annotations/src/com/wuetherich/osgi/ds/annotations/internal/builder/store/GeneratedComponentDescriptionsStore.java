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
package com.wuetherich.osgi.ds.annotations.internal.builder.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;

import com.wuetherich.osgi.ds.annotations.Constants;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class GeneratedComponentDescriptionsStore {

  /** - */
  private static UpdateManifestAndBuildPropertiesSaveParticipant _saveParticipant = new UpdateManifestAndBuildPropertiesSaveParticipant();

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param resource
   * @throws CoreException
   */
  public static void deleteGeneratedFiles(IProject project, IPath resource) throws CoreException {

    List<IPath> result = getGeneratedFiles(project, resource);

    for (IPath path : result) {
      try {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        file.delete(true, null);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   * @throws CoreException
   */
  public static List<IPath> getGeneratedFiles(IProject project, IPath resource) throws CoreException {

    //
    List<IPath> result = new LinkedList<IPath>();

    //
    Properties properties = loadProperties(project);

    //
    for (Entry<Object, Object> entry : properties.entrySet()) {

      //
      if (resource.toOSString().equals(entry.getValue())) {

        //
        result.add(new Path((String) entry.getKey()));

      }
    }

    //
    return result;
  }

  public static List<IPath> getOriginFiles(IProject project) throws CoreException {

    //
    List<IPath> result = new LinkedList<IPath>();

    //
    Properties properties = loadProperties(project);

    //
    for (Entry<Object, Object> entry : properties.entrySet()) {

      //
      IPath iPath = new Path((String) entry.getKey());

      //
      if (!result.contains(iPath)) {
        result.add(iPath);
      }
    }

    //
    return result;
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   * @throws CoreException
   */
  public static List<IPath> getGeneratedFiles(IProject project) throws CoreException {

    //
    List<IPath> result = new LinkedList<IPath>();

    //
    Properties properties = loadProperties(project);

    //
    for (Entry<Object, Object> entry : properties.entrySet()) {

      //
      result.add(new Path((String) entry.getKey()));
    }

    //
    Collections.sort(result, new Comparator<IPath>() {
      @Override
      public int compare(IPath o1, IPath o2) {
        return o1.toString().compareTo(o2.toString());
      }
    });

    //
    return result;
  }

  /**
   * @param project
   * @return
   * @throws CoreException
   */
  // TODO: REPLACE
  private static Properties loadProperties(IProject project) throws CoreException {
    try {
      File file = getFile(project);
      Properties properties = new Properties();
      properties.load(new FileInputStream(file));
      return properties;
    } catch (FileNotFoundException e) {
      throw new CoreException(new Status(IStatus.ERROR, Constants.BUNDLE_ID, e.getMessage(), e));
    } catch (IOException e) {
      throw new CoreException(new Status(IStatus.ERROR, Constants.BUNDLE_ID, e.getMessage(), e));
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   * @throws IOException
   */
  // TODO: REPLACE
  private static File getFile(IProject project) throws IOException {

    IPath path = project.getWorkingLocation(Constants.BUNDLE_ID);
    IPath iPath = path.append("generatedFiles");
    File file = iPath.toFile();
    if (!file.exists()) {
      file.createNewFile();
    }

    return file;
  }
  
  public static void main(String[] args) throws FileNotFoundException, XMLStreamException, FactoryConfigurationError {
    
    //
    XMLStreamReader xr = XMLInputFactory.newInstance().createXMLStreamReader(
        new FileInputStream("C:\\workspaces\\junit-workspace\\DEFAULT-JDT-TEST-PROJECT\\OSGI-INF\\de.test.Test.xml"));
    
    
    Pattern p = Pattern.compile(Constants.DS_ANNOTATION_BUILDER_GENERATED_REGEXP);
    
    //
    while (xr.hasNext()) {
      if (xr.next() == XMLStreamConstants.COMMENT) {
        String comment = xr.getText();
        System.out.println(comment);
        Matcher m = p.matcher(comment);
        if (m.matches()) {
          System.out.println(m.group(2));
        }
      }
    }
  }
}
