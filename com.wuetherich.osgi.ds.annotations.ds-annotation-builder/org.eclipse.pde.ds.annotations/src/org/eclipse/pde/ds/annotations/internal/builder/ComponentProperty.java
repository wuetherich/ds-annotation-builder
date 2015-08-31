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
package org.eclipse.pde.ds.annotations.internal.builder;

import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class ComponentProperty {

  /** - */
  private String _name;

  /** - */
  private String _type;

  /** - */
  private String _value;
  
  /**
   * <p>
   * Creates a new instance of type {@link ComponentProperty}.
   * </p>
   */
  public ComponentProperty() {
    //
  }

  /**
   * <p>
   * Creates a new instance of type {@link ComponentProperty}.
   * </p>
   * 
   * @param name
   * @param value
   */
  public ComponentProperty(String name, String value) {
    Assert.isNotNull(name);
    Assert.isNotNull(value);

    this._name = name;
    this._value = value;
  }

  /**
   * <p>
   * Creates a new instance of type {@link ComponentProperty}.
   * </p>
   * 
   * @param name
   * @param type
   * @param value
   */
  public ComponentProperty(String name, String type, String value) {
    Assert.isNotNull(name);
    Assert.isNotNull(type);
    Assert.isNotNull(value);

    this._name = name;
    this._type = type;
    this._value = value;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getName() {
    return _name;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getType() {
    return _type;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getValue() {
    return _value;
  }

  /**
   * <p>
   * </p>
   * 
   * @param _name
   */
  public void setName(String _name) {
    this._name = _name;
  }

  /**
   * <p>
   * </p>
   * 
   * @param _type
   */
  public void setType(String _type) {
    this._type = _type;
  }

  /**
   * <p>
   * </p>
   * 
   * @param _value
   */
  public void setValue(String _value) {
    this._value = _value;
  }

}
