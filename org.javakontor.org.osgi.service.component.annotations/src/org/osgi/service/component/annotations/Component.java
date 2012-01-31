package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface Component {

  String name() default "";

  boolean enabled() default true;

  boolean immediate() default false;

  String factory() default "";

  boolean servicefactory() default false;

  ConfigurationPolicy configurationPolicy() default ConfigurationPolicy.OPTIONAL;

  Class<?>[] service() default {};

  String[] property() default {};

  String[] properties() default {};

}
