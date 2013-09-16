/*******************************************************************************
 * Copyright (c) 2011-2013 Gerd W&uuml;therich (gerd@gerd-wuetherich.de).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Gerd W&uuml;therich (gerd@gerd-wuetherich.de) - initial API and implementation
 ******************************************************************************/
package com.wuetherich.osgi.ds.annotations.internal;

import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationProblem {

  private String _message;

  private int    _charStart;

  private int    _charEnd;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationProblem}.
   * </p>
   * 
   * @param message
   * @param charStart
   * @param charEnd
   */
  public DsAnnotationProblem(String message, int charStart, int charEnd) {
    Assert.isNotNull(message);

    this._message = message;
    this._charStart = charStart;
    this._charEnd = charEnd;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public String getMessage() {
    return _message;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public int getCharStart() {
    return _charStart;
  }

  /**
   * <p>
   * </p>
   * 
   * @return
   */
  public int getCharEnd() {
    return _charEnd;
  }
}
