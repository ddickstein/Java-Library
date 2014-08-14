// Copyright (c) 2014 Daniel S. Dickstein

package library.java8.debugger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Annotation can be applied to fields or to methods that take 0 arguments
// If the annotation is applied to methods with > 0 arguments, it is ignored

@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Debuggable {
  String name() default "";
}