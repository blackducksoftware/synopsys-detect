package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import org.apache.commons.lang3.StringUtils
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class DependencyGathererConfig {
    private Set<String> includedConfigurationsSet
    private Set<String> excludedConfigurationsSet
    private Set<String> includedProjectsSet
    private Set<String> excludedProjectsSet

    public DependencyGathererConfig(String includedConfigurations, String excludedConfigurations, String includedProjects, String excludedProjects) {
        includedConfigurationsSet = createSetFromString(includedConfigurations)
        excludedConfigurationsSet = createSetFromString(excludedConfigurations)
        includedProjectsSet = createSetFromString(includedProjects)
        excludedProjectsSet = createSetFromString(excludedProjects)
    }

    public boolean shouldIncludeConfiguration(Configuration configuration) {
        return shouldInclude(configuration.name, includedConfigurationsSet, excludedConfigurationsSet)
    }

    public boolean shouldIncludeProject(Project project) {
        return shouldInclude(project.name, includedProjectsSet, excludedProjectsSet)
    }

    private boolean shouldInclude(String needle, Set<String> includeSet, Set<String> excludeSet) {
        if (excludeSet.contains(needle)) {
            return false
        }

        if (includeSet.size() > 0 && !includeSet.contains(needle)) {
            return false
        }

        return true
    }

    private Set<String> createSetFromString(String s) {
        new HashSet<String>(StringUtils.trimToEmpty(s).tokenize(',').collect { it.trim() })
    }
}
