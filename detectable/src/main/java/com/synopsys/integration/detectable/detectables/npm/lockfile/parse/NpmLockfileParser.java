/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

public class NpmLockfileParser {
    private final Logger logger = LoggerFactory.getLogger(NpmLockfileParser.class);
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NpmLockfileParser(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult parse(final String sourcePath, final Optional<String> packageJsonText, final String lockFileText, final boolean includeDevDependencies) {
        final MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        logger.info("Parsing lock file text: ");
        logger.debug(lockFileText);

        Optional<PackageJson> packageJson = Optional.empty();
        if (packageJsonText.isPresent()) {
            logger.debug(packageJsonText.get());
            packageJson = Optional.of(gson.fromJson(packageJsonText.get(), PackageJson.class));
        }

        final PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);
        logger.debug(lockFileText);

        logger.info("Processing project.");
        if (packageLock.dependencies != null) {
            logger.info(String.format("Found %d dependencies.", packageLock.dependencies.size()));
            //Convert to our custom format
            final NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
            final NpmProject project = dependencyConverter.convertLockFile(packageLock, packageJson);

            //First we will recreate the graph from the resolved npm dependencies
            for (NpmDependency resolved : project.getResolvedDependencies()){
                transformTreeToGraph(resolved, project, dependencyGraph, includeDevDependencies);
            }

            //Then we will add relationships between the project (root) and the graph
            boolean atLeastOneRequired = project.getDeclaredDependencies().size() > 0 || project.getDeclaredDevDependencies().size() > 0;
            if (atLeastOneRequired) {
                addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDependencies(), dependencyGraph);
                if (includeDevDependencies) {
                    addRootDependencies(project.getResolvedDependencies(), project.getDeclaredDevDependencies(), dependencyGraph);
                }
            } else {
                project.getResolvedDependencies()
                    .stream()
                    .map(NpmDependency::getGraphDependency)
                    .forEach(dependencyGraph::addChildToRoot);
            }

        } else {
            logger.info("Lock file did not have a 'dependencies' section.");
        }
        logger.info("Finished processing.");
        final ExternalId projectId = externalIdFactory.createNameVersionExternalId(Forge.NPM, packageLock.name, packageLock.version);
        final CodeLocation codeLocation = new CodeLocation(dependencyGraph, projectId);
        return new NpmParseResult(packageLock.name, packageLock.version, codeLocation);
    }

    private void addRootDependencies(List<NpmDependency> resolvedDependencies, List<NpmRequires> requires, final MutableDependencyGraph dependencyGraph) {
        for (NpmRequires dependency : requires) {
            NpmDependency resolved = firstDependencyWithName(resolvedDependencies, dependency.getName());
            if (resolved != null) {
                dependencyGraph.addChildToRoot(resolved.getGraphDependency());
            } else {
                logger.error("No dependency found for package: " + dependency.getName());
            }
        }
    }

    private void transformTreeToGraph(final NpmDependency npmDependency, final NpmProject npmProject, final MutableDependencyGraph dependencyGraph, final boolean includeDevDependencies) {
        if (!shouldIncludeDependency(npmDependency, includeDevDependencies))
            return;

        npmDependency.getRequires().forEach(required -> {
            logger.debug("Required package: " + required.getName() + " of version: " + required.getFuzzyVersion());
            final NpmDependency resolved = lookupDependency(npmDependency, npmProject, required.getName());
            if (resolved != null) {
                logger.debug("Found package: " + resolved.getName() + "with version: " + resolved.getVersion());
                dependencyGraph.addChildWithParent(resolved.getGraphDependency(), npmDependency.getGraphDependency());
            } else {
                logger.error("No dependency found for package: " + required.getName());
            }
        });

        npmDependency.getDependencies().forEach(child -> transformTreeToGraph(child, npmProject, dependencyGraph, includeDevDependencies));
    }

    //returns the first dependency in the following order: directly under this dependency, under a parent, under the project
    private NpmDependency lookupDependency(final NpmDependency npmDependency, final NpmProject project, final String name) {
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

    private NpmDependency firstDependencyWithName(final List<NpmDependency> dependencies, final String name) {
        for (final NpmDependency current : dependencies) {
            if (current.getName().equals(name)) {
                return current;
            }
        }
        return null;
    }

    private boolean shouldIncludeDependency(final NpmDependency packageLockDependency, final boolean includeDevDependencies) {
        if (packageLockDependency.isDevDependency()) {
            return includeDevDependencies;
        }
        return true;
    }
}