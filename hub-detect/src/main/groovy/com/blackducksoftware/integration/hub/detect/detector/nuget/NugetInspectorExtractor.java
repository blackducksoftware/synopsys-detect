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
package com.blackducksoftware.integration.hub.detect.detector.nuget;

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

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;

public class NugetInspectorExtractor {
    public static final String INSPECTOR_OUTPUT_PATTERN = "*_inspection.json";

    private final Logger logger = LoggerFactory.getLogger(NugetInspectorExtractor.class);

    private final DirectoryManager directoryManager;
    private final NugetInspectorPackager nugetInspectorPackager;
    private final ExecutableRunner executableRunner;
    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;

    public NugetInspectorExtractor(final DirectoryManager directoryManager, final NugetInspectorPackager nugetInspectorPackager, final ExecutableRunner executableRunner, final DetectFileFinder detectFileFinder,
        final DetectConfiguration detectConfiguration) {
        this.directoryManager = directoryManager;
        this.nugetInspectorPackager = nugetInspectorPackager;
        this.executableRunner = executableRunner;
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public Extraction extract(final File directory, String inspectorExe, final ExtractionId extractionId) {
        try {
            final File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);

            final List<String> options = new ArrayList<>(Arrays.asList(
                "--target_path=" + directory.toString(),
                "--output_directory=" + outputDirectory.getCanonicalPath(),
                "--ignore_failure=" + detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NUGET_IGNORE_FAILURE, PropertyAuthority.None)));

            final String nugetExcludedModules = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_EXCLUDED_MODULES, PropertyAuthority.None);
            if (StringUtils.isNotBlank(nugetExcludedModules)) {
                options.add("--excluded_modules=" + nugetExcludedModules);
            }
            final String nugetIncludedModules = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INCLUDED_MODULES, PropertyAuthority.None);
            if (StringUtils.isNotBlank(nugetIncludedModules)) {
                options.add("--included_modules=" + nugetIncludedModules);
            }
            final String[] nugetPackagesRepo = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL, PropertyAuthority.None);
            if (nugetPackagesRepo.length > 0) {
                final String packagesRepos = Arrays.asList(nugetPackagesRepo).stream().collect(Collectors.joining(","));
                options.add("--packages_repo_url=" + packagesRepos);
            }
            final String nugetConfigPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH, PropertyAuthority.None);
            if (StringUtils.isNotBlank(nugetConfigPath)) {
                options.add("--nuget_config_path=" + nugetConfigPath);
            }
            if (logger.isTraceEnabled()) {
                options.add("-v");
            }

            final Executable hubNugetInspectorExecutable = new Executable(directory, inspectorExe, options);
            final ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable);

            if (executableOutput.getReturnCode() != 0) {
                return new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", options), executableOutput.getReturnCode())).build();
            }

            final List<File> dependencyNodeFiles = detectFileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);

            final List<NugetParseResult> parseResults = new ArrayList<>();
            for (final File dependencyNodeFile : dependencyNodeFiles) {
                //TODO fix
                //directoryManager.registerFileOfInterest(extractionId, dependencyNodeFile);
                final NugetParseResult result = nugetInspectorPackager.createDetectCodeLocation(dependencyNodeFile);
                parseResults.add(result);
            }

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
                    logger.info("Multiple project code locations were generated for: " + directory.toString());
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
