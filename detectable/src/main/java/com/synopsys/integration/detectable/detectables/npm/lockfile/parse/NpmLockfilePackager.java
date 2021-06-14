/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.npm.lockfile.NpmDependencyConverter;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmDependency;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmProject;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmRequires;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.PackageLock;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;
import com.synopsys.integration.util.NameVersion;

public class NpmLockfilePackager {
    private final Logger logger = LoggerFactory.getLogger(NpmLockfilePackager.class);
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NpmLockfilePackager(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult parse(@Nullable String packageJsonText, String lockFileText, boolean includeDevDependencies, boolean includePeerDependencies) {
        return parse(packageJsonText, lockFileText, includeDevDependencies, includePeerDependencies, new ArrayList<>());
    }

    public NpmParseResult parse(@Nullable String packageJsonText, String lockFileText, boolean includeDevDependencies, boolean includePeerDependencies, List<NameVersion> externalDependencies) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();

        Optional<PackageJson> packageJson = Optional.ofNullable(packageJsonText)
                                                .map(content -> gson.fromJson(content, PackageJson.class));

        PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);

        logger.debug("Processing project.");
        if (packageLock.dependencies != null) {
            logger.debug(String.format("Found %d dependencies in the lockfile.", packageLock.dependencies.size()));
            //Convert to our custom format
            NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
            NpmProject project = dependencyConverter.convertLockFile(packageLock, packageJson.orElse(null));

            //First we will recreate the graph from the resolved npm dependencies
            for (NpmDependency resolved : project.getResolvedDependencies()) {
                transformTreeToGraph(resolved, project, dependencyGraph, includeDevDependencies, includePeerDependencies, externalDependencies);
            }

            //Then we will add relationships between the project (root) and the graph
            boolean atLeastOneRequired = !project.getDeclaredDependencies().isEmpty() || !project.getDeclaredDevDependencies().isEmpty();
            if (atLeastOneRequired) {
                addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDependencies(), dependencyGraph, externalDependencies);
                if (includeDevDependencies) {
                    addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDevDependencies(), dependencyGraph, externalDependencies);
                }
            } else {
                project.getResolvedDependencies()
                    .stream()
                    .map(NpmDependency::getGraphDependency)
                    .forEach(dependencyGraph::addChildToRoot);
            }

            logger.debug(String.format("Found %d root dependencies.", dependencyGraph.getRootDependencies().size()));
        } else {
            logger.debug("Lock file did not have a 'dependencies' section.");
        }
        logger.debug("Finished processing.");
        ExternalId projectId;
        if (packageJson.isPresent()) {
            projectId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, packageJson.get().name, packageJson.get().version);
        } else {
            projectId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, packageLock.name, packageLock.version);
        }
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmParseResult(projectId.getName(), projectId.getVersion(), codeLocation);
    }

    private void addRootDependencies(List<NpmDependency> resolvedDependencies, List<NpmRequires> requires, MutableDependencyGraph dependencyGraph, List<NameVersion> externalDependencies) {
        for (NpmRequires dependency : requires) {
            Dependency resolved = lookupProjectOrExternal(dependency.getName(), resolvedDependencies, externalDependencies);
            if (resolved != null) {
                dependencyGraph.addChildToRoot(resolved);
            } else {
                logger.warn("No dependency found for package: " + dependency.getName());
            }
        }
    }

    private void transformTreeToGraph(NpmDependency npmDependency, NpmProject npmProject, MutableDependencyGraph dependencyGraph, boolean includeDevDependencies, boolean includePeerDependencies, List<NameVersion> externalDependencies) {
        if (!shouldIncludeDependency(npmDependency, includeDevDependencies, includePeerDependencies)) {
            return;
        }

        npmDependency.getRequires().forEach(required -> {
            logger.trace(String.format("Required package: %s of version: %s", required.getName(), required.getFuzzyVersion()));
            Dependency resolved = lookupDependency(required.getName(), npmDependency, npmProject, externalDependencies);
            if (resolved != null) {
                logger.trace(String.format("Found package: %s with version: %s", resolved.getName(), resolved.getVersion()));
                dependencyGraph.addChildWithParent(resolved, npmDependency.getGraphDependency());
            } else {
                logger.warn("No dependency found for package: " + required.getName());
            }
        });

        npmDependency.getDependencies().forEach(child -> transformTreeToGraph(child, npmProject, dependencyGraph, includeDevDependencies, includePeerDependencies, externalDependencies));
    }

    private Dependency lookupProjectOrExternal(String name, List<NpmDependency> projectResolvedDependencies, List<NameVersion> externalDependencies) {
        Dependency projectDependency = firstDependencyWithName(projectResolvedDependencies, name);
        if (projectDependency != null) {
            return projectDependency;
        } else {
            Optional<NameVersion> externalNameVersion = externalDependencies.stream().filter(it -> it.getName().equals(name)).findFirst();
            return externalNameVersion.map(nameVersion -> new Dependency(nameVersion.getName(), nameVersion.getVersion(),
                externalIdFactory.createNameVersionExternalId(Forge.NPMJS, nameVersion.getName(), nameVersion.getVersion()))).orElse(null);
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
                return current.getGraphDependency();
            }
        }
        return null;
    }

    private boolean shouldIncludeDependency(NpmDependency packageLockDependency, boolean includeDevDependencies, boolean includePeerDependencies) {
        if (packageLockDependency.isDevDependency()) {
            return includeDevDependencies;
        }
        return true;
    }
}
