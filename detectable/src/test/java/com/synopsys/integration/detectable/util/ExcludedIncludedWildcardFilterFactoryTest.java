package com.synopsys.integration.detectable.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.util.ExcludedIncludedWildcardFilter;

public class ExcludedIncludedWildcardFilterFactoryTest {
    private static ExcludedIncludedWildcardFilterFactory excludedIncludedWildcardFilterFactory;

    @BeforeAll
    private static void setup() {
        excludedIncludedWildcardFilterFactory = new ExcludedIncludedWildcardFilterFactory();
    }

    @Test
    public void testCreationMixed() {
        ExcludedIncludedWildcardFilter filter = excludedIncludedWildcardFilterFactory.create(null, Collections.emptyList());
        assertTrue(filter.shouldInclude("anything"));
    }

    @Test
    public void testExclude() {
        ExcludedIncludedWildcardFilter filter = excludedIncludedWildcardFilterFactory.create(Arrays.asList("excludeme", "andme"), null);
        assertFalse(filter.shouldInclude("andme"));
    }

    @Test
    public void testDontExclude() {
        ExcludedIncludedWildcardFilter filter = excludedIncludedWildcardFilterFactory.create(Arrays.asList("excludeme"), null);
        assertTrue(filter.shouldInclude("somethingelse"));
    }
}
