package com.synopsys.integration.detectable.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class ListSourcedExcludedIncludedWildcardFilterTest {

    @Test
    public void testCreationMixed() {
        ListSourcedExcludedIncludedWildcardFilter filter = ListSourcedExcludedIncludedWildcardFilter.create(null, new ArrayList<>(0));
        assertTrue(filter.shouldInclude("anything"));
    }

    @Test
    public void testExclude() {
        ListSourcedExcludedIncludedWildcardFilter filter = ListSourcedExcludedIncludedWildcardFilter.create(Arrays.asList("excludeme", "andme"), null);
        assertFalse(filter.shouldInclude("andme"));
    }

    @Test
    public void testDontExclude() {
        ListSourcedExcludedIncludedWildcardFilter filter = ListSourcedExcludedIncludedWildcardFilter.create(Arrays.asList("excludeme"), null);
        assertTrue(filter.shouldInclude("somethingelse"));
    }
}
