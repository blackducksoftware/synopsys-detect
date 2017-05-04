package com.blackducksoftware.integration.hub.packman.util

import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.Test

class FileFinderTest {
    @Test
    public void testFindingMan() {
        FileFinder fileFinder = new FileFinder()
        String manPath = fileFinder.findExecutablePath('man')
        Assert.assertFalse(StringUtils.isBlank(manPath))
        Assert.assertTrue(manPath.endsWith('man'))
    }

    @Test
    public void testFindingCommandThatDoesNotExist() {
        FileFinder fileFinder = new FileFinder()
        String manPath = fileFinder.findExecutablePath('man_that_does_not_exist')
        Assert.assertEquals(null, manPath)
    }
}
