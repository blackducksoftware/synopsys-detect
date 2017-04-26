package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId

class DependencyGatherer {
    String includedConfigurations
    String excludedConfigurations
    String includedProjects
    String excludedProjects

    private DependencyGathererConfig dependencyGathererConfig
    private Map<ExternalId, DependencyNode> addedNodes = new HashMap<>()

    DependencyNode getFullyPopulatedRootNode(final Project rootProject) {
        dependencyGathererConfig = new DependencyGathererConfig(includedConfigurations, excludedConfigurations, includedProjects, excludedProjects)
        def group = rootProject.group
        def name = rootProject.name
        def version = rootProject.version

        List<DependencyNode> children = []
        DependencyNode rootProjectNode = new DependencyNode(name, version, new MavenExternalId(group, name, version), children)

        rootProject.allprojects.each { project ->
            if (dependencyGathererConfig.shouldIncludeProject(project)) {
                getProjectDependencies(project, children);
            }
        }

        return rootProjectNode;
    }

    private void getProjectDependencies(final Project project, final List<DependencyNode> children) {
        project.configurations.each { configuration ->
            if (dependencyGathererConfig.shouldIncludeConfiguration(configuration)) {
                configuration.resolvedConfiguration.firstLevelModuleDependencies.each {
                    children.add(createCommonDependencyNode(it))
                }
            }
        }
    }

    private DependencyNode createCommonDependencyNode(final ResolvedDependency resolvedDependency) {
        def group = resolvedDependency.moduleGroup
        def name = resolvedDependency.moduleName
        def version = resolvedDependency.moduleVersion

        def mavenExternalId = new MavenExternalId(group, name, version)

        if (addedNodes.containsKey(mavenExternalId)) {
            return addedNodes.get(mavenExternalId)
        } else {
            final List<DependencyNode> children = []
            def dependencyNode = new DependencyNode(name, version, mavenExternalId, children)
            for (final ResolvedDependency child : resolvedDependency.getChildren()) {
                children.add(createCommonDependencyNode(child))
            }

            addedNodes.put(mavenExternalId, dependencyNode)
            return dependencyNode
        }
    }
}
