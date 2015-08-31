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
package org.eclipse.pde.ds.annotations.ui.internal.preferences.fwk;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * <p>
 * </p>
 * 
 * <p>
 * This source was copied (and than modified) from the internal class
 * {@link org.eclipse.jdt.internal.ui.preferences.PropertyAndPreferencePage}.
 * </p>
 */
public abstract class ConfigurationBlock extends Composite {

  /** - */
  private AbstractPropertyAndPreferencesPage _page;

  /**
   * <p>
   * </p>
   * 
   * @param parent
   * @param style
   */
  public ConfigurationBlock(Composite parent, int style, AbstractPropertyAndPreferencesPage page) {
    super(parent, style);

    //
    Assert.isNotNull(page);

    //
    _page = page;

    createContent();
  }

  /**
   * <p>
   * </p>
   * 
   * @param configurationBlock
   */
  protected abstract void createContent();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public AbstractPropertyAndPreferencesPage getPage() {
    return _page;
  }

  /**
   * <p>
   * </p>
   * 
   */
  public abstract void performDefaults();

  protected abstract String[] getPreferenceKeys();

  /**
   * <p>
   * </p>
   * 
   */
  public abstract void initialize();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public abstract boolean performOk();

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean performApply() {
    return performOk();
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public boolean performCancel() {
    return true;
  }

  /**
   * <p>
   * </p>
   */
  public void performHelp() {
    //
  }

  /**
   * <p>
   * </p>
   * 
   * @param project
   * @return
   */
  public boolean hasProjectSpecificOptions(IProject project) {

    //
    IEclipsePreferences eclipsePreferences = new ProjectScope(project).getNode(getPage().getStoreIdentifier());

    for (String string : getPreferenceKeys()) {
      if (eclipsePreferences.get(string, null) != null) {
        return true;
      }
    }

    //
    return false;
  }

  /**
   * Creates a composite that contains buttons for selecting the preference opening new project selections.
   */
  protected RadioGroupDialogField createRadioGroup(Composite parent, String label, String[] names, Object[] values,
      int layoutType) {

    //
    return new RadioGroupDialogField(parent, label, names, values, layoutType);
  }

  protected Text createLabelTextField(Composite parent, String label, int textFieldWidrg) {

    //
    Assert.isNotNull(parent);
    Assert.isNotNull(label);

    //
    Composite composite = new Composite(parent, SWT.NULL);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    composite.setLayout(layout);
    GridData data = new GridData();
    data.verticalAlignment = GridData.FILL;
    data.horizontalAlignment = GridData.FILL;
    composite.setLayoutData(data);

    //
    Label pathLabel = new Label(composite, SWT.NONE);
    pathLabel.setText(label);

    Text result =
        new Text(composite, SWT.SINGLE | SWT.BORDER);
    GridData gd = new GridData();
    gd.widthHint =
        getPage().convertWidthInCharsToPixels(textFieldWidrg);
    result.setLayoutData(gd);

    //
    return result;
  }
}
