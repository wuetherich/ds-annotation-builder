package org.osgi.service.component.annotations;

/**
 * Cardinality for the Reference annotation. Specifies if the reference is optional and if the component implementation
 * support a single bound service or multiple bound services.
 * 
 * @since Release 4 Early Draft 2011.09
 * 
 */
public enum ReferenceCardinality {

  /**
   * The reference is mandatory and multiple. That is, the reference has a cardinality of <code>1..n</code>.
   */
  AT_LEAST_ONE("1..n"), //
  /**
   * The reference is optional and multiple. That is, the reference has a cardinality of <code>0..n</code>.
   */
  MULTIPLE("0..n"), //
  /**
   * The reference is mandatory and unary. That is, the reference has a cardinality of <code>1..1</code>.
   */
  MANDATORY("1..1"),
  /**
   * The reference is optional and unary. That is, the reference has a cardinality of <code>0..1</code>.
   */
  OPTIONAL("0..1");

  private final String value;

  /**
   * non standard accessor for the xml value
   * 
   * @return
   */
  public String value() {
    return value;
  }

  private ReferenceCardinality(final String value) {
    this.value = value;
  }

  private static final ReferenceCardinality[] ENUM_VALS = values();

  public static ReferenceCardinality from(final String value) {
    for (final ReferenceCardinality known : ENUM_VALS) {
      if (known.value.equalsIgnoreCase(value)) {
        return known;
      }
    }
    return MANDATORY;
  }

}
