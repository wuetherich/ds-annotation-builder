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
package org.eclipse.pde.ds.annotations.ui.internal.handler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class RegenerateComponentDesciptionsHandler extends AbstractHandler
		implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if (isEnabled()) {

			ListSelectionDialog dlg = new ListSelectionDialog(Display
					.getCurrent().getActiveShell(), ResourcesPlugin
					.getWorkspace().getRoot(), getContentProvider(),
					new WorkbenchLabelProvider(), "Select the Project:");

			dlg.setTitle("Project Selection");

			if (dlg.open() == Window.OK) {

				for (Object object : dlg.getResult()) {

					IProject project = null;

					if (object instanceof IResource) {
						IResource resource = (IResource) object;
						project = resource.getProject();
					} else if (object instanceof IAdaptable) {
						IResource resource = (IResource) ((IAdaptable) object)
								.getAdapter(IResource.class);
						if (resource != null) {
							project = resource.getProject();
						}
					}

					if (project != null) {
						BuildSupport.rebuildProject(project);
					}
				}
			}
		}

		//
		return null;
	}

	protected IStructuredContentProvider getContentProvider() {

		return new WorkbenchContentProvider() {
			public Object[] getChildren(Object o) {

				if (o instanceof IWorkspaceRoot) {

					//
					IWorkspaceRoot workspaceRoot = (IWorkspaceRoot) o;

					// Collect all the projects in the workspace except the
					// given project
					IProject[] projects = workspaceRoot.getProjects();
					List<IProject> result = new ArrayList<IProject>(
							projects.length);
					for (IProject project : projects) {
						try {
							if (project.hasNature(Constants.NATURE_ID)) {
								result.add(project);
							}
						} catch (CoreException e) {
							//
						}
					}

					//
					return result.toArray();

				} else {
					return new Object[0];
				}

			}
		};
	}
}
