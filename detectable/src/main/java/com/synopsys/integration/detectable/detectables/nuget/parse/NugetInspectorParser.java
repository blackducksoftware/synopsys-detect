/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
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
                if (StringUtils.isNotBlank(result.getProjectName())) {
                    projectName = result.getProjectName();
                    projectVersion = result.getProjectVersion();
                }
                codeLocations.addAll(result.getCodeLocations());
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
                if (StringUtils.isBlank(projectVersionName)) {
                    projectVersionName = container.version;
                }

                final CodeLocation codeLocation = new CodeLocation(children, externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName), convertSourcePath(container.sourcePath));
                codeLocations.add(codeLocation);
            }
            parseResult = new NugetParseResult(projectName, projectVersionName, codeLocations);
        } else if (NugetContainerType.PROJECT == nugetContainer.type) {
            projectName = nugetContainer.name;
            projectVersionName = nugetContainer.version;
            final NugetDependencyNodeBuilder builder = new NugetDependencyNodeBuilder(externalIdFactory);
            builder.addPackageSets(nugetContainer.packages);
            final DependencyGraph children = builder.createDependencyGraph(nugetContainer.dependencies);

            final CodeLocation codeLocation = new CodeLocation(children, externalIdFactory.createNameVersionExternalId(Forge.NUGET, projectName, projectVersionName), convertSourcePath(nugetContainer.sourcePath));
            parseResult = new NugetParseResult(projectName, projectVersionName, codeLocation);
        } else {
            parseResult = null;
        }

        return Optional.ofNullable(parseResult);
    }

    private File convertSourcePath(final String sourcePath) {//TODO: Seem to be getting a relative path for nuget... not sure where to look, something like "folder/./project/"
        File fileSourcePath = null;
        if (StringUtils.isNotBlank(sourcePath)) {
            fileSourcePath = new File(sourcePath);
        }

        return fileSourcePath;
    }
}
