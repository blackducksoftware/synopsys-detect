package com.synopsys.integration.detectable.detectable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DetectableInfo {
    String forge();

    String language();

    String requirementsMarkdown();

    String name();
    
    DetectableAccuracyType accuracy();
}
