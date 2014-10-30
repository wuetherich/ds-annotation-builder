package com.wuetherich.osgi.ds.annotations.internal.prefs;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.wuetherich.osgi.ds.annotations.Constants;
import com.wuetherich.osgi.ds.annotations.DsAnnotationVersion;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationsPreferenceInitializer extends AbstractPreferenceInitializer {

  /**
   * {@inheritDoc}
   */
  @Override
  public void initializeDefaultPreferences() {

    // get the preferences
    IEclipsePreferences preferences = DefaultScope.INSTANCE.getNode(Constants.BUNDLE_ID);

    // default setting
    preferences.put(Constants.PREF_DS_VERSION, DsAnnotationVersion.V_1_2.name());
    preferences.putBoolean(Constants.PREF_MARK_COMPONENT_DESCRIPTOR_AS_DERIVED, true);

  }
}