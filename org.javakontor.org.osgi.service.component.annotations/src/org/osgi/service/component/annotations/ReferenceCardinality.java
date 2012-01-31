package org.osgi.service.component.annotations;

public enum ReferenceCardinality {

  AT_LEAST_ONE("1..n"), //

  MULTIPLE("0..n"), //

  MANDATORY("1..1"), // default

  OPTIONAL("0..1"), //

  ;

  public final String value;

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
