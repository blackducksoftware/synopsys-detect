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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.parse.NugetInspectorPackager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.nuget.parse.NugetParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class NugetInspectorExtractor extends Extractor<NugetInspectorContext> {
    static final String INSPECTOR_OUTPUT_PATTERN = "*_inspection.json";
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorExtractor.class);
    @Autowired
    public DetectFileManager detectFileManager;
    @Autowired
    NugetInspectorPackager nugetInspectorPackager;
    @Autowired
    private DetectConfiguration detectConfiguration;
    @Autowired
    private ExecutableRunner executableRunner;
    @Autowired
    private DetectFileFinder detectFileFinder;

    @Override
    public Extraction extract(final NugetInspectorContext context) {

        try {
            final File outputDirectory = detectFileManager.getOutputDirectory(context);

            final List<String> options = new ArrayList<>(Arrays.asList(
                    "--target_path=" + context.directory.toString(),
                    "--output_directory=" + outputDirectory.getCanonicalPath(),
                    "--ignore_failure=" + detectConfiguration.getNugetInspectorIgnoreFailure()
                    ));

            if (detectConfiguration.getNugetInspectorExcludedModules() != null) {
                options.add("--excluded_modules=" + detectConfiguration.getNugetInspectorExcludedModules());
            }
            if (detectConfiguration.getNugetInspectorIncludedModules() != null) {
                options.add("--included_modules=" + detectConfiguration.getNugetInspectorIncludedModules());
            }
            if (detectConfiguration.getNugetPackagesRepoUrl() != null) {
                final String packagesRepos = Arrays.asList(detectConfiguration.getNugetPackagesRepoUrl()).stream().collect(Collectors.joining(","));
                options.add("--packages_repo_url=" + packagesRepos);
            }
            if (StringUtils.isNotBlank(detectConfiguration.getNugetConfigPath())) {
                options.add("--nuget_config_path=" + detectConfiguration.getNugetConfigPath());
            }
            if (logger.isTraceEnabled()) {
                options.add("-v");
            }

            final Executable hubNugetInspectorExecutable = new Executable(context.directory, context.inspectorExe, options);
            final ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable);

            if (executableOutput.getReturnCode() != 0) {
                return new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", options), executableOutput.getReturnCode())).build();
            }

            final List<File> dependencyNodeFiles = detectFileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);
            final List<NugetParseResult> parseResults = dependencyNodeFiles.stream()
                    .map(it -> nugetInspectorPackager.createDetectCodeLocation(it))
                    .collect(Collectors.toList());

            final List<DetectCodeLocation> codeLocations = parseResults.stream()
                    .flatMap(it -> it.codeLocations.stream())
                    .collect(Collectors.toList());

            if (codeLocations.size() <= 0) {
                logger.warn("Unable to extract any dependencies from nuget");
            }

            final Map<String, DetectCodeLocation> codeLocationsBySource = new HashMap<>();
            final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            codeLocations.stream().forEach(codeLocation -> {
                final String sourcePathKey = codeLocation.getSourcePath().toLowerCase();
                if (codeLocationsBySource.containsKey(sourcePathKey)) {
                    logger.info("Multiple project code locations were generated for: " + context.directory.toString());
                    logger.info("This most likely means the same project exists in multiple solutions.");
                    logger.info("The code location's dependencies will be combined, in the future they will exist seperately for each solution.");
                    final DetectCodeLocation destination = codeLocationsBySource.get(sourcePathKey);
                    combiner.addGraphAsChildrenToRoot((MutableDependencyGraph) destination.getDependencyGraph(), codeLocation.getDependencyGraph());
                } else {
                    codeLocationsBySource.put(sourcePathKey, codeLocation);
                }
            });

            final List<DetectCodeLocation> uniqueCodeLocations = codeLocationsBySource.values().stream().collect(Collectors.toList());

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
