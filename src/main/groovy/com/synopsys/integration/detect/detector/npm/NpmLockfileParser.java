/**
 * synopsys-detect
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
package com.synopsys.integration.detect.detector.npm;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.detector.npm.model.NpmDependency;
import com.synopsys.integration.detect.detector.npm.model.PackageJson;
import com.synopsys.integration.detect.detector.npm.model.PackageLock;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationType;
import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

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
            NpmDependencyConverter dependencyConverter = new NpmDependencyConverter(externalIdFactory);
            NpmDependency rootDependency = dependencyConverter.convertLockFile(packageLock, packageJson);
            traverse(rootDependency, dependencyGraph, true, includeDevDependencies);
        } else {
            logger.info("Lock file did not have a 'dependencies' section.");
        }
        logger.info("Finished processing.");
        final ExternalId projectId = externalIdFactory.createNameVersionExternalId(Forge.NPM, packageLock.name, packageLock.version);
        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.NPM, sourcePath, projectId, dependencyGraph).build();
        return new NpmParseResult(packageLock.name, packageLock.version, codeLocation);
    }

    private void traverse(NpmDependency npmDependency, MutableDependencyGraph dependencyGraph, boolean atRoot, boolean includeDevDependencies) {
        if (!shouldInclude(npmDependency, includeDevDependencies))
            return;

        npmDependency.getRequires().forEach(required -> {
            NpmDependency resolved = lookupDependency(npmDependency, required.getName());
            logger.debug("Required package: " + required.getName() + " of version: " + required.getFuzzyVersion());
            if (resolved != null) {
                logger.debug("Found package: " + resolved.getName() + "with version: " + resolved.getVersion());
                if (atRoot) {
                    dependencyGraph.addChildToRoot(resolved.getGraphDependency());
                } else {
                    dependencyGraph.addChildWithParent(resolved.getGraphDependency(), npmDependency.getGraphDependency());
                }
            } else {
                logger.error("No dependency found for package: " + required.getName());
            }
        });

        npmDependency.getDependencies().forEach(child -> traverse(child, dependencyGraph, false, includeDevDependencies));
    }

    //returns the first dependency directly under this dependency or under a parent
    private NpmDependency lookupDependency(NpmDependency npmDependency, String name) {
        for (NpmDependency current : npmDependency.getDependencies()) {
            if (current.getName().equals(name)) {
                return current;
            }
        }

        if (npmDependency.getParent().isPresent()) {
            return lookupDependency(npmDependency.getParent().get(), name);
        } else {
            return null;
        }
    }

    private boolean shouldInclude(final NpmDependency packageLockDependency, final boolean includeDevDependencies) {
        if (packageLockDependency.isDevDependency()) {
            return includeDevDependencies;
        }
        return true;
    }
}