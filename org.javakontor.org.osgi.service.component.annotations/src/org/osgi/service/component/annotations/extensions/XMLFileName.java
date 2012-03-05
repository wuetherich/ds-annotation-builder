/**
 * 
 */
package org.osgi.service.component.annotations.extensions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Allows to override the generated XML filename for the generated component.xml
 * <p>
 * <b>This is a non Standard extension</b>
 * </p>
 * 
 * @author laeubi
 * 
 */
@Retention(RetentionPolicy.SOURCE)
public @interface XMLFileName {
  /**
   * 
   * @return the name of the file, it is suffixed with .xml
   */
  public String value();

}
