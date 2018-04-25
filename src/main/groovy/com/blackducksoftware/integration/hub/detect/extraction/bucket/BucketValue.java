package com.blackducksoftware.integration.hub.detect.extraction.bucket;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import groovy.transform.TypeChecked;

@TypeChecked
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BucketValue {
    String value() default "";
}
