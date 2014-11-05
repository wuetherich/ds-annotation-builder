package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.swt.widgets.Composite;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.internal.handler.BuildSupport;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.AbstractPropertyAndPreferencesPage;
import com.wuetherich.osgi.ds.annotations.internal.preferences.fwk.ConfigurationBlock;

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
