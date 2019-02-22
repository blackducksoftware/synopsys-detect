/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detectable.detectables.nuget.parse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetContainer;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetContainerType;
import com.synopsys.integration.detectable.detectables.nuget.model.NugetInspection;

public class NugetInspectorParser {
    private final Gson gson;
    private final ExternalIdFactory externalIdFactory;

    public NugetInspectorParser(final Gson gson, final ExternalIdFactory externalIdFactory) {
        this.gson = gson;
        this.externalIdFactory = externalIdFactory;
    }

    public NugetParseResult createCodeLocation(final String dependencyFileText) {
        final NugetInspection nugetInspection = gson.fromJson(dependencyFileText, NugetInspection.class);

        final List<CodeLocation> codeLocations = new ArrayList<>();
        String projectName = "";
        String projectVersion = "";
        for (final NugetContainer it : nugetInspection.containers) {
            final Optional<NugetParseResult> possibleParseResult = createDetectCodeLocationFromNugetContainer(it);
            if (possibleParseResult.isPresent()) {
                final NugetParseResult result = possibleParseResult.get();
                if (StringUtils.isNotBlank(result.projectName)) {
                    projectName = result.projectName;
                    projectVersion = result.projectVersion;
                }
                codeLocations.addAll(result.codeLocations);
            }
        }

        return new NugetParseResult(projectName, projectVersion, codeLocations);
    }

    private Optional<NugetParseResult> createDetectCodeLocationFromNugetContainer(final NugetContainer nugetContainer) {
        final NugetParseResult parseResult;
        String projectName = "";
        String projectVersionName = "";

        if (NugetContainerType.SOLUTION == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            final List<CodeLocation> codeLocations = new ArrayList<>();
            for (final NugetContainer container : nugetContainer.children) {
                final NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder(externalIdFactory);
                builder.addPackageSets(container.packages);
                final DependencyGraph children = builder.createDependencyGraph(container.dependencies);
                final String sourcePath = container.sourcePath;

                if (StringUtils.isBlank(projectVersionName)) {
                    projectVersionName = container.version;
                }
                final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.NUGET, children, externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName))
                                                            .build();
                codeLocations.add(codeLocation);
            }
            parseResult = new NugetParseResult(projectName, projectVersionName, codeLocations);
        } else if (NugetContainerType.PROJECT == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            final String sourcePath = nugetContainer.sourcePath;
            final NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder(externalIdFactory);
            builder.addPackageSets(nugetContainer.packages);
            final DependencyGraph children = builder.createDependencyGraph(nugetContainer.dependencies);

            final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.NUGET, children, externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName))
                                                        .build();
            parseResult = new NugetParseResult(projectName, projectVersionName, codeLocation);
        } else {
            parseResult = null;
        }

        return Optional.ofNullable(parseResult);
    }
}
