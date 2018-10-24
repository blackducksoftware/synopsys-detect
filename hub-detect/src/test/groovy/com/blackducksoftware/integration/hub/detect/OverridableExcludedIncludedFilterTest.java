package com.blackducksoftware.integration.hub.detect;

import org.junit.Test;

import static org.junit.Assert.*;

public class OverridableExcludedIncludedFilterTest {

    @Test
    public void testNormalExcludeList() {
        OverridableExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNormalIncludeList() {
        OverridableExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testAllExcluded() {
        OverridableExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("ALL", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testAllExcludedWithIgnoredIncludes() {
        OverridableExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("ALL", "docker,rubygems");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }
}