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
package org.eclipse.pde.ds.annotations;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public enum DsAnnotationVersion {

  V_1_0("http://www.osgi.org/xmlns/scr/v1.0.0", "1.0 (OSGi Release 4.0/4.1)"), V_1_1(
      "http://www.osgi.org/xmlns/scr/v1.1.0", "1.1 (OSGi Release 4.2)"), V_1_2("http://www.osgi.org/xmlns/scr/v1.2.0",
      "1.2 (OSGi Release 4.3/5.0)"), V_1_3("http://www.osgi.org/xmlns/scr/v1.3.0", "1.3 (OSGi Release 6.0)");

  /** - */
  private String _xmlns;

  /** - */
  private String _description;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationVersion}.
   * </p>
   * 
   * @param xmlns
   */
  private DsAnnotationVersion(String xmlns, String description) {
    this._xmlns = xmlns;
    this._description = description;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getXmlns() {
    return _xmlns;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getDescription() {
    return _description;
  }

  /**
   * <p>
   * </p>
   * 
   * @param other
   * @return
   */
  public boolean greaterThan(DsAnnotationVersion other) {
    return this.getXmlns().compareTo(other.getXmlns()) > 0;
  }

  /**
   * <p>
   * </p>
   * 
   * @param namespace
   * @return
   */
  public static DsAnnotationVersion getFromNamespace(String namespace) {

    //
    for (DsAnnotationVersion version : values()) {
      if (version.getXmlns().equals(namespace)) {
        return version;
      }
    }

    //
    return null;
  }
}
