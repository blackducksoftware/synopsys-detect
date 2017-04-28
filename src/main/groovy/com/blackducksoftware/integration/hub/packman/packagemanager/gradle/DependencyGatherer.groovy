package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvedDependency
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId

class DependencyGatherer {
    private final Logger logger = LoggerFactory.getLogger(DependencyGatherer.class)

    String includedConfigurations
    String excludedConfigurations
    String includedProjects
    String excludedProjects

    private DependencyGathererConfig dependencyGathererConfig
    private Set<String> alreadyAddedIds
    private DependencyNodeBuilder dependencyNodeBuilder

    DependencyNode getFullyPopulatedRootNode(final Project rootProject) {
        def group = rootProject.group
        def name = rootProject.name
        def version = rootProject.version
        DependencyNode rootProjectNode = new DependencyNode(name, version, new MavenExternalId(group, name, version))

        dependencyGathererConfig = new DependencyGathererConfig(includedConfigurations, excludedConfigurations, includedProjects, excludedProjects)
        alreadyAddedIds = new HashSet<>()
        dependencyNodeBuilder = new DependencyNodeBuilder(rootProjectNode)

        rootProject.allprojects.each { project ->
            if (dependencyGathererConfig.shouldIncludeProject(project)) {
                project.configurations.each { configuration ->
                    if (dependencyGathererConfig.shouldIncludeConfiguration(configuration)) {
                        configuration.resolvedConfiguration.firstLevelModuleDependencies.each { dependency ->
                            addDependencyNodeToParent(configuration.name, rootProjectNode, dependency)
                        }
                    }
                }
            }
        }

        return rootProjectNode;
    }

    private void addDependencyNodeToParent(String configurationName, DependencyNode parentDependencyNode, final ResolvedDependency resolvedDependency) {
        def group = resolvedDependency.moduleGroup
        def name = resolvedDependency.moduleName
        def version = resolvedDependency.moduleVersion

        def mavenExternalId = new MavenExternalId(group, name, version)
        def dependencyNode = new DependencyNode(name, version, mavenExternalId)
        dependencyNodeBuilder.addChildNodeWithParents(dependencyNode, [parentDependencyNode])
        if (alreadyAddedIds.add(mavenExternalId.createDataId())) {
            for (ResolvedDependency child : resolvedDependency.getChildren()) {
                /**
                 * A ResolvedDependency will include ALL children from ALL Configurations, regardless of the Configuration it came from
                 */
                if (configurationName == child.configuration) {
                    addDependencyNodeToParent(configurationName, dependencyNode, child)
                }
            }
        }
    }
}
