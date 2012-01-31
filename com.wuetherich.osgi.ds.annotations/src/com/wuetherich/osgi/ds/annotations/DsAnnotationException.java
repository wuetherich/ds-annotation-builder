package com.wuetherich.osgi.ds.annotations;

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
