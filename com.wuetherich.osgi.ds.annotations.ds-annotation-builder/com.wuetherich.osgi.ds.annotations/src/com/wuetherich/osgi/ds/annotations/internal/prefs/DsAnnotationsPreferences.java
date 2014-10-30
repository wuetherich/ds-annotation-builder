package com.wuetherich.osgi.ds.annotations.internal.prefs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import com.wuetherich.osgi.ds.annotations.Constants;

public class DsAnnotationsPreferences {

  /**
   * <p>
   * </p>
   *
   * @param project
   * @param key
   * @param defaultValue
   * @return
   */
  public static String get(IProject project, String key, String defaultValue) {
    return Platform.getPreferencesService().getString(Constants.BUNDLE_ID, key, defaultValue, getContexts(project));
  }

  public static boolean getBoolean(IProject project, String key, boolean defaultValue) {
    return Platform.getPreferencesService().getBoolean(Constants.BUNDLE_ID, key, defaultValue, getContexts(project));
  }

  /**
   * <p>
   * </p>
   *
   * @param project
   * @return
   */
  private static IScopeContext[] getContexts(IProject project) {
    IScopeContext projectCtx = new ProjectScope(project);
    IScopeContext instanceCtx = InstanceScope.INSTANCE;
    IScopeContext defaultCtx = DefaultScope.INSTANCE;
    return new IScopeContext[] { projectCtx, instanceCtx, defaultCtx };
  }
}
