/**
 * synopsys-detect
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
package com.synopsys.integration.detectable.detectables.nuget;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetInspectorParser;
import com.synopsys.integration.detectable.detectables.nuget.parse.NugetParseResult;

public class NugetInspectorExtractor {
    public static final String INSPECTOR_OUTPUT_PATTERN = "*_inspection.json";

    private final Logger logger = LoggerFactory.getLogger(NugetInspectorExtractor.class);

    private final NugetInspectorParser nugetInspectorParser;
    private final FileFinder fileFinder;

    public NugetInspectorExtractor(final NugetInspectorParser nugetInspectorParser, final FileFinder fileFinder) {
        this.nugetInspectorParser = nugetInspectorParser;
        this.fileFinder = fileFinder;
    }

    public Extraction extract(final File targetDirectory, File outputDirectory, NugetInspector inspector, NugetInspectorOptions nugetInspectorOptions) {
        try {
            final ExecutableOutput executableOutput = inspector.execute(targetDirectory, nugetInspectorOptions);

            if (executableOutput.getReturnCode() != 0) {
                return new Extraction.Builder().failure(String.format("Executing the nuget inspector failed", executableOutput.getReturnCode())).build();
            }

            final List<File> dependencyNodeFiles = fileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);

            final List<NugetParseResult> parseResults = new ArrayList<>();
            for (final File dependencyNodeFile : dependencyNodeFiles) {
                final String text = FileUtils.readFileToString(dependencyNodeFile, StandardCharsets.UTF_8);
                final NugetParseResult result = nugetInspectorParser.createCodeLocation(text);
                parseResults.add(result);
            }

            final List<CodeLocation> codeLocations = parseResults.stream()
                                                               .flatMap(it -> it.codeLocations.stream())
                                                               .collect(Collectors.toList());

            if (codeLocations.size() <= 0) {
                logger.warn("Unable to extract any dependencies from nuget");
            }

            final Map<String, CodeLocation> codeLocationsBySource = new HashMap<>();
            final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            codeLocations.stream().forEach(codeLocation -> {
                final String sourcePathKey = codeLocation.getSourcePath().toLowerCase();
                if (codeLocationsBySource.containsKey(sourcePathKey)) {
                    logger.info("Multiple project code locations were generated for: " + targetDirectory.toString());
                    logger.info("This most likely means the same project exists in multiple solutions.");
                    logger.info("The code location's dependencies will be combined, in the future they will exist seperately for each solution.");
                    final CodeLocation destination = codeLocationsBySource.get(sourcePathKey);
                    combiner.addGraphAsChildrenToRoot((MutableDependencyGraph) destination.getDependencyGraph(), codeLocation.getDependencyGraph());
                } else {
                    codeLocationsBySource.put(sourcePathKey, codeLocation);
                }
            });

            final List<CodeLocation> uniqueCodeLocations = codeLocationsBySource.values().stream().collect(Collectors.toList());

            final Extraction.Builder builder = new Extraction.Builder().success(uniqueCodeLocations);
            final Optional<NugetParseResult> project = parseResults.stream().filter(it -> StringUtils.isNotBlank(it.projectName)).findFirst();
            if (project.isPresent()) {
                builder.projectName(project.get().projectName);
                builder.projectVersion(project.get().projectVersion);
            }
            return builder.build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
