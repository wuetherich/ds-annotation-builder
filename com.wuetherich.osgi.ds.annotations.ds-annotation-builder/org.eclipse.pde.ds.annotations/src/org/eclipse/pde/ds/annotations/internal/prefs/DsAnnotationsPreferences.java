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
package org.eclipse.pde.ds.annotations.internal.prefs;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.DsAnnotationVersion;

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

  public static DsAnnotationVersion getDsAnnotationVersion(IProject project) {
    return DsAnnotationVersion.valueOf(DsAnnotationsPreferences.get(project, Constants.PREF_DS_VERSION,
        DsAnnotationVersion.V_1_2.name()));
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
