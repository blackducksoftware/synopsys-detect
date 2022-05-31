package com.synopsys.integration.detectable.detectable.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DetectableInfo {
    String forge();

    String language();

    String requirementsMarkdown();

    String name();
}
