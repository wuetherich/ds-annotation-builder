package org.osgi.service.component.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Reference {

  String name() default "";

  Class<?> service() default Object.class;

  ReferenceCardinality cardinality() default ReferenceCardinality.MANDATORY;

  ReferencePolicy policy() default ReferencePolicy.STATIC;

  String target() default "";

  String unbind() default "";

}
