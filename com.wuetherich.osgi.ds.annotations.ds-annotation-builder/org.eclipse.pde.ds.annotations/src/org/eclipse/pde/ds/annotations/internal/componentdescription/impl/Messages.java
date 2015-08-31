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
package org.eclipse.pde.ds.annotations.internal.componentdescription.impl;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

  private static final String BUNDLE_NAME = "org.eclipse.pde.ds.annotations.internal.componentdescription.impl.messages"; //$NON-NLS-1$

  public static String        ComponentDescription_INVALID_BIND_METHOD_NAME;

  public static String        ComponentDescription_INVALID_SERVICEFACTORY_DECLARATION;

  public static String        ComponentDescription_INVALID_DERIVED_REFERENCE_NAME;

  public static String        ComponentDescription_INVALID_PROPERTY_TYPE;

  public static String        ComponentDescription_INVALID_REFERENCE_NAME;

  public static String        ComponentDescription_REFERENCE_NAME_NOT_UNIQUE;

  public static String        ComponentDescription_UNSUPPORTED_ANNOTATION_ACTIVATE;

  public static String        ComponentDescription_UNSUPPORTED_ANNOTATION_DEACTIVATE;

  public static String        ComponentDescription_UNSUPPORTED_ANNOTATION_MODIFIED;

  public static String        ComponentDescription_UNSUPPORTED_ATTRIBUTE_CONFIGURATIONPID;

  public static String        ComponentDescription_UNSUPPORTED_ATTRIBUTE_CONFIGURATIONPOLICY;

  public static String        ComponentDescription_UNSUPPORTED_ATTRIBUTE_POLICYOPTION;

  public static String        ComponentDescription_UNSUPPORTED_ATTRIBUTE_UPDATED;

  public static String        ComponentDescription_INVALID_SERVICE_TYPE;

  public static String        ComponentDescription_INVALID_FILTER;

  public static String        ComponentDescription_NON_EXISTING_UNBIND_METHOD;

  public static String        ComponentDescription_NON_EXISTING_UPDATED_METHOD;

  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
