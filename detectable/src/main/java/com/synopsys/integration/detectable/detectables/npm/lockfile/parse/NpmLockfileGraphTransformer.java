package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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

    public DependencyGraph transform(PackageLock packageLock, NpmProject project, List<NameVersion> externalDependencies, List<String> workspaces) {
        DependencyGraph dependencyGraph = new BasicDependencyGraph();

        logger.debug("Processing project.");
        if (packageLock.packages != null) {
            logger.debug(String.format("Found %d packages in the lockfile.", packageLock.packages.size()));

            //First we will recreate the graph from the resolved npm dependencies
            for (NpmDependency resolved : project.getResolvedDependencies()) {
                if (resolved.getName().contains("react-components")) {
                    System.out.println("");
                    
                    // TODO debug
                    
                    for (NpmDependency dependency : resolved.getDependencies()) {
                        //System.out.println(dependency.getName());
                        // TODO go after child dependencies until run out of them then go after requires
                        List<NpmDependency> dependencies = dependency.getDependencies();
                        Collections.sort(dependencies, new DependencyComparator());
                        for (NpmDependency childDependency1 : dependencies) {
                           // System.out.println(childDependency1.getName());
                            List<NpmDependency> child1Dependencies = childDependency1.getDependencies();
                            Collections.sort(child1Dependencies, new DependencyComparator());
                            for (NpmDependency childDependency2: child1Dependencies) {
                                System.out.println(childDependency2.getName());
                            }
                        }
                    }
                    
                    for (NpmRequires requires : resolved.getRequires()) {
                        System.out.println(requires.getName() + ": " + requires.getFuzzyVersion());
                    }
                    
                    
                }
                transformTreeToGraph(resolved, project, dependencyGraph, externalDependencies, workspaces);
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
            logger.debug("Lock file did not have a 'packages' section.");
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

    private void transformTreeToGraph(NpmDependency npmDependency, NpmProject npmProject, DependencyGraph dependencyGraph, List<NameVersion> externalDependencies, List<String> workspaces) {
        if (!shouldIncludeDependency(npmDependency)) {
            return;
        }
        
        // TODO check all code paths some callers still sending absolute
        // add workspaces as direct dependencies
        if (workspaces != null && !StringUtils.isBlank(npmDependency.getName()) &&
                workspaces.stream().anyMatch(x -> x.equals(npmDependency.getName()))) {
            dependencyGraph.addDirectDependency(npmDependency);
            
            // add workspace requires
            for (NpmRequires required : npmDependency.getRequires()) {
                Dependency workspaceDependency = lookupDependency(required.getName(), npmDependency, npmProject, externalDependencies);
                dependencyGraph.addChildrenToRoot(workspaceDependency);
            }
            
        }
        
        // TODO is it okay to do this again if the workspace triggered it above?
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

        npmDependency.getDependencies().forEach(child -> transformTreeToGraph(child, npmProject, dependencyGraph, externalDependencies, workspaces));
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
    
    class DependencyComparator implements Comparator<NpmDependency> {

        @Override
        public int compare(NpmDependency o1, NpmDependency o2) {
                NpmDependency one = (NpmDependency) o1;
                NpmDependency two = (NpmDependency) o2;
                // TODO Auto-generated method stub
                return one.getName().compareTo(two.getName());
        }
        
    }
}
