package org.osgi.service.component.annotations;

/**
 * Controls whether component configurations must be satisfied depending on the presence of a corresponding
 * Configuration object in the OSGi Configuration Admin service. A corresponding configuration is a Configuration object
 * where the PID is the name of the component.
 * 
 * @since Release 4 Early Draft 2011.09
 * 
 */
public enum ConfigurationPolicy {

  /**
   * Always allow the component configuration to be satisfied and do not use the corresponding Configuration object even
   * if it is present.
   */
  IGNORE("ignore"),

  /**
   * Use the corresponding Configuration object if present but allow the component to be satisfied even if the
   * corresponding Configuration object is not present. (this is the <b>default</b>)
   */
  OPTIONAL("optional"),

  /**
   * There must be a corresponding Configuration object for the component configuration to become satisfied.
   */
  REQUIRE("require");

  private final String value;

  /**
   * non standard accessor to the xml element name
   * 
   * @return
   */
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
