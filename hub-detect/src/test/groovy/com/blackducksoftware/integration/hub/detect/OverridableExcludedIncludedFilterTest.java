package com.blackducksoftware.integration.hub.detect;

import com.synopsys.integration.util.ExcludedIncludedFilter;
import org.junit.Test;

import static org.junit.Assert.*;

public class OverridableExcludedIncludedFilterTest {

    // NonOverridable

    @Test
    public void testNonOverridableNormalExcludeList() {
        ExcludedIncludedFilter filter = new ExcludedIncludedFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableNormalIncludeList() {
        ExcludedIncludedFilter filter = new ExcludedIncludedFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcluded() {
        ExcludedIncludedFilter filter = new ExcludedIncludedFilter("ALL", "");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcludedWithIgnoredIncludes() {
        ExcludedIncludedFilter filter = new ExcludedIncludedFilter("ALL", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    // Overridable

    @Test
    public void testOverridableNormalExcludeList() {
        ExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableNormalIncludeList() {
        ExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcluded() {
        ExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("ALL", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcludedWithIgnoredIncludes() {
        ExcludedIncludedFilter filter = new OverridableExcludedIncludedFilter("ALL", "docker,rubygems");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }
}