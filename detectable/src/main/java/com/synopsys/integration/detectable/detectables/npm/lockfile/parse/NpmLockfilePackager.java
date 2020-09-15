/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.npm.lockfile.parse;

import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
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
import com.synopsys.integration.detectable.util.MissingDependencyLogger;

public class NpmLockfilePackager {
    private final Logger logger = LoggerFactory.getLogger(NpmLockfilePackager.class);
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NpmLockfilePackager(Gson gson, ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult parse(@Nullable String packageJsonText, String lockFileText, boolean includeDevDependencies, MissingDependencyLogger missingDependencyLogger) {
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
                transformTreeToGraph(resolved, project, dependencyGraph, includeDevDependencies);
            }

            //Then we will add relationships between the project (root) and the graph
            boolean atLeastOneRequired = !project.getDeclaredDependencies().isEmpty() || !project.getDeclaredDevDependencies().isEmpty();
            if (atLeastOneRequired) {
                addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDependencies(), dependencyGraph, missingDependencyLogger);
                if (includeDevDependencies) {
                    addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDevDependencies(), dependencyGraph, missingDependencyLogger);
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
        ExternalId projectId = externalIdFactory.createNameVersionExternalId(Forge.NPMJS, packageLock.name, packageLock.version);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmParseResult(packageLock.name, packageLock.version, codeLocation);
    }

    private void addRootDependencies(List<NpmDependency> resolvedDependencies, List<NpmRequires> requires, MutableDependencyGraph dependencyGraph, MissingDependencyLogger missingDependencyLogger) {
        for (NpmRequires dependency : requires) {
            NpmDependency resolved = firstDependencyWithName(resolvedDependencies, dependency.getName());
            if (resolved != null) {
                dependencyGraph.addChildToRoot(resolved.getGraphDependency());
            } else {
                missingDependencyLogger.logError(dependency.getName(), logger, String.format("No dependency found for package: %s", dependency.getName()));
            }
        }
    }

    private void transformTreeToGraph(NpmDependency npmDependency, NpmProject npmProject, MutableDependencyGraph dependencyGraph, boolean includeDevDependencies) {
        if (!shouldIncludeDependency(npmDependency, includeDevDependencies))
            return;

        npmDependency.getRequires().forEach(required -> {
            logger.trace(String.format("Required package: %s of version: %s", required.getName(), required.getFuzzyVersion()));
            NpmDependency resolved = lookupDependency(npmDependency, npmProject, required.getName());
            if (resolved != null) {
                logger.trace(String.format("Found package: %s with version: %s", resolved.getName(), resolved.getVersion()));
                dependencyGraph.addChildWithParent(resolved.getGraphDependency(), npmDependency.getGraphDependency());
            } else {
                logger.error(String.format("No dependency found for package: %s", required.getName()));
            }
        });

        npmDependency.getDependencies().forEach(child -> transformTreeToGraph(child, npmProject, dependencyGraph, includeDevDependencies));
    }

    //returns the first dependency in the following order: directly under this dependency, under a parent, under the project
    private NpmDependency lookupDependency(NpmDependency npmDependency, NpmProject project, String name) {
        NpmDependency resolved = firstDependencyWithName(npmDependency.getDependencies(), name);

        if (resolved != null) {
            return resolved;
        } else {
            if (npmDependency.getParent().isPresent()) {
                return lookupDependency(npmDependency.getParent().get(), project, name);
            } else {
                return firstDependencyWithName(project.getResolvedDependencies(), name);
            }
        }
    }

    private NpmDependency firstDependencyWithName(List<NpmDependency> dependencies, String name) {
        for (NpmDependency current : dependencies) {
            if (current.getName().equals(name)) {
                return current;
            }
        }
        return null;
    }

    private boolean shouldIncludeDependency(NpmDependency packageLockDependency, boolean includeDevDependencies) {
        if (packageLockDependency.isDevDependency()) {
            return includeDevDependencies;
        }
        return true;
    }
}