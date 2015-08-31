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
package org.eclipse.pde.ds.annotations.ui.internal.preferences;

import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.ui.internal.handler.BuildSupport;
import org.eclipse.pde.ds.annotations.ui.internal.preferences.fwk.AbstractPropertyAndPreferencesPage;
import org.eclipse.pde.ds.annotations.ui.internal.preferences.fwk.ConfigurationBlock;
import org.eclipse.swt.widgets.Composite;

public class DsAnnotationsPropertyAndPreferencePage extends AbstractPropertyAndPreferencesPage {

  @Override
  protected String getPreferencePageID() {
    return "com.wuetherich.osgi.ds.annotations.ui.preferences";
  }

  @Override
  protected String getPropertyPageID() {
    return "com.wuetherich.osgi.ds.annotations.ui.properties";
  }

  @Override
  protected ConfigurationBlock createPreferenceContent(Composite composite) {
    return new DsAnnotationsPropertyAndPreferenceConfigurationBlock(composite, this);
  }

  @Override
  public String getStoreIdentifier() {
    return Constants.BUNDLE_ID;
  }

  @Override
  public boolean performOk() {

    //
    if (super.performOk()) {
      
      if (hasProjectSpecificOptions(getProject())) {
        BuildSupport.rebuildProject(getProject());
      } else {
        BuildSupport.rebuildProjects();
      }
      
      return true;
    }

    //
    return false;
  }

  @Override
  protected void performApply() {
    super.performApply();

    if (hasProjectSpecificOptions(getProject())) {
      BuildSupport.rebuildProject(getProject());
    } else {
      BuildSupport.rebuildProjects();
    }
  }
}
