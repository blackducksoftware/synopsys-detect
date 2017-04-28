package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.junit.Assert
import org.junit.Test

class DependencyGathererConfigTest {
    @Test
    public void testConstructor() {
        String includedConfigurations = ""
        String excludedConfigurations = ""
        String includedProjects = ""
        String excludedProjects = ""

        def dependencyGathererConfig = new DependencyGathererConfig(includedConfigurations, excludedConfigurations, includedProjects, excludedProjects)
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'whatever'}] as Configuration))
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeProject([getName : {'whatever'}] as Project))

        dependencyGathererConfig = new DependencyGathererConfig(null, null, null, null)
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'whatever'}] as Configuration))
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeProject([getName : {'whatever'}] as Project))
    }

    @Test
    public void testExcluded() {
        String includedConfigurations = ""
        String excludedConfigurations = "bad"
        String includedProjects = ""
        String excludedProjects = "really_bad,also_really_bad"

        def dependencyGathererConfig = new DependencyGathererConfig(includedConfigurations, excludedConfigurations, includedProjects, excludedProjects)
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'whatever'}] as Configuration))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'bad'}] as Configuration))
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeProject([getName : {'whatever'}] as Project))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeProject([getName : {'really_bad'}] as Project))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeProject([getName : {'also_really_bad'}] as Project))
    }

    @Test
    public void testIncludedAndExcluded() {
        String includedConfigurations = "good,bad"
        String excludedConfigurations = "bad"
        String includedProjects = "good"
        String excludedProjects = "really_bad,also_really_bad"

        def dependencyGathererConfig = new DependencyGathererConfig(includedConfigurations, excludedConfigurations, includedProjects, excludedProjects)
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'whatever'}] as Configuration))
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'good'}] as Configuration))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeConfiguration([getName : {'bad'}] as Configuration))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeProject([getName : {'whatever'}] as Project))
        Assert.assertTrue(dependencyGathererConfig.shouldIncludeProject([getName : {'good'}] as Project))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeProject([getName : {'really_bad'}] as Project))
        Assert.assertFalse(dependencyGathererConfig.shouldIncludeProject([getName : {'also_really_bad'}] as Project))
    }
}
