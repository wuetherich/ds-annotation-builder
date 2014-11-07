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
package com.wuetherich.osgi.ds.annotations.internal.componentdescription.impl;

import java.io.StringBufferInputStream;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescription;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescriptionReader;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IComponentDescriptionWriter;
import com.wuetherich.osgi.ds.annotations.internal.componentdescription.IManifestAndBuildPropertiesUpdater;
import com.wuetherich.osgi.ds.annotations.internal.prefs.DsAnnotationsPreferences;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ComponentDescriptionWriter implements IComponentDescriptionWriter {

  /** - */
  private IComponentDescriptionReader        _componentDescriptionReader;

  /** - */
  private IManifestAndBuildPropertiesUpdater _manifestAndBuildPropertiesUpdater;

  /**
   * <p>
   * Creates a new instance of type {@link ComponentDescriptionWriter}.
   * </p>
   * 
   * @param componentDescriptionReader
   */
  public ComponentDescriptionWriter(IComponentDescriptionReader componentDescriptionReader,
      IManifestAndBuildPropertiesUpdater manifestAndBuildPropertiesUpdater) {
    Assert.isNotNull(componentDescriptionReader);
    Assert.isNotNull(manifestAndBuildPropertiesUpdater);

    this._componentDescriptionReader = componentDescriptionReader;
    this._manifestAndBuildPropertiesUpdater = manifestAndBuildPropertiesUpdater;
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   */
  public void removeDanglingComponentDescriptions(IProject project) {

    //
    for (Map.Entry<IPath, List<IPath>> entry : _componentDescriptionReader.loadGeneratedDescriptionsMap(project)
        .entrySet()) {

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
      _manifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(project);
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
  public void writeComponentDescription(IProject project, IComponentDescription description) throws CoreException {

    // get the output file
    IFile file = project.getFile(new Path(Constants.COMPONENT_DESCRIPTION_FOLDER).append(new Path(description.getName()
        + ".xml")));

    // check if the component description has changed
    try {
      if (file.exists() && description.equals(file.getContents(true))
          && _componentDescriptionReader.containsDsAnnotationBuilderComment(file)) {
        return;
      }
    } catch (Exception e) {
      // simply ignore exceptions
    }

    // create the output folder if necessary
    IFolder folder = project.getFolder(Constants.COMPONENT_DESCRIPTION_FOLDER);
    if (!folder.exists()) {
      folder.create(true, true, null);
    }

    //
    if (file.exists()) {
      if (markGeneratedComponentDescriptionsAsDerived(project)) {
        if (!file.isDerived()) {
          file.setDerived(true, null);
        }
      }
      file.setContents(new StringBufferInputStream(description.toXml()), IFile.FORCE, null);
    } else {

      // delete old files
      deleteGeneratedFiles(project, new Path(description.getAssociatedSourceFile()));

      // write the new component description to disc
      file.create(new StringBufferInputStream(description.toXml()), true, null);
      if (markGeneratedComponentDescriptionsAsDerived(project)) {
        file.setDerived(true, null);
      }
    }

    // finally we have to refresh the local folder
    folder.refreshLocal(IResource.DEPTH_INFINITE, null);

    //
    _manifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(project);
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @param resource
   * @throws CoreException
   */
  public void deleteGeneratedFiles(IProject project, IPath resource) throws CoreException {

    //
    Map<IPath, List<IPath>> generatedFiles = _componentDescriptionReader.loadGeneratedDescriptionsMap(project);

    //
    List<IPath> result = generatedFiles.get(resource);

    //
    if (result != null) {

      for (IPath path : result) {

        final IFile file = project.getFile(path);

        Job job = new Job(String.format("Deleting resource '%s'...", file.getName())) {
          @Override
          protected IStatus run(IProgressMonitor monitor) {
            long start = System.currentTimeMillis();
            while (file.exists() && (System.currentTimeMillis() - start) < 7500) {
              try {
                file.delete(true, null);
              } catch (Exception e) {
              }
            }
            // TODO
            // if (file.exists()) {
            // return Status.
            // }
            return Status.OK_STATUS;
          }

        };
        job.setUser(true);
        job.schedule();

        long start = System.currentTimeMillis();

        while (file.exists() && (System.currentTimeMillis() - start) < 3500) {
          try {
            file.delete(true, null);
          } catch (Exception e) {
            // e.printStackTrace();
          }
        }
      }
    }

    //
    _manifestAndBuildPropertiesUpdater.updateManifestAndBuildProperties(project);
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  private boolean markGeneratedComponentDescriptionsAsDerived(IProject project) {
    return DsAnnotationsPreferences.getBoolean(project, Constants.PREF_MARK_COMPONENT_DESCRIPTOR_AS_DERIVED, true);
  }
}
