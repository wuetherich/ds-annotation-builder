package com.wuetherich.osgi.ds.annotations.internal.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;

/**
 * @author Nils Hartmann (nils@nilshartmann.net)
 * 
 */
public class BundleMakerPreferenceInitializer extends AbstractPreferenceInitializer {

  /** - */
  public static final String PREF_SWITCH_TO_PERSPECTIVE_ON_PROJECT_OPEN = Activator.PLUGIN_ID
                                                                            + ".switch_to_perspective_on_open";

  /**
   * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
   */
  @Override
  public void initializeDefaultPreferences() {
    Activator.getDefault().getPreferenceStore()
        .setDefault(PREF_SWITCH_TO_PERSPECTIVE_ON_PROJECT_OPEN, MessageDialogWithToggle.PROMPT);
  }
}
