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
package com.blackducksoftware.integration.hub.detect.bomtool.npm.parse;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.DependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.google.gson.Gson;

public class NpmLockfilePackager {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NpmLockfilePackager(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NpmParseResult parse(final String sourcePath, final String lockFileText, final boolean includeDevDependencies) {
        final NpmProject npmProject = gson.fromJson(lockFileText, NpmProject.class);

        final LazyExternalIdDependencyGraphBuilder lazyBuilder = new LazyExternalIdDependencyGraphBuilder();

        final List<DependencyId> alreadySetNames = new ArrayList<>();
        npmProject.dependencies.forEach((name, npmDependency) -> {
            if (shouldInclude(npmDependency, includeDevDependencies)) {
                setDependencyInfoIfVersionExists(name, npmDependency.version, alreadySetNames, lazyBuilder);
                final DependencyId dependency = createDependencyId(name, npmDependency.version);
                lazyBuilder.addChildToRoot(dependency);

                if (npmDependency.requires != null) {
                    npmDependency.requires.forEach((childName, childVersion) -> {
                        setDependencyInfoIfVersionExists(childName, childVersion, alreadySetNames, lazyBuilder);
                        final DependencyId child = createDependencyId(childName, childVersion);
                        lazyBuilder.addChildWithParent(child, dependency);
                    });
                }
            }
        });

        final DependencyGraph graph = lazyBuilder.build();
        final ExternalId projectId = externalIdFactory.createNameVersionExternalId(Forge.NPM, npmProject.name, npmProject.version);
        final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.NPM, sourcePath, projectId, graph).build();
        return new NpmParseResult(npmProject.name, npmProject.version, codeLocation);
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
        return new NameDependencyId(name);
        /*
         * if (StringUtils.isNotBlank(version)) { return new NameVersionDependencyId(name, version); } else { return new NameDependencyId(name); }
         */
    }

    private void setDependencyInfoIfVersionExists(final String name, final String version, final List<DependencyId> alreadySetNames, final LazyExternalIdDependencyGraphBuilder lazyBuilder) {
        final DependencyId nameDependencyId = new NameDependencyId(name);
        if (StringUtils.isNotBlank(version)) {
            final ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.NPM, name, version);
            // lazyBuilder.setDependencyInfo(createDependencyId(name, version), name, version, externalId);
            if (!alreadySetNames.contains(nameDependencyId)) {
                lazyBuilder.setDependencyInfo(nameDependencyId, name, version, externalId);
                alreadySetNames.add(nameDependencyId);
            }
        }
    }
}
