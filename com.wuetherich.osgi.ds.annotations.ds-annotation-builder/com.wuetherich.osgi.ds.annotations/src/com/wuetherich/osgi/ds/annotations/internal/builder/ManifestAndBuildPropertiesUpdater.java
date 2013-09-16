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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.pde.core.project.IBundleProjectDescription;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.Activator;
import com.wuetherich.osgi.ds.annotations.internal.util.PathUtils;

/**
 * <p>
 * The {@link ManifestAndBuildPropertiesUpdater} is responsible for updating the projects manifest and build properties.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ManifestAndBuildPropertiesUpdater {

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param generatedComponentDescriptions
   * @throws CoreException
   */
  public static void updateManifestAndBuildProperties(IProject project, Map<IPath, List<IPath>> map)
      throws CoreException {

    //
    boolean projectDescriptionChanged = false;

    //
    IBundleProjectDescription bundleProjectDescription = Activator.getBundleProjectDescription(project);

    //
    if (bundleProjectDescription != null) {

      //
      if (map.size() > 0) {

        //
        List<String> descriptions = new LinkedList<String>();
        for (List<IPath> pathes : map.values()) {
          for (IPath iPath : pathes) {
            descriptions.add(iPath.toPortableString());
          }
        }

        //
        StringBuilder stringBuilder = new StringBuilder();
        for (Iterator<String> iterator = descriptions.iterator(); iterator.hasNext();) {
          stringBuilder.append(iterator.next());
          if (iterator.hasNext()) {
            stringBuilder.append(",\n ");
          }
        }

        //
        String newHeader = stringBuilder.toString();
        newHeader = newHeader.replace(",\n ", ",");
        projectDescriptionChanged = !newHeader.equals(bundleProjectDescription
            .getHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT));

        //
        bundleProjectDescription.setHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT, stringBuilder.toString());

      } else {

        //
        projectDescriptionChanged = bundleProjectDescription.getHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT) == null;
        bundleProjectDescription.setHeader(Constants.BUNDLE_HEADER_SERVICE_COMPONENT, null);
      }

      //
      projectDescriptionChanged = projectDescriptionChanged
          || addComponentDescriptionFolderToBinIncludes(map, bundleProjectDescription);

      if (projectDescriptionChanged) {

        // Bug-Fix: https://github.com/wuetherich/ds-annotation-builder/issues/6
        // 'Empty Manifest entries got removed'
        for (String emtptyHeader : getManifestEmptyHeader(project)) {
          bundleProjectDescription.setHeader(emtptyHeader, "");
        }

        // store description
        bundleProjectDescription.apply(null);
      }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @param generatedComponentDescriptions
   * @param shouldApply
   * @param bundleProjectDescription
   * @return
   */
  private static boolean addComponentDescriptionFolderToBinIncludes(Map<IPath, List<IPath>> map,
      IBundleProjectDescription bundleProjectDescription) {

    //
    IPath componentDescriptionFolder = new Path(Constants.COMPONENT_DESCRIPTION_FOLDER + "/");

    //
    IPath[] binIncludePaths = bundleProjectDescription.getBinIncludes();
    if (!PathUtils.contains(binIncludePaths, componentDescriptionFolder) && map.size() > 0) {
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
    else if (PathUtils.contains(binIncludePaths, componentDescriptionFolder) && map.size() == 0) {
      List<IPath> newPathList = new LinkedList<IPath>();
      for (IPath binIncludePath : binIncludePaths) {
        if (!binIncludePath.equals(componentDescriptionFolder)) {
          newPathList.add(binIncludePath);
        }
      }
      binIncludePaths = newPathList.toArray(new IPath[0]);
    }

    //
    boolean result = !Arrays.deepEquals(bundleProjectDescription.getBinIncludes(), binIncludePaths);

    bundleProjectDescription.setBinIncludes(binIncludePaths);

    return result;
  }

  /**
   * <p>
   * This method is necessary to fix bug [https://github.com/wuetherich/ds-annotation-builder/issues/6]. It resource an
   * arrays of all empty manifest entries contained in the manifest of the given project. If the project does not
   * contain a manifest file (or the manifest could not be read), an empty array will be returned instead.
   * </p>
   * 
   * @param project the project
   * @return the array of empty manifest header (or null)
   */
  private static String[] getManifestEmptyHeader(IProject project) {

    //
    try {

      //
      IFile file = project.getFile("META-INF/MANIFEST.MF");
      Manifest manifest = new Manifest(file.getContents());

      //
      List<String> result = new LinkedList<String>();
      for (Map.Entry<Object, Object> entries : manifest.getMainAttributes().entrySet()) {
        if (entries.getValue() == null || entries.getValue().toString().isEmpty()) {
          result.add(entries.getKey().toString());
        }
      }

      //
      return result.toArray(new String[] {});
    }

    //
    catch (Throwable throwable) {
      return new String[] {};
    }
  }
}
