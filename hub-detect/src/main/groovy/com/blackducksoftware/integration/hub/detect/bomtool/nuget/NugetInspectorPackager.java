/*
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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.google.gson.Gson;

public class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class);

    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NugetInspectorPackager(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NugetParseResult createDetectCodeLocation(final BomToolType bomToolType, final File dependencyNodeFile) throws IOException {
        final String text = FileUtils.readFileToString(dependencyNodeFile, StandardCharsets.UTF_8);
        final NugetInspection nugetInspection = gson.fromJson(text, NugetInspection.class);

        final List<DetectCodeLocation> codeLocations = new ArrayList<>();
        String projectName = "";
        String projectVersion = "";
        for (final NugetContainer it : nugetInspection.containers) {
            final NugetParseResult result = createDetectCodeLocationFromNugetContainer(bomToolType, it);
            if (StringUtils.isNotBlank(result.projectName)) {
                projectName = result.projectName;
                projectVersion = result.projectVersion;
            }
            codeLocations.addAll(result.codeLocations);
        }

        return new NugetParseResult(projectName, projectVersion, codeLocations);
    }

    private NugetParseResult createDetectCodeLocationFromNugetContainer(final BomToolType bomToolType, final NugetContainer nugetContainer) {
        String projectName = "";
        String projectVersionName = "";
        if (NugetContainerType.SOLUTION == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            final List<DetectCodeLocation> codeLocations = new ArrayList<>();
            for (final NugetContainer container : nugetContainer.children) {
                final NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder(externalIdFactory);
                builder.addPackageSets(container.packages);
                final DependencyGraph children = builder.createDependencyGraph(container.dependencies);
                final String sourcePath = container.sourcePath;

                if (StringUtils.isBlank(projectVersionName)) {
                    projectVersionName = container.version;
                }
                final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.NUGET, bomToolType, sourcePath, externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName), children)
                        .build();
                codeLocations.add(codeLocation);
            }
            return new NugetParseResult(projectName, projectVersionName, codeLocations);
        } else if (NugetContainerType.PROJECT == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            final String sourcePath = nugetContainer.sourcePath;
            final NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder(externalIdFactory);
            builder.addPackageSets(nugetContainer.packages);
            final DependencyGraph children = builder.createDependencyGraph(nugetContainer.dependencies);

            final DetectCodeLocation codeLocation = new DetectCodeLocation.Builder(BomToolGroupType.NUGET, bomToolType, sourcePath, externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName), children)
                    .build();
            return new NugetParseResult(projectName, projectVersionName, codeLocation);
        } else {
            return null;
        }
    }
}
