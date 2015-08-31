/*******************************************************************************
 * Copyright (c) 2015 Gerd Wütherich.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Gerd Wütherich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package org.eclipse.pde.ds.annotations.internal;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.pde.core.project.IBundleProjectDescription;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.internal.builder.DsAnnotationBuilder;

/**
 * <p>
 * The {@link DsAnnotationNature} indicates that an eclipse project is DS annotation aware. In this case the associated
 * {@link DsAnnotationBuilder} generates component descriptions for annotated component classes.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationNature implements IProjectNature {

  /** the associated bundle maker project */
  private IProject _project;

  /**
   * {@inheritDoc}
   */
  @Override
  public IProject getProject() {
    return _project;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setProject(IProject value) {
    _project = value;
  }

  /**
   * {@inheritDoc}
   */
  public void configure() throws CoreException {

    IProjectDescription desc = _project.getDescription();

    // check if the ds annotation builder already is configured
    ICommand[] commands = desc.getBuildSpec();
    for (int i = 0; i < commands.length; ++i) {
      if (commands[i].getBuilderName().equals(Constants.BUILDER_ID)) {
        return;
      }
    }

    // add builder to project
    ICommand command = desc.newCommand();
    command.setBuilderName(Constants.BUILDER_ID);
    ICommand[] newCommands = new ICommand[commands.length + 1];

    // Add it before other builders.
    System.arraycopy(commands, 0, newCommands, 1, commands.length);
    newCommands[0] = command;
    desc.setBuildSpec(newCommands);
    _project.setDescription(desc, null);

    // configure default import
    configureDefaultImport();
  }

  /**
   * {@inheritDoc}
   */
  public void deconfigure() throws CoreException {

    // get the description
    IProjectDescription desc = _project.getDescription();

    // remove the ds annotation builder command
    List<ICommand> iCommands = new LinkedList<ICommand>(Arrays.asList(desc.getBuildSpec()));
    for (Iterator<ICommand> iterator = iCommands.iterator(); iterator.hasNext();) {
      if (iterator.next().getBuilderName().equals(Constants.BUILDER_ID)) {
        iterator.remove();
        break;
      }
    }
    desc.setBuildSpec(iCommands.toArray(new ICommand[0]));

    // set the build specification
    _project.setDescription(desc, null);
  }

  /**
   * <p>
   * Adds the package '<code>org.osgi.service.component.annotations</code>' to the list of imported packages.
   * </p>
   * 
   * @throws CoreException
   */
  private void configureDefaultImport() throws CoreException {

    // get the bundle project description
    IBundleProjectDescription bundleProjectDescription = Activator.getBundleProjectDescription(_project);

    //
    if (bundleProjectDescription != null) {

      // check if already a IMPORT-PACKAGE header exists
      String importPackage = bundleProjectDescription.getHeader(org.osgi.framework.Constants.IMPORT_PACKAGE);

      //
      if (importPackage != null) {

        // check if the package 'org.osgi.service.component.annotations' already is imported
        for (String imp : importPackage.split(",")) {
          String[] strings = imp.split(";");
          if (strings[0].equals(Constants.DS_ANNOTATION_PACKAGE)) {
            return;
          }
        }

        //
        bundleProjectDescription.setHeader(org.osgi.framework.Constants.IMPORT_PACKAGE, importPackage + ", "
            + Constants.DS_ANNOTATION_PACKAGE);
        bundleProjectDescription.apply(null);
      }

      //
      else {

        //
        bundleProjectDescription
            .setHeader(org.osgi.framework.Constants.IMPORT_PACKAGE, Constants.DS_ANNOTATION_PACKAGE);

        //
        bundleProjectDescription.apply(null);
      }
    }
  }
}
