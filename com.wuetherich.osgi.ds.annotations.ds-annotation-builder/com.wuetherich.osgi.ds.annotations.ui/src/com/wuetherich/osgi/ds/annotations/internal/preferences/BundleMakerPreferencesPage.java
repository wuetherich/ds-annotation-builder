package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.swt.widgets.Composite;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class BundleMakerPreferencesPage extends AbstractPropertyAndPreferencesPage {

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getPreferencePageID() {
    return "org.bundlemaker.core.ui.preferences.bundlemakerpage";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getPropertyPageID() {
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStoreIdentifier() {
    return Activator.PLUGIN_ID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ConfigurationBlock createPreferenceContent(Composite composite) {
    return new BundleMakerPreferencesConfigurationBlock(composite, this);
  }
}
