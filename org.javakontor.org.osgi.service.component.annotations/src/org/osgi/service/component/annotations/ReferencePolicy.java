package org.osgi.service.component.annotations;

public enum ReferencePolicy {

  STATIC("static"), // default

  DYNAMIC("dynamic"), //

  ;

  public final String value;

  public String value() {
    return value;
  }

  private ReferencePolicy(final String value) {
    this.value = value;
  }

  private static final ReferencePolicy[] ENUM_VALS = values();

  public static ReferencePolicy from(final String value) {

    for (final ReferencePolicy known : ENUM_VALS) {
      if (known.value.equalsIgnoreCase(value)) {
        return known;
      }
    }

    return STATIC;

  }

}
