package com.wuetherich.osgi.ds.annotations;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public enum DsAnnotationVersion {

  V_1_0("http://www.osgi.org/xmlns/scr/v1.0.0", "1.0 (OSGi Release 4.0/4.1)"), V_1_1(
      "http://www.osgi.org/xmlns/scr/v1.1.0", "1.1 (OSGi Release 4.2)"), V_1_2("http://www.osgi.org/xmlns/scr/v1.2.0",
      "1.2 (OSGi Release 4.3+)");

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
