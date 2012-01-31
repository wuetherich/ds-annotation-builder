package org.osgi.service.component.annotations;

public enum ConfigurationPolicy {

  IGNORE("ignore"), //

  OPTIONAL("optional"), // default

  REQUIRE("require"), //

  ;

  public final String value;

  public String value() {
    return value;
  }

  private ConfigurationPolicy(final String value) {
    this.value = value;
  }

  private static final ConfigurationPolicy[] ENUM_VALS = values();

  public static ConfigurationPolicy from(final String value) {

    for (final ConfigurationPolicy known : ENUM_VALS) {
      if (known.value.equalsIgnoreCase(value)) {
        return known;
      }
    }

    return OPTIONAL;

  }

}
