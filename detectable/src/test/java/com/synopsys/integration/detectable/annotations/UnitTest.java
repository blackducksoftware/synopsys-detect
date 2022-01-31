package com.synopsys.integration.detectable.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Test
@Tag("unit")
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitTest {
    // A meta-annotation for unit tests. Tests that are not dependent on a resource file should be considered a UnitTest
}
