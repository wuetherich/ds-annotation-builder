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
package org.eclipse.pde.ds.annotations.internal.componentdescription;

import org.eclipse.core.resources.IProject;
import org.eclipse.pde.ds.annotations.DsAnnotationVersion;
import org.eclipse.pde.ds.annotations.internal.componentdescription.impl.ComponentDescriptionReader;
import org.eclipse.pde.ds.annotations.internal.componentdescription.impl.ComponentDescriptionWriter;
import org.eclipse.pde.ds.annotations.internal.componentdescription.impl.ManifestAndBuildPropertiesUpdater;
import org.eclipse.pde.ds.annotations.internal.componentdescription.impl.SCR_1_0_ComponentDescription;
import org.eclipse.pde.ds.annotations.internal.componentdescription.impl.SCR_1_1_ComponentDescription;
import org.eclipse.pde.ds.annotations.internal.componentdescription.impl.SCR_1_2_ComponentDescription;

public class ComponentDescriptionFactory {

  /** - */
  private static ComponentDescriptionReader         _componentDescriptionReader;

  private static IComponentDescriptionWriter        _componentDescriptionWriter;

  private static IManifestAndBuildPropertiesUpdater _manifestAndBuildPropertiesUpdater;

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public static IComponentDescriptionWriter getComponentDescriptionWriter() {

    if (_componentDescriptionWriter == null) {
      _componentDescriptionWriter = new ComponentDescriptionWriter(componentDescriptionReader(),
          getManifestAndBuildPropertiesUpdater());
    }

    return _componentDescriptionWriter;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public static IManifestAndBuildPropertiesUpdater getManifestAndBuildPropertiesUpdater() {

    if (_manifestAndBuildPropertiesUpdater == null) {
      _manifestAndBuildPropertiesUpdater = new ManifestAndBuildPropertiesUpdater(componentDescriptionReader());
    }

    return _manifestAndBuildPropertiesUpdater;
  }

  /**
   * <p>
   * </p>
   * 
   * @param typeDeclaration
   * @return
   */
  public static IComponentDescription createComponentDescription(AbstractTypeAccessor typeDeclaration,
      IProject project, DsAnnotationVersion requestedversion) {

    //
    switch (requestedversion) {
    case V_1_0: {
      return new SCR_1_0_ComponentDescription(typeDeclaration);
    }
    case V_1_1: {
      return new SCR_1_1_ComponentDescription(typeDeclaration);
    }
    case V_1_2: {
      return new SCR_1_2_ComponentDescription(typeDeclaration);
    }
    default: {
      throw new RuntimeException("Unsupported version");
    }
    }
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  private static ComponentDescriptionReader componentDescriptionReader() {

    if (_componentDescriptionReader == null) {
      _componentDescriptionReader = new ComponentDescriptionReader();
    }

    return _componentDescriptionReader;
  }
}
