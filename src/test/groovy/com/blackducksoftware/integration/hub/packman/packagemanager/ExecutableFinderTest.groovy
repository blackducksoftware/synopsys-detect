package com.blackducksoftware.integration.hub.packman.packagemanager

import org.apache.commons.lang3.StringUtils
import org.junit.Assert
import org.junit.Test

class ExecutableFinderTest {
    @Test
    public void testFindingMan() {
        ExecutableFinder executableFinder = new ExecutableFinder()
        String manPath = executableFinder.findExecutable('man')
        Assert.assertFalse(StringUtils.isBlank(manPath))
        Assert.assertTrue(manPath.endsWith('man'))
    }

    @Test
    public void testFindingCommandThatDoesNotExist() {
        ExecutableFinder executableFinder = new ExecutableFinder()
        String manPath = executableFinder.findExecutable('man_that_does_not_exist')
        Assert.assertEquals(null, manPath)
    }
}
