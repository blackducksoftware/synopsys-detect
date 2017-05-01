package com.blackducksoftware.integration.hub.packman.util

import org.junit.Assert
import org.junit.Test

class ExcludedIncludedFilterTest {
    @Test
    public void testConstructor() {
        def excludedIncludedFilter = new ExcludedIncludedFilter("", "")
        Assert.assertTrue(excludedIncludedFilter.shouldInclude('whatever'))

        excludedIncludedFilter = new ExcludedIncludedFilter(null, null)
        Assert.assertTrue(excludedIncludedFilter.shouldInclude('whatever'))
    }

    @Test
    public void testExcluded() {
        String includedProjects = ""
        String excludedProjects = "really_bad,also_really_bad"

        def excludedIncludedFilter = new ExcludedIncludedFilter("bad", "")
        Assert.assertTrue(excludedIncludedFilter.shouldInclude('whatever'))
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('bad'))

        excludedIncludedFilter = new ExcludedIncludedFilter("really_bad,also_really_bad", null)
        Assert.assertTrue(excludedIncludedFilter.shouldInclude('whatever'))
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('really_bad'))
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('also_really_bad'))
    }

    @Test
    public void testIncludedAndExcluded() {
        def excludedIncludedFilter = new ExcludedIncludedFilter("bad", "good,bad")
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('whatever'))
        Assert.assertTrue(excludedIncludedFilter.shouldInclude('good'))
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('bad'))

        excludedIncludedFilter = new ExcludedIncludedFilter("really_bad,also_really_bad", "good")
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('whatever'))
        Assert.assertTrue(excludedIncludedFilter.shouldInclude('good'))
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('really_bad'))
        Assert.assertFalse(excludedIncludedFilter.shouldInclude('also_really_bad'))
    }
}
