package com.blackducksoftware.integration.hub.detect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.util.filter.DetectFilter;
import com.blackducksoftware.integration.hub.detect.util.filter.DetectNameFilter;
import com.blackducksoftware.integration.hub.detect.util.filter.DetectOverrideableFilter;

public class AllAndNoneExcludedIncludedFilterTest {

    // NonOverridable

    @Test
    public void testNonOverridableNormalExcludeList() {
        DetectFilter filter = new DetectNameFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableNormalIncludeList() {
        DetectFilter filter = new DetectNameFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcluded() {
        DetectFilter filter = new DetectNameFilter("ALL", "");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testNonOverridableAllExcludedWithIgnoredIncludes() {
        DetectFilter filter = new DetectNameFilter("ALL", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    // Overridable

    @Test
    public void testOverridableNormalExcludeList() {
        DetectFilter filter = new DetectOverrideableFilter("docker,rubygems", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertTrue(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableNormalIncludeList() {
        DetectFilter filter = new DetectOverrideableFilter("", "docker,rubygems");
        assertTrue(filter.shouldInclude("docker"));
        assertTrue(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcluded() {
        DetectFilter filter = new DetectOverrideableFilter("ALL", "");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }

    @Test
    public void testOverridableAllExcludedWithIgnoredIncludes() {
        DetectFilter filter = new DetectOverrideableFilter("ALL", "docker,rubygems");
        assertFalse(filter.shouldInclude("docker"));
        assertFalse(filter.shouldInclude("rubygems"));
        assertFalse(filter.shouldInclude("gradle"));
    }
}