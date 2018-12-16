/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.detector.npm;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageJson;
import com.blackducksoftware.integration.hub.detect.detector.npm.model.PackageLock;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependencyid.DependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameDependencyId;
import com.synopsys.integration.bdio.model.dependencyid.NameVersionDependencyId;
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

    public NpmParseResult parse(final String sourcePath, final String packageJsonText, final String lockFileText, final boolean includeDevDependencies) {
        final LazyExternalIdDependencyGraphBuilder lazyBuilder = new LazyExternalIdDependencyGraphBuilder();
        logger.info("Parsing lock file text: ");
        logger.debug(lockFileText);

        final PackageJson packageJson = gson.fromJson(packageJsonText, PackageJson.class);
        final PackageLock packageLock = gson.fromJson(lockFileText, PackageLock.class);

        List<String> rootPackages = new ArrayList<>();
        if (packageJson != null) {
            if (packageJson.dependencies != null)
                rootPackages.addAll(packageJson.dependencies.keySet());
            if (packageJson.devDependencies != null)
                rootPackages.addAll(packageJson.devDependencies.keySet());
        }

        logger.info("Processing project.");
        if (packageLock.dependencies != null) {
            logger.info(String.format("Found %d dependencies.", packageLock.dependencies.size()));
            packageLock.dependencies.forEach((name, npmDependency) -> {
                if (shouldInclude(npmDependency, includeDevDependencies)) {
                    final DependencyId dependency = createDependencyId(name, npmDependency.version);
                    setDependencyInfo(dependency, name, npmDependency.version, lazyBuilder);
                    if (rootPackages.contains(name)) {
                        lazyBuilder.addChildToRoot(dependency);
                    }
                    if (npmDependency.requires != null) {
                        npmDependency.requires.forEach((childName, childVersion) -> {
                            final DependencyId childId = createDependencyId(childName, childVersion);
                            setDependencyInfo(childId, childName, childVersion, lazyBuilder);
                            lazyBuilder.addChildWithParent(childId, dependency);
                        });
                    }
                }
            });
        } else {
            logger.info("Lock file did not have a 'dependencies' section.");
        }
        logger.info("Finished processing.");
        final DependencyGraph graph = lazyBuilder.build();
        final ExternalId projectId = externalIdFactory.createNameVersionExternalId(Forge.NPM, packageLock.name, packageLock.version);
        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.NPM, sourcePath, projectId, graph).build();
        return new NpmParseResult(packageLock.name, packageLock.version, codeLocation);
    }

    private boolean shouldInclude(final NpmDependency npmDependency, final boolean includeDevDependencies) {
        boolean isDev = false;
        if (npmDependency.dev != null && npmDependency.dev == true) {
            isDev = true;
        }
        if (isDev) {
            return includeDevDependencies;
        }
        return true;
    }

    private DependencyId createDependencyId(final String name, final String version) {
        if (StringUtils.isNotBlank(version)) {
            return new NameVersionDependencyId(name, version);
        } else {
            return new NameDependencyId(name);
        }
    }

    private void setDependencyInfo(final DependencyId dependencyId, final String name, final String version, final LazyExternalIdDependencyGraphBuilder lazyBuilder) {
        final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version);
        lazyBuilder.setDependencyInfo(dependencyId, name, version, externalId);
    }
}
