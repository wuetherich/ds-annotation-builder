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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.pde.ds.annotations.Constants;
import org.eclipse.pde.ds.annotations.DsAnnotationVersion;

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
