package com.empowerops.linqalike.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that an object (typically a collection)
 * cannot be modified. Once constructed, it is non-changing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Immutable {
}
