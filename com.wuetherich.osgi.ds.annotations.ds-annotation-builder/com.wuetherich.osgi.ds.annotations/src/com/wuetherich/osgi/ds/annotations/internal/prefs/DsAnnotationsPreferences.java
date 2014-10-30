package com.wuetherich.osgi.ds.annotations.internal.prefs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.Preferences;

import com.wuetherich.osgi.ds.annotations.Constants;

public class DsAnnotationsPreferences {

  public static String get(IProject project, String key, String defaultValue) {

    //
    Preferences projectPreferences = new ProjectScope(project).getNode(Constants.BUNDLE_ID);
    Preferences instancePreferences = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_ID);
    Preferences defaultPreferences = DefaultScope.INSTANCE.getNode(Constants.BUNDLE_ID);
    
    //
    return Platform.getPreferencesService().get(key, defaultValue,
        new Preferences[] { projectPreferences, instancePreferences, defaultPreferences });
  }
}
