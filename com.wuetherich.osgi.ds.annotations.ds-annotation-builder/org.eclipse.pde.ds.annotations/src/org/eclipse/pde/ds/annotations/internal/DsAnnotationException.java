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
package org.eclipse.pde.ds.annotations.internal;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public class DsAnnotationException extends RuntimeException {

  /** - */
  private static final long serialVersionUID = 1L;

  private String            _annotationField;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationException}.
   * </p>
   * 
   * @param message
   * @param cause
   */
  public DsAnnotationException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationException}.
   * </p>
   * 
   * @param message
   */
  public DsAnnotationException(String message) {
    super(message);
  }

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationException}.
   * </p>
   * 
   * @param message
   * @param cause
   * @param field
   */
  public DsAnnotationException(String message, Throwable cause, String field) {
    super(message, cause);

    setAnnotationField(field);
  }

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationException}.
   * </p>
   * 
   * @param message
   * @param field
   */
  public DsAnnotationException(String message, String field) {
    super(message);

    setAnnotationField(field);
  }

  public String getAnnotationField() {
    return _annotationField;
  }

  public boolean hasAnnotationField() {
    return _annotationField != null;
  }

  public void setAnnotationField(String annotationField) {
    _annotationField = annotationField;
  }
}
