package com.wuetherich.osgi.ds.annotations;

/**
 * <p>
 * </p>
 * 
 * @author Gerd W&uuml;therich (gerd@gerd-wuetherich.de)
 */
public enum DsAnnotationVersion {

  V_1_0("http://www.osgi.org/xmlns/scr/v1.0.0"), V_1_1("http://www.osgi.org/xmlns/scr/v1.1.0"), V_1_2(
      "http://www.osgi.org/xmlns/scr/v1.2.0");

  /** - */
  private String _xmlns;

  /**
   * <p>
   * Creates a new instance of type {@link DsAnnotationVersion}.
   * </p>
   * 
   * @param xmlns
   */
  private DsAnnotationVersion(String xmlns) {
    this._xmlns = xmlns;
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
