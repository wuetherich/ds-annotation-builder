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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.pde.core.project.IBundleProjectDescription;

import com.wuetherich.osgi.ds.annotations.internal.Activator;
import com.wuetherich.osgi.ds.annotations.internal.Constants;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class GeneratedComponentDescriptionsStore {

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   * @throws CoreException
   */
  public static final void addGeneratedFile(IProject project, IPath componentDescription, IPath resourcePath)
      throws CoreException {

    try {

      //
      Properties properties = loadProperties(project);

      //
      properties.setProperty(componentDescription.toOSString(), resourcePath.toOSString());

      //
      saveProperties(project, properties);

    } catch (Exception e) {
      throw new CoreException(new Status(IStatus.ERROR, Constants.BUNDLE_ID, e.getMessage(), e));
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param componentDescription
   * @throws CoreException
   */
  public static final void removeGeneratedFile(IProject project, IPath componentDescription) throws CoreException {

    try {

      //
      Properties properties = loadProperties(project);

      //
      properties.remove(componentDescription.toOSString());

      //
      saveProperties(project, properties);

    } catch (Exception e) {
      throw new CoreException(new Status(IStatus.ERROR, Constants.BUNDLE_ID, e.getMessage(), e));
    }
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

    List<IPath> result = getGeneratedFiles(project, resource);

    for (IPath path : result) {
      try {
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        file.delete(true, null);
        removeGeneratedFile(project, path);
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
    return result;
  }

  /**
   * @param project
   * @return
   * @throws CoreException
   */
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
   * @param project
   * @param properties
   * @throws CoreException
   */
  private static void saveProperties(IProject project, Properties properties) throws CoreException {

    try {
      File file = getFile(project);
      properties.store(new FileOutputStream(file), null);

      // TODO Move
      // *** Start: Set BUNDLE_HEADER_SERVICE_COMPONENT ***//
      IBundleProjectDescription bundleProjectDescription = Activator.getBundleProjectDescription(project);
      if (bundleProjectDescription != null) {

        //
        List<IPath> genFiles = getGeneratedFiles(project);

        if (genFiles.size() > 0) {
          StringBuilder stringBuilder = new StringBuilder();
          for (Iterator<IPath> iterator = genFiles.iterator(); iterator.hasNext();) {
            IPath iPath = (IPath) iterator.next();
            stringBuilder.append(iPath.makeRelativeTo(project.getFullPath()).toPortableString());
            if (iterator.hasNext()) {
              stringBuilder.append(",\n ");
            }
          }
          //
          bundleProjectDescription.setHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT, stringBuilder.toString());
        } else {
          bundleProjectDescription.setHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT, null);
        }

        //
        IPath componentDescriptionFolder = new Path(Constants.COMPONENT_DESCRIPTION_FOLDER + "/");

        //
        IPath[] binIncludePaths = bundleProjectDescription.getBinIncludes();
        if (!contains(binIncludePaths, componentDescriptionFolder) && genFiles.size() > 0) {
          if (binIncludePaths != null) {
            IPath[] newPaths = new IPath[binIncludePaths.length + 1];
            System.arraycopy(binIncludePaths, 0, newPaths, 0, binIncludePaths.length);
            newPaths[binIncludePaths.length] = componentDescriptionFolder;
            binIncludePaths = newPaths;
          } else {
            binIncludePaths = new IPath[] { componentDescriptionFolder };
          }
        }

        //
        else if (contains(binIncludePaths, componentDescriptionFolder) && genFiles.size() == 0) {
          List<IPath> newPathList = new LinkedList<IPath>();
          for (IPath binIncludePath : binIncludePaths) {
            if (!binIncludePath.equals(componentDescriptionFolder)) {
              newPathList.add(binIncludePath);
            }
          }
          binIncludePaths = newPathList.toArray(new IPath[0]);
        }
        bundleProjectDescription.setBinIncludes(binIncludePaths);

        bundleProjectDescription.apply(null);
        // *** Stop: Set BUNDLE_HEADER_SERVICE_COMPONENT ***//
      }

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
   * @param paths
   * @param path
   * @return
   */
  private static boolean contains(IPath[] paths, IPath path) {

    //
    if (paths == null) {
      return false;
    }

    //
    for (IPath iPath : paths) {
      if (iPath.equals(path)) {
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
   * @param project
   * @return
   * @throws IOException
   */
  private static File getFile(IProject project) throws IOException {

    IPath path = project.getWorkingLocation(Constants.BUNDLE_ID);
    IPath iPath = path.append("generatedFiles");
    File file = iPath.toFile();
    if (!file.exists()) {
      file.createNewFile();
    }

    return file;
  }
}
