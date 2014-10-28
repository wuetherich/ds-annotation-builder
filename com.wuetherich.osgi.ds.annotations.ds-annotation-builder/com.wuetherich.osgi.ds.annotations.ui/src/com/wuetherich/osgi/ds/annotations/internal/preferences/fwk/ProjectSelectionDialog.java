/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal.preferences.fwk;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionStatusDialog;

import com.wuetherich.osgi.ds.annotations.DsAnnotationsCore;

/**
 * <p>
 * </p>
 * 
 * <p>
 * This source was copied (and than modified) from the internal class
 * {@link org.eclipse.pde.internal.ui.preferences.ProjectSelectionDialog}.
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ProjectSelectionDialog extends SelectionStatusDialog {

  // the visual selection widget group
  private TableViewer                                     _tableViewer;

  private Set<IProject> _projectsWithSpecifics;

  // sizing constants
  private final static int                                SIZING_SELECTION_WIDGET_HEIGHT = 250;

  private final static int                                SIZING_SELECTION_WIDGET_WIDTH  = 300;

  private ViewerFilter                                    fFilter;

  public ProjectSelectionDialog(Shell parentShell, Set<IProject> projectsWithSpecifics) {
    super(parentShell);
    setTitle("Project Specific Configuration");
    setMessage("Select the project to configure:");
    _projectsWithSpecifics = projectsWithSpecifics;

    fFilter = new ViewerFilter() {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        return _projectsWithSpecifics.contains(element);
      }
    };
  }

  /*
   * (non-Javadoc) Method declared on Dialog.
   */
  @Override
  protected Control createDialogArea(Composite parent) {
    // page group
    Composite composite = (Composite) super.createDialogArea(parent);

    Font font = parent.getFont();
    composite.setFont(font);

    createMessageArea(composite);

    _tableViewer = new TableViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    _tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
        doSelectionChanged(((IStructuredSelection) event.getSelection()).toArray());
      }
    });
    _tableViewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent event) {
        okPressed();
      }
    });
    GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
    data.heightHint = SIZING_SELECTION_WIDGET_HEIGHT;
    data.widthHint = SIZING_SELECTION_WIDGET_WIDTH;
    _tableViewer.getTable().setLayoutData(data);

    _tableViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public Image getImage(Object element) {
        return PlatformUI.getWorkbench().getSharedImages().getImage(org.eclipse.ui.ide.IDE.SharedImages.IMG_OBJ_PROJECT);
      }

      @Override
      public String getText(Object element) {
        return ((IProject) element).getName();
      }
    });
    _tableViewer.setContentProvider(ArrayContentProvider.getInstance());
    _tableViewer.setComparator(new ViewerComparator());
    _tableViewer.getControl().setFont(font);

    _tableViewer.setInput(DsAnnotationsCore.getDsAnnotationAwareProjects());

    doSelectionChanged(new Object[0]);
    Dialog.applyDialogFont(composite);
    return composite;
  }

  private void doSelectionChanged(Object[] objects) {
    if (objects.length != 1) {
      updateStatus(new StatusInfo(IStatus.ERROR, "")); //$NON-NLS-1$
      setSelectionResult(null);
    } else {
      updateStatus(new StatusInfo());
      setSelectionResult(objects);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.ui.dialogs.SelectionStatusDialog#computeResult()
   */
  @Override
  protected void computeResult() {
  }
}
