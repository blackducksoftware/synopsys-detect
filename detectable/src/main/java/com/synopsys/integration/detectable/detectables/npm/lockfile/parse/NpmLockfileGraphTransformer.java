package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.npm.NpmDependencyType;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmRequires;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.util.NameVersion;

public class NpmLockfileGraphTransformer {
    private final Logger logger = LoggerFactory.getLogger(NpmLockfileGraphTransformer.class);
    private final EnumListFilter<NpmDependencyType> npmDependencyTypeFilter;

    public NpmLockfileGraphTransformer(EnumListFilter<NpmDependencyType> npmDependencyTypeFilter) {
        this.npmDependencyTypeFilter = npmDependencyTypeFilter;
    }

    public DependencyGraph transform(PackageLock packageLock, NpmProject project, List<NameVersion> externalDependencies) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        logger.debug("Processing project.");
        if (packageLock.dependencies != null) {
            logger.debug(String.format("Found %d dependencies in the lockfile.", packageLock.dependencies.size()));

            //First we will recreate the graph from the resolved npm dependencies
            for (NpmDependency resolved : project.getResolvedDependencies()) {
                transformTreeToGraph(resolved, project, dependencyGraph, externalDependencies);
            }

            //Then we will add relationships between the project (root) and the graph
            boolean atLeastOneRequired = !project.getDeclaredDependencies().isEmpty()
                || !project.getDeclaredDevDependencies().isEmpty()
                || !project.getDeclaredPeerDependencies().isEmpty();
            if (atLeastOneRequired) {
                addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDependencies(), dependencyGraph, externalDependencies);
                if (npmDependencyTypeFilter.shouldInclude(NpmDependencyType.DEV)) {
                    addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDevDependencies(), dependencyGraph, externalDependencies);
                }
                if (npmDependencyTypeFilter.shouldInclude(NpmDependencyType.PEER)) {
                    addRootDependencies(project.getResolvedDependencies(), project.getDeclaredPeerDependencies(), dependencyGraph, externalDependencies);
                }
            } else {
                project.getResolvedDependencies()
                    .stream()
                    .filter(this::shouldIncludeDependency)
                    .forEach(dependencyGraph::addChildToRoot);
            }

            logger.debug(String.format("Found %d root dependencies.", dependencyGraph.getRootDependencies().size()));
        } else {
            logger.debug("Lock file did not have a 'dependencies' section.");
        }

        return dependencyGraph;
    }

    private void addRootDependencies(
        List<NpmDependency> resolvedDependencies,
        List<NpmRequires> requires,
        DependencyGraph dependencyGraph,
        List<NameVersion> externalDependencies
    ) {
        for (NpmRequires dependency : requires) {
            Dependency resolved = lookupProjectOrExternal(dependency.getName(), resolvedDependencies, externalDependencies);
            if (resolved != null) {
                dependencyGraph.addChildToRoot(resolved);
            } else {
                logger.debug("No resolved dependency found for dependency package: {}", dependency.getName());
            }
        }
    }

    private void transformTreeToGraph(NpmDependency npmDependency, NpmProject npmProject, DependencyGraph dependencyGraph, List<NameVersion> externalDependencies) {
        if (!shouldIncludeDependency(npmDependency)) {
            return;
        }

        npmDependency.getRequires().forEach(required -> {
            logger.trace(String.format("Required package: %s of version: %s", required.getName(), required.getFuzzyVersion()));
            Dependency resolved = lookupDependency(required.getName(), npmDependency, npmProject, externalDependencies);
            if (resolved != null) {
                logger.trace(String.format("Found package: %s with version: %s", resolved.getName(), resolved.getVersion()));
                dependencyGraph.addChildWithParent(resolved, npmDependency);
            } else {
                logger.debug("No resolved dependency found for required package: {}", required.getName());
            }
        });

        npmDependency.getDependencies().forEach(child -> transformTreeToGraph(child, npmProject, dependencyGraph, externalDependencies));
    }

    private Dependency lookupProjectOrExternal(String name, List<NpmDependency> projectResolvedDependencies, List<NameVersion> externalDependencies) {
        Dependency projectDependency = firstDependencyWithName(projectResolvedDependencies, name);
        if (projectDependency != null) {
            return projectDependency;
        } else {
            Optional<NameVersion> externalNameVersion = externalDependencies.stream().filter(it -> it.getName().equals(name)).findFirst();
            return externalNameVersion.map(nameVersion ->
                Dependency.FACTORY.createNameVersionDependency(Forge.NPMJS, nameVersion.getName(), nameVersion.getVersion())
            ).orElse(null);
        }
    }

    //returns the first dependency in the following order: directly under this dependency, under a parent, under the project, under external dependencies
    private Dependency lookupDependency(String name, NpmDependency npmDependency, NpmProject project, List<NameVersion> externalDependencies) {
        Dependency resolved = firstDependencyWithName(npmDependency.getDependencies(), name);

        if (resolved != null) {
            return resolved;
        } else if (npmDependency.getParent().isPresent()) {
            return lookupDependency(name, npmDependency.getParent().get(), project, externalDependencies);
        } else {
            return lookupProjectOrExternal(name, project.getResolvedDependencies(), externalDependencies);
        }
    }

    private Dependency firstDependencyWithName(List<NpmDependency> dependencies, String name) {
        for (NpmDependency current : dependencies) {
            if (current.getName().equals(name)) {
                return current;
            }
        }
        return null;
    }

    private boolean shouldIncludeDependency(NpmDependency packageLockDependency) {
        return (!packageLockDependency.isDevDependency() && !packageLockDependency.isPeerDependency()) // If the type is not dev or peer, we always want to include it.
            || (packageLockDependency.isDevDependency() && npmDependencyTypeFilter.shouldInclude(NpmDependencyType.DEV))
            || (packageLockDependency.isPeerDependency() && npmDependencyTypeFilter.shouldInclude(NpmDependencyType.PEER));
    }
}
